/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.meishi.front.ext.weixin.api;


import com.jfinal.kit.HttpKit;
import com.meishi.front.ext.weixin.kit.ParaMap;

import java.io.IOException;
import java.util.Map;

/**
 * 认证并获取 access_token API
 * http://mp.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96access_token
 */
public class AccessTokenApi {
	
	// "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
	
	private static AccessToken accessToken;
	
	public static AccessToken getAccessToken(String appId,String appSecret) {
//		if (accessToken != null && accessToken.isAvailable())
//			return accessToken;
		
		for (int i=0; i<3; i++) {
			accessToken = requestAccesToken(appId,appSecret);
			if (accessToken.isAvailable())
				break;
		}
		return accessToken;
	}
	
	private static AccessToken requestAccesToken(String appId,String appSecret) {
		Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", appSecret).getData();
		String json = HttpKit.get(url, queryParas);
		return new AccessToken(json);
	}
	
	public static void main(String[] args) throws  IOException {
//		ApiConfig.setAppId("wx9803d1188fa5fbda");
//		ApiConfig.setAppSecret("db859c968763c582794e7c3d003c3d87");
//
     	AccessToken at = getAccessToken("wx5e138dda1ee276f7","4e5334dbf462fd059824d2b0e09690bc");
		if (at.isAvailable())
			System.out.println("access_token : " + at.getAccessToken());
		else
			System.out.println(at.getErrorCode() + " : " + at.getErrorMsg());
	}
}
