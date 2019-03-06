package com.example.lib;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wuzh.lib.AutoCreat;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class CustomProcessor extends AbstractProcessor {

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);
	}

	// 指定处理的注解,需要将要处理的注解的全名放到Set中返回
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(AutoCreat.class.getCanonicalName());
	}

	// 指定支持的Java版本.
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return super.getSupportedSourceVersion();
	}

	// 实际处理注解的地方
	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		MethodSpec main = MethodSpec.methodBuilder("main")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.returns(void.class)
						.addParameter(String[].class, "args")
						.addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
						.build();

		TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
						.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
						.addMethod(main)
						.build();

		JavaFile javaFile = JavaFile.builder("com.ilifesmart.aptdemo", helloWorld)
						.build();

		try {
			javaFile.writeTo(processingEnv.getFiler());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
