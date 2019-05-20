package com.example.compiler;

import com.example.annotation.Test;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)//必须
public class TestProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //指定需要处理的注解
        return Collections.singleton(Test.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(Test.class);
//        for (Element element : elementSet) {
//            switch (element.getKind()) {//判断注解类型
//                case ENUM:
//                case CLASS:
//                case INTERFACE:
//                    TypeElement typeElement = (TypeElement) element;
//                    List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);
//
//                    for (Element item : allMembers) {
//                        Test annotation = item.getAnnotation(Test.class);
//                    }
//
//                    break;
//                case FIELD:
//                    break;
//                case METHOD:
//                    break;
//            }
//        }
        //指定生成方法
        MethodSpec main = MethodSpec.methodBuilder("main")//方法名
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//修饰符
                .returns(void.class)//返回值
                .addParameter(String[].class, "args")//参数
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")//语句
                .build();
        //指定生成类
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")//类名
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//修饰符
                .addMethod(main)//方法
                .build();
        //指定生成Java文件
        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
