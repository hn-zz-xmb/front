package com.meishi.front.common.controller;

import com.meishi.front.controller.BaseController;
import com.meishi.model.Area;
import com.meishi.model.City;
import com.meishi.model.Province;
import com.meishi.model.Street;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CityController extends BaseController {
	public void toChangeCity() {
		// 切换城市之后返回原来访问的页面
		setSessionAttr("last_url", getPara("last_url"));
		// 加载所有的省份
		List<Province> provinces = Province.dao.findAll();
		// 读取city
		List<City> cities = City.dao.findAllCity();
		Map<String, List<City>> cityMap = new TreeMap<String, List<City>>();
		List<City> tmpList = null;
		String tmpStr = "";
		City cityTmp = null;
		for (int j = 0; j < cities.size(); j++) {
			cityTmp = cities.get(j);
			tmpStr = cityTmp.getStr("inital");
			// 判断是否为空，map集中key不能为空
			tmpStr = tmpStr==null?"":tmpStr;
			if(cityMap.get(tmpStr)==null){
				tmpList = new ArrayList<City>();
				cityMap.put(tmpStr,tmpList);
			}else{
				cityMap.get(tmpStr).add(cityTmp);
			}
		}
		setAttr("regionList", provinces);
		setAttr("cityMap", cityMap);
		render("/city.html");
	}

	// 切换城市
	public void changeCity() {
		setSessionAttr("cur_city", City.dao.findById(getPara("cityId")));
		String last_url = getSessionAttr("last_url");
		if (StringUtils.isEmpty(last_url)) {
			redirect("/");
		}
		removeSessionAttr("last_url");
		redirect(last_url);
	}

	// Ajax方式根据省份Id查询city
	public void city() {
		String provinceId=getPara("privinceId");
		List<City> cities = City.dao.findCityByProvince(provinceId);
		renderJson(cities);
	}

	// Ajax方式根据城市Id查询area
	public void area(){
		String cityId=getPara("cityId");
		if(!cityId.equals("")||cityId!=null){
			List<Area> areas = Area.dao.findByCity(cityId);
			renderJson(areas);
		}
	}

	// Ajax方式根据城市Id查询area
	public void street(){
		String areaId=getPara("areaId");
		if(!areaId.equals("")||areaId!=null){
			List<Street> streets = Street.dao.findByAreaId(areaId);
			renderJson(streets);
		}
	}

}
