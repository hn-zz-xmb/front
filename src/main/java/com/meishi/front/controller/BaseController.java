package com.meishi.front.controller;

import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.meishi.front.config.PropertyConfig;
import com.meishi.model.Member;
import com.meishi.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

public abstract class BaseController extends Controller {
	private static Logger log = Logger.getLogger(BaseController.class);

	/**
	 * 全局变量
	 */

	// protected Syslog reqSysLog; // 访问日志

	/**
	 * 获取当前用户id
	 * 
	 * @return
	 */
	public String getUserIds() {
		Member member = getSessionAttr("user_");
		if(member==null){
			//return "402882e447c2a2690147c2a911180001";
			return null;
		}
		return member.getStr("id");
	}

	/**
	 * 重写getPara，进行二次decode解码
	 */
	@Override
	public String getPara(String name) {
		String value = getRequest().getParameter(name);
		if (null != value && !value.isEmpty()) {
			try {
				value = URLDecoder.decode(value, StringUtil.encoding);
			} catch (UnsupportedEncodingException e) {
				log.error("decode异常：" + value);
			}
		}else{
			value="";
		}
		return value;
	}

	/**
	 * 封装查询参数
	 */
	protected String paramsToStr(Map<String, Object> params){
		if(params != null && params.size() != 0) {
			StringBuilder queryStringBuilder = new StringBuilder();
			Iterator it = params.entrySet().iterator();

			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				queryStringBuilder.append((String)entry.getKey()).append('=').append(entry.getValue());
				if(it.hasNext()) {
					queryStringBuilder.append('&');
				}
			}

			return queryStringBuilder.toString();
		} else {
			return "";
		}
	}

	/**
	 * 效验Referer有效性
	 *
	 * @return
	 */
	protected boolean authReferer() {
		String referer = getRequest().getHeader("Referer");
		if (null != referer && !referer.trim().equals("")) {
			referer = referer.toLowerCase();
			String domainStr = PropertyConfig.me().getProperty("config.domain");
			String[] domainArr = domainStr.split(",");
			for (String domain : domainArr) {
				if (referer.indexOf(domain.trim()) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	/************************************ get set 方法 ************************************************/

	// public Syslog getReqSysLog() {
	// return reqSysLog;
	// }
	//
	// public void setReqSysLog(Syslog reqSysLog) {
	// this.reqSysLog = reqSysLog;
	// }
	//

}
