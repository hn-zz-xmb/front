/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.meishi.front.ext.weixin.msg;

import com.meishi.front.ext.weixin.msg.out.*;
import com.meishi.util.WebUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 利用 FreeMarker 动态生成 OutMsg xml 内容 
 */
public class OutMsgXmlBuilderByBeetl {
	
	private static String encoding = "utf-8";

	public static String build(OutMsg outMsg){
		if (outMsg == null)
			throw new IllegalArgumentException("参数 OutMsg 不能为 null");

		Map root = new HashMap();
		// 供 OutMsg 里的 TEMPLATE 使用
		root.put("__msg", outMsg);

		try {
			// text 文本消息
//		// news 图文消息
//		// image 图片消息
//		//voice 语音消息
//		// video 视频消息
//		// music 音乐消息
			if(outMsg instanceof OutTextMsg){
				return WebUtil.generateStr(OutTextMsg.TEMPLATE, root);
			}else if(outMsg instanceof OutNewsMsg){
				return WebUtil.generateStr(OutNewsMsg.TEMPLATE,root);
			}else if(outMsg instanceof OutImageMsg){
				return WebUtil.generateStr(OutImageMsg.TEMPLATE,root);
			}else if(outMsg instanceof OutVoiceMsg){
				return WebUtil.generateStr(OutVoiceMsg.TEMPLATE,root);
			}else if(outMsg instanceof OutVideoMsg){
				return WebUtil.generateStr(OutVideoMsg.TEMPLATE,root);
			}else if(outMsg instanceof OutMusicMsg){
				return WebUtil.generateStr(OutMusicMsg.TEMPLATE,root);
			}

			return "";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setEncoding(String encoding) {
		OutMsgXmlBuilderByBeetl.encoding = encoding;
	}
	
	public static String getEncoding() {
		return encoding;
	}
	
	public static void main(String[] args) {
		OutTextMsg msg = new OutTextMsg();
		msg.setToUserName("to james");
		msg.setFromUserName("from james");
		msg.setCreateTime(msg.now());
		msg.setContent("jfinal weixin 极速开发平台碉堡了");
		String xml = OutMsgXmlBuilderByBeetl.build(msg);
		System.out.println(xml);
	}
}






