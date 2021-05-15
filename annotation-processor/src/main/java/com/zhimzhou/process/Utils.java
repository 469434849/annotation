package com.zhimzhou.process;

import com.sun.tools.javac.util.Assert;

public class Utils {

	public static String firstLetterName(String s) {
		char[] cs = s.toCharArray();
		Assert.check(cs[0] >= 'a' && cs[0] <= 'z', "variable (" + s + ") first letter must between 'a' and 'z' ");
		cs[0] -= 32;
		return String.valueOf(cs);
	}

}
