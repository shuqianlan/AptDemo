package com.example.lib;

import com.google.auto.service.AutoService;
import com.wuzh.lib.InjectView;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
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

		// 代表程序的元素， 例如类，包，方法
		mElementsUtils = processingEnvironment.getElementUtils();
		// 创建文件
		mFiler = processingEnvironment.getFiler();
		// 错误处理工具
		mMessager = processingEnvironment.getMessager();
		// 处理TypeMirror的工具类，用于取类信息
		mTypeUtils = processingEnvironment.getTypeUtils();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(InjectView.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(InjectView.class);
		mMessager.printMessage(Diagnostic.Kind.NOTE, "size=" + elements.size());
		if (elements.size() == 0) {
			return true;
		}

		Map<Element, List<Element>> elementListMap = new HashMap<>();
		StringBuffer buffer = new StringBuffer();
		buffer.append("package com.example.lib;\n")
						.append("public class " + INJECTOR_NAME + "{\n");

		//遍历所有被InjectView注释的元素
		for(Element element:elements) {
			if (element.getKind() != ElementKind.FIELD) {
				mMessager.printMessage(Diagnostic.Kind.ERROR, "is not a FIELD", element);
			}

			if (!isView(element.asType())) {
				mMessager.printMessage(Diagnostic.Kind.ERROR, "is not a View", element);
			}

			//获取所在类的信息
			Element clazz = element.getEnclosingElement();
			addElement(elementListMap, clazz, element);
		}

		for(Map.Entry<Element, List<Element>> entry:elementListMap.entrySet()) {
			Element clazz = entry.getKey();
			//获取类名
			String className = clazz.getSimpleName().toString();

			//获取所在的包名
			String packageName = mElementsUtils.getPackageOf(clazz).asType().toString();

			//生成注入代码
			generateInjectorCode(packageName, className, entry.getValue());

			//完整类名
			String fullName = clazz.asType().toString();

			buffer.append("\tpublic static void inject(" + fullName + " arg) {\n")
							.append("\t\t" + fullName + GEN_CLASS_SUFFIX + ".inject(arg);\n")
							.append("\t}\n");
		}

		buffer.append("}");
		generateCode(INJECTOR_NAME, buffer.toString());

		return true;
	}

	private boolean isView(TypeMirror type) {
		List<? extends TypeMirror> supers = mTypeUtils.directSupertypes(type);
		if (supers.size() == 0) {
			return false;
		}

		for (TypeMirror superType:supers) {
			mMessager.printMessage(Diagnostic.Kind.WARNING, "class " + superType.toString());
			if (superType.toString().equals("android.view.View") || isView(superType)) {
				return true;
			}
		}

		return false;
	}

	private void addElement(Map<Element, List<Element>> map, Element clazz, Element field) {
		List<Element> list = map.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			map.put(clazz, list);
		}
		list.add(field);
	}

	/**
	 * 生成注入代码
	 *
	 * @param packageName 包名
	 * @param className   类名
	 * @param views       需要注入的成员变量
	 */
	private void generateInjectorCode(String packageName, String className, List<Element> views) {
		StringBuilder builder = new StringBuilder();
		builder.append("package " + packageName + ";\n\n")
						.append("public class " + className + GEN_CLASS_SUFFIX + " {\n")
						.append("\tpublic static void inject(" + className + " arg) {\n");

		for (Element element : views) {
			//获取变量类型
			String type = element.asType().toString();

			//获取变量名
			String name = element.getSimpleName().toString();

			//id
			int resourceId = element.getAnnotation(InjectView.class).value();

			builder.append("\t\targ." + name + "=(" + type + ")arg.findViewById(" + resourceId + ");\n");
		}

		builder.append("\t}\n")
						.append("}");

		//生成代码
		generateCode(className + GEN_CLASS_SUFFIX, builder.toString());
	}

	private void generateCode(String className, String code) {
		try {
			JavaFileObject file = mFiler.createSourceFile(className);
			Writer writer = file.openWriter();
			writer.write(code);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
