package com.zhimzhou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解可以注释在需要相互转化的类中。必须有相同的getter，setter方法名称。参数不同也行。个数必须一致
 * 可以写在BO上  也可以写在DO上。建议写在BO上
 */
@Retention(RetentionPolicy.SOURCE) //注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃
@Target(ElementType.TYPE)
public @interface AutoBODOConverter {

	//相互转化的类
	Class<?> targetClass();

	//生成文件的包名
	String generatedFilePackageName();

	//生成文件的类名
	String generatedClassName();
}
