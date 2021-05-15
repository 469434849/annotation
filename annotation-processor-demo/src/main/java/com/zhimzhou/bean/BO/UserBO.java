package com.zhimzhou.bean.BO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zhimzhou.annotation.AutoBODOConverter;
import com.zhimzhou.bean.DO.UserDO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AutoBODOConverter(UserDO.class)
public class UserBO {

	private Long id;

	private String name;

	private Integer age;

	private LocalDateTime create;

	private LocalDate update;
}
