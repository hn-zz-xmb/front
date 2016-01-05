package com.meishi.front.controller.index;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.*;
import com.meishi.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "setmealSearch")
public class SetMealSearchController extends BaseController {
	private static Logger log = Logger.getLogger(SetMealSearchController.class);

	public void index() {
		City city = getSessionAttr("cur_city");
		if (city == null) {
			city = City.dao.findByName("郑州市");
		}
		List<Advert> advertAd = Advert.dao.findBy("main_pro", "setmeal_r",
				city.getStr("id"));
		setAttr("areaList", Area.dao.findByCity(city.getStr("id")));
		setAttr("mealCategory", SetMealCategory.dao.findallSetMealCategory());
		setAttr("dataDicList", Datadic.dao.findByGroup("goods_price_interval"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cityId", city.getStr("id"));
		params.put("areaId", getPara("areaId"));
		params.put("categoryId", getPara("categoryId"));
		params.put("smallPrice", getPara("smallPrice"));
		params.put("largePrice", getPara("largePrice"));
		if (StringUtil.isBlank(getPara("nav"))) {
			params.put("nav", 0);
		} else {
			params.put("nav", getParaToInt("nav"));
		}
		params.put("sort", getPara("sort"));
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 24;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 24;// 最大100条
		}
		Page<SetMeal> result = SetMeal.dao.list(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("advertAd", advertAd);
		setAttr("page_params", paramsToStr(params));
		render("/index/youhuitaocan.html");
	}
}
