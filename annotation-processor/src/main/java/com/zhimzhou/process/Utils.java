package com.zhimzhou.process;

public class Utils {

	public static String firstLetterName(String s) {
		char[] cs = s.toCharArray();
		if (!(cs[0] >= 'a' && cs[0] <= 'z')) {
			throw new IllegalStateException("variable (" + s + ") first letter must between 'a' and 'z' ");
		}
		cs[0] -= 32;
		return String.valueOf(cs);
	}

}
