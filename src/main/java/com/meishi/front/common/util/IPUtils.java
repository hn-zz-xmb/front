package com.meishi.front.common.util;

import net.sf.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//获取公网IP地址
public class IPUtils {

	private static final String IPADDR = "http://ip.taobao.com/service/getIpInfo.php?ip=";

	private static JSONObject getHtmlJsonByUrl(String urlTemp) {
		URL url = null;
		InputStreamReader input = null;
		HttpURLConnection conn;
		JSONObject jsonObj = null;
		try {
			url = new URL(urlTemp);
			conn = (HttpURLConnection) url.openConnection();
			input = new InputStreamReader(conn.getInputStream(), "utf-8");
			Scanner inputStream = new Scanner(input);
			StringBuffer sb = new StringBuffer();
			while (inputStream.hasNext()) {
				sb.append(inputStream.nextLine());
			}

			inputStream.close();
			jsonObj = JSONObject.fromObject(sb.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return jsonObj;
	}

	// 返回城市名称
	public static String getIPADDR(String ip) {
		String uri = IPADDR + ip;
		JSONObject jsonObject = IPUtils.getHtmlJsonByUrl(uri);
		Integer code = (Integer) jsonObject.get("code");
		if (code == null || code.intValue() == 1) {
			return "北京";
		}

		String data = jsonObject.getString("data");

		jsonObject = JSONObject.fromObject(data);
		String city = (String) jsonObject.get("city");
		if (city == null || "".equals(city)) {
			return "北京";
		}
		return city;
	}


	public static void main(String[] args) {
	}
}
