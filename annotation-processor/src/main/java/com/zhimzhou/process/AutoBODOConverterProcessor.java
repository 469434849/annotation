package com.zhimzhou.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
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
	private Elements elementUtils;
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
		elementUtils = processingEnv.getElementUtils();
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
		//te 是注解 AutoBODOConverter
		for (TypeElement annotationTe : annotations) {
			//Element 表示 package, class, or method,
			//elements 这里表示所有加了 AutoBODOConverter 的类
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotationTe);
			//			elements.stream()
			//							.filter(TypeElement.class::isInstance)
			//							.map(TypeElement.class::cast)
			//							.map(TypeElement::getQualifiedName)
			//							.map(name -> "2222Class " + name + " is annotated with " + annotationTe.getQualifiedName())
			//							.forEach(System.out::println);
			for (Element element : elements) {
				AutoBODOConverter bodo = element.getAnnotation(AutoBODOConverter.class);
				String generatedClassName = bodo.generatedClassName();
				String generatedFilePackageName = bodo.generatedFilePackageName();
				String targetClassQualifiedName = "";
				System.out.println(annotationTe.getQualifiedName());
				try {
					// 该目标类已经被编译
					Class<?> targetClass = bodo.targetClass();
					targetClassQualifiedName = targetClass.getCanonicalName();
				} catch (MirroredTypeException mte) {
					//该目标类未被编译
					DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
					TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
					targetClassQualifiedName = classTypeElement.getQualifiedName().toString();
				}

				element.asType();

				String currentQualifiedName = element.asType().toString();
				System.out.println("currentQualifiedName:" + currentQualifiedName);
				System.out.println("targetClassQualifiedName:" + targetClassQualifiedName);
				System.out.println("generatedClassName:" + generatedClassName);
				System.out.println("generatedFilePackageName:" + generatedFilePackageName);
				TypeElement targetClassTE = elementUtils.getTypeElement(targetClassQualifiedName);
				List<? extends Element> targetClassTEEnclosedElements = targetClassTE.getEnclosedElements();
				for (Element e : targetClassTEEnclosedElements) {
					ElementKind kind = e.getKind();
					if (kind == ElementKind.FIELD) {
						String fieldName = e.getSimpleName().toString();
						String getMethod = "get" + Utils.firstLetterName(fieldName);
						String setMethod = "set" + Utils.firstLetterName(fieldName);
						System.out.println(getMethod);
						System.out.println(setMethod);
					}
					System.out.println("target e:" + e);
				}

				List<? extends Element> currentClosedElements = element.getEnclosedElements();

				for (Element e : currentClosedElements) {
					ElementKind kind = e.getKind();
					if (kind == ElementKind.FIELD) {
						String fieldName = e.getSimpleName().toString();
						String getMethod = "get" + Utils.firstLetterName(fieldName);
						String setMethod = "set" + Utils.firstLetterName(fieldName);
						System.out.println(getMethod);
						System.out.println(setMethod);
					}
					System.out.println("current e:" + e);
				}

				generateCode(currentQualifiedName, targetClassQualifiedName, generatedClassName, generatedFilePackageName);

			}

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

	public void generateCode(String aQualifiedClassName, String bQualifiedClassName, String generatedClassName, String generatedFilePackageName) {
		System.out.println(aQualifiedClassName);
		System.out.println(bQualifiedClassName);
		System.out.println(generatedFilePackageName);
		TypeElement aE = elementUtils.getTypeElement(aQualifiedClassName);
		TypeElement bE = elementUtils.getTypeElement(bQualifiedClassName);
		List<? extends Element> aees = aE.getEnclosedElements();
		List<? extends Element> bees = bE.getEnclosedElements();
		if (aees.size() != bees.size()) {
			throw new IllegalStateException(aQualifiedClassName + "\t and " + bQualifiedClassName + "  attributes count are not equals");
		}

		Set<String> aFields = aees.stream().filter(e -> e.getKind().equals(ElementKind.FIELD)).map(e -> e.getSimpleName().toString()).collect(Collectors.toSet());
		Set<String> bFields = bees.stream().filter(e -> e.getKind().equals(ElementKind.FIELD)).map(e -> e.getSimpleName().toString()).collect(Collectors.toSet());
		if (!aFields.containsAll(bFields)) {
			throw new IllegalStateException(aQualifiedClassName + "\t and " + bQualifiedClassName + " has not equal attribute");
		}

		MethodSpec.Builder toMethod = MethodSpec.methodBuilder("to")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.addParameter(TypeName.get(aE.asType()), "data")
						.returns(TypeName.get(bE.asType()));

		toMethod.beginControlFlow("if (data == null)")
						.addStatement("return null")
						.endControlFlow();
		toMethod.addStatement("$L instance = new $L()", bE.getSimpleName().toString(), bE.getSimpleName().toString())
						.addStatement("return instance");

		MethodSpec.Builder fromMethod = MethodSpec.methodBuilder("from")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.addParameter(TypeName.get(bE.asType()), "data")
						.returns(TypeName.get(aE.asType()));

		fromMethod.beginControlFlow("if (data == null)")
						.addStatement("return null")
						.endControlFlow();

		fromMethod.addStatement("return new $L()", aE.getSimpleName().toString());

		TypeSpec typeSpec = TypeSpec
						.classBuilder(generatedClassName)
						.addModifiers(Modifier.PUBLIC)
						.addMethod(toMethod.build())
						.addMethod(fromMethod.build())
						.build();

		// Write file
		try {
			JavaFile.builder(generatedFilePackageName, typeSpec).build().writeTo(filer);
			//			JavaFile.builder(generatedFilePackageName, typeSpec).build().writeTo(System.out);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//校验注解添加类的信息
	//	private void checkValidClass(AutoBODOConverterAnnotatedClass item) {
	//		// Cast to TypeElement, has more type specific methods
	//		TypeElement classElement = item.getMAnnotatedClassElement();
	//		// Check if it's a public class
	//		if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
	//			error(classElement, "The class %s is not public.",
	//							classElement.getQualifiedName().toString());
	//		}
	//
	//		// Check if it's an abstract class
	//		if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
	//			error(classElement,
	//							"The class %s is abstract. You can't annotate abstract classes with @%"
	//							, Factory.class.getSimpleName());
	//		}
	//
	//		// Check inheritance: Class must be child class as specified in @Factory.type();
	//		TypeElement superClassElement = mElementUtils.getTypeElement(item.getMQualifiedSuperClassName());
	//		if (superClassElement.getKind() == ElementKind.INTERFACE) {
	//			// Check interface implemented
	//			if (!classElement.getInterfaces().contains(superClassElement.asType())) {
	//				error(classElement,
	//								"The class %s annotated with @%s must implement the interface %s",
	//								classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
	//								item.getMQualifiedSuperClassName());
	//			}
	//		} else {
	//			// Check subclassing
	//			TypeElement currentClass = classElement;
	//			while (true) {
	//				/**
	//				 * getSuperclass()
	//				 * Returns the direct superclass of this type element.
	//				 * If this type element represents an interface or the class java.lang.Object,
	//				 * then a NoType with kind NONE is returned.
	//				 */
	//				TypeMirror superClassType = currentClass.getSuperclass();
	//
	//				if (superClassType.getKind() == TypeKind.NONE) {
	//					// Basis class (java.lang.Object) reached, so exit
	//					error(classElement,
	//									"The class %s annotated with @%s must inherit from %s",
	//									classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
	//									item.getMQualifiedSuperClassName());
	//				}
	//
	//				if (superClassType.toString().equals(item.getMQualifiedSuperClassName())) {
	//					// Required super class found
	//					break;
	//				}
	//
	//				// Moving up in inheritance tree
	//				currentClass = (TypeElement) mTypeUtils.asElement(superClassType);
	//			}
	//		}
	//
	//		// Check if an empty public constructor is given
	//		for (Element enclosed : classElement.getEnclosedElements()) {
	//			if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
	//				ExecutableElement constructorElement = (ExecutableElement) enclosed;
	//				if (constructorElement.getParameters().size() == 0 &&
	//								constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
	//					// Found an empty constructor
	//					return;
	//				}
	//			}
	//		}
	//
	//		// No empty constructor found
	//		error(classElement,
	//						"The class %s must provide an public empty default constructor",
	//						classElement.getQualifiedName().toString());
	//	}

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
