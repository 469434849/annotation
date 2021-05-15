package com.zhimzhou.bean.DO;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDO implements Serializable {

	private Long id;

	private String name;

	private Integer age;

	private Long create;

	private Long update;

	public String b(int a) {
		return "c";
	}

}
