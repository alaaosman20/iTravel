/*************************************************************************
 *  
 *  Copyright (C) 2013 SuZhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date: 2013-11-14 上午11:23:54
 * 
 * @version: V1.0
 * 
 * @description:
 * 
 */
public class StringUtils {
	public static boolean isMobilePhone(String phone) {//
		Pattern pattern = Pattern
				.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
	public static boolean isBlank(String str) {
		boolean isBlank = false;
		if (null == str || str.equals("") || str.equals("null")
				|| str.trim().length() == 0) {
			isBlank = true;
		}
		return isBlank;
	}
	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		boolean isEmpty = false;
		if (null == str || str.equals("") || str.equals("null") || str.trim().length() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	/**
	 * 判断非空字段是否相同
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEqual(String str1, String str2) {
		boolean isEqual = false;
		if (!isEmpty(str1) && !isEmpty(str2)) {
			if (str1.equals(str2)) {
				isEqual = true;
			}
		}
		return isEqual;
	}

	/**
	 * 判断是否是手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	//判断是否为电话号码
	public static boolean isHomePhoneNumber(String mobiles) {
		 String phone = "0\\d{2,3}\\d{7,8}"; 
		 Pattern p = Pattern.compile(phone);  
		 Matcher m = p.matcher(mobiles);
		 return m.matches();
	}
	
	//对字符串进行分割
public static List<String> getString(String string,List<String> descimg){
				String[]str=string.split("\\|");
				for(int i=0;i<str.length;i++){
					descimg.add(str[i]);
				}
				return descimg;
			}
}
