package com.zhimzhou.process;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.springframework.util.StringUtils;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zhimzhou.annotation.AutoBODOConverter;

public class AnnotationProcessorHider {
	public static class MyAnnotationProcessor extends AbstractProcessor {

		@Override
		public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
			for (Element element : roundEnv.getElementsAnnotatedWith(AutoBODOConverter.class)) {
				processAnnotation(element);
			}
			return true;
		}

		private void processAnnotation(Element element) {
			TypeMirror typeMirror = element.asType();
			build(element);
		}
	}

	public static void build(Element element) {
		String property = System.getProperty("user.dir");
		System.out.println(property);

		FieldSpec serialVersionUID = FieldSpec.builder(TypeName.LONG, "serialVersionUID",
						Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
						.initializer(CodeBlock.of("$L", System.currentTimeMillis() + "L"))
						.build();

		MethodSpec main = MethodSpec.methodBuilder("main")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.returns(void.class)
						.addParameter(String[].class, "args")
						.addStatement("$T.out.println($S)", System.class, element.toString())
						.addStatement("$T.isEmpty($S)", StringUtils.class, ArrayList.class)
						.build();

		TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
						.addSuperinterface(Serializable.class)
						.addModifiers(Modifier.PUBLIC)
						.addField(serialVersionUID)
						.addMethod(main)
						.build();

		JavaFile javaFile = JavaFile.builder("com.zhimzhou.annotation", helloWorld).build();

		try {
			String fileName = "/Users/zhimzhou/Desktop";
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			javaFile.writeTo(file);
			javaFile.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
