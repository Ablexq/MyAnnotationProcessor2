
# 创建 annotation 的 Java 模块并配置

新建注解：
``` 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Test {
}
```
build.gradle:
``` 
apply plugin: 'java-library'

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
}

sourceCompatibility = "7"
targetCompatibility = "7"

```

# 创建 annotationCompiler 的 Java 模块并配置

```
@AutoService(Processor.class)//必须
public class TestProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //指定需要处理的注解
        return Collections.singleton(Test.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //指定生成方法
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();
        //指定生成类
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
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
```

build.gradle:

``` 
apply plugin: 'java-library'

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    //注解 processor 类，并对其生成 META-INF 的配置信息
    implementation 'com.google.auto.service:auto-service:1.0-rc2'
    //生成代码工具
    implementation 'com.squareup:javapoet:1.8.0'
    //依赖注解
    implementation project(':annotation')
}

sourceCompatibility = "7"
targetCompatibility = "7"

```

# 配置 app 的 android 模块

build.gradle:

``` 
apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.example.myannotationprocessor"
        minSdkVersion 15
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    //依赖注解
    implementation project(':annotation')
    //依赖注解处理器
    annotationProcessor project(':compiler')
}

```
使用注解：
``` 
@Test
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```
# rebuild项目：

在app模块的 build/generated/source/apt/debug 可找到生成的类：

![](imgs/annotationprocessor.png)








