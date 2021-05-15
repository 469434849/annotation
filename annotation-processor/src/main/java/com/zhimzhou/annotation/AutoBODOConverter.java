package com.zhimzhou.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE) //注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃
@Target(ElementType.TYPE)
public @interface AutoBODOConverter {

	//DO class
	Class<?> value();
}
