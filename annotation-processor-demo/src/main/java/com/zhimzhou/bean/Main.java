package com.zhimzhou.bean;

import java.time.LocalDateTime;

import com.zhimzhou.bean.BO.UserBO;

public class Main {
	public static void main(String[] args) {
		UserBO userBO = new UserBO();
		userBO.getAge();
		System.out.println(LocalDateTime.now());
	}
}
