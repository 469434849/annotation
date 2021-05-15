package com.zhimzhou.bean.BO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zhimzhou.annotation.AutoBODOConverter;
import com.zhimzhou.bean.DO.UserDO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AutoBODOConverter(targetClass = UserDO.class, generatedFilePackageName = "com.zhimzhou.bean.converter", generatedClassName = "UserBODOConverter")
public class UserBO implements Serializable {

	private Long id;

	private String name;

	private Integer age;

	private LocalDateTime create;

	private LocalDate update;

	public void a() {
		System.out.println(1);
	}
}
