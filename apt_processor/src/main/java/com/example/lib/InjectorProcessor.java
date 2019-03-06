package com.example.lib;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes({"com.wuzh.lib.InjectView"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InjectorProcessor extends AbstractProcessor {

	private static final String GEN_CLASS_SUFFIX = "Injector";
	private static final String INJECTOR_NAME = "ViewInjector";

	private Elements mElementsUtils;
	private Filer mFiler;
	private Messager mMessager;
	private Types mTypeUtils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);

		mElementsUtils = processingEnvironment.getElementUtils();
		mFiler = processingEnvironment.getFiler();
		mMessager = processingEnvironment.getMessager();
		mTypeUtils = processingEnvironment.getTypeUtils();
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		return false;
	}
}
