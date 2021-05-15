package com.zhimzhou.process;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

import com.zhimzhou.annotation.AutoBODOConverter;

/**
 * 使用AutoBODOConverter 注释类的信息
 */
public class AutoBODOConverterAnnotatedClass {

	/**
	 * 类信息
	 */
	private TypeElement annotatedClassElement;
	/**
	 * 注释类的全类名
	 */
	private String qualifiedSuperClassName;

	/**
	 * 转化目标类全类名
	 */
	private String targetClassQualifiedSuperClassName;

	/**
	 * 生成转化类的包名
	 */
	private String generatedFilePackageName;

	/**
	 * 生成转化类的类名
	 */
	private String generatedClassName;

	public AutoBODOConverterAnnotatedClass(TypeElement classElement) {
		this.annotatedClassElement = classElement;
		this.qualifiedSuperClassName = classElement.getQualifiedName().toString();
		AutoBODOConverter annotation = classElement.getAnnotation(AutoBODOConverter.class);
		try {
			// 该目标类已经被编译
			Class<?> clazz = annotation.targetClass();
			this.targetClassQualifiedSuperClassName = clazz.getCanonicalName();
		} catch (MirroredTypeException mte) {
			//该目标类未被编译
			DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
			TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
			this.targetClassQualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
		}

		this.generatedFilePackageName = annotation.generatedFilePackageName();

	}

}
