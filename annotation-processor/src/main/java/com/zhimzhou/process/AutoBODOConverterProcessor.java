package com.zhimzhou.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zhimzhou.annotation.AutoBODOConverter;

@SupportedAnnotationTypes("com.zhimzhou.annotation.AutoBODOConverter")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class AutoBODOConverterProcessor extends AbstractProcessor {

	/**
	 * 文件相关的辅助类
	 */
	private Filer filer;
	/**
	 * 元素相关的辅助类
	 */
	private Elements elements;
	/**
	 * 元素相关的辅助类
	 */
	private Types types;
	/**
	 * 日志相关的辅助类
	 */
	private Messager messager;
	/**
	 * 解析的目标注解集合
	 */
	private Map<String, AnnotatedClass> annotatedClassMap = new HashMap<>();

	private static final String CONVERTER_SUFFIX = "converter";

	@Override
	public Set<String> getSupportedOptions() {
		return super.getSupportedOptions();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new LinkedHashSet<>();
		types.add(AutoBODOConverter.class.getCanonicalName());//返回该注解处理器支持的注解集合
		return types;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		elements = processingEnv.getElementUtils();
		messager = processingEnv.getMessager();
		filer = processingEnv.getFiler();
		types = processingEnv.getTypeUtils();
	}

	@Override
	public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
		return super.getCompletions(element, annotation, member, userText);
	}

	@Override
	protected synchronized boolean isInitialized() {
		return super.isInitialized();
	}

	//annotations 是@SupportedAnnotationTypes 里面的注解。代码里面使用的。没使用读取不到
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		//TypeElement 表示 class or interface
		for (TypeElement te : annotations) {
			//Element 表示 package, class, or method
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(te);
			elements.stream()
							.filter(TypeElement.class::isInstance)
							.map(TypeElement.class::cast)
							.map(TypeElement::getQualifiedName)
							.map(name -> "2222Class " + name + " is annotated with " + te.getQualifiedName())
							.forEach(System.out::println);

			this.generateCode(this.elements, this.filer, te.getQualifiedName().toString());

		}
		//		try {
		//			String filename = LocalDateTime.now() + ".txt";
		//			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/zhimzhou/Desktop/1/" + filename));
		//			bw.write(LocalDateTime.now().toString());
		//			bw.close();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		return true;
	}

	/**
	 * @param elements           元素处理工具
	 * @param filer              生成源代码文件工具
	 * @param qualifiedClassName 全类名
	 * @throws IOException
	 */
	public void generateCode(Elements elements, Filer filer, String qualifiedClassName) {
		System.out.println("begin1111:");
		TypeElement superClassName = elements.getTypeElement(qualifiedClassName);
		String factoryClassName = superClassName.getSimpleName() + CONVERTER_SUFFIX;
		String qualifiedFactoryClassName = qualifiedClassName + CONVERTER_SUFFIX;
		PackageElement pkg = elements.getPackageOf(superClassName);
		String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

		MethodSpec.Builder method = MethodSpec.methodBuilder("create")
						.addModifiers(Modifier.PUBLIC)
						.addParameter(String.class, "id")
						.returns(TypeName.get(superClassName.asType()));

		// check if id is null
		method.beginControlFlow("if (id == null)")
						.addStatement("throw new IllegalArgumentException($S)", "id is null!")
						.endControlFlow();

		// Generate items map

		//		for (FactoryAnnotatedClass item : itemsMap.values()) {
		//			method.beginControlFlow("if ($S.equals(id))", item.getMId())
		//							.addStatement("return new $L()", item.getMAnnotatedClassElement().getQualifiedName().toString())
		//							.endControlFlow();
		//		}

		method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

		TypeSpec typeSpec = TypeSpec
						.classBuilder(factoryClassName)
						.addModifiers(Modifier.PUBLIC)
						.addMethod(method.build())
						.build();

		// Write file
		try {
			JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
			JavaFile.builder(packageName, typeSpec).build().writeTo(System.out);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}

	private void error(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
	}

	private void info(String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
	}

}
