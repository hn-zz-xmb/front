package com.meishi.front.controller.index;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "/storeSearch")
public class StoreSearchController extends BaseController {
	private static Logger log = Logger.getLogger(StoreSearchController.class);

	public void index() {
		City city = getSessionAttr("cur_city");
		if (city == null) {
			city = City.dao.findByName("郑州市");
		}
		List<Advert> advertAd = Advert.dao.findBy("main_pro", "storesearch_r",
				city.getStr("id"));
		setAttr("areaList", Area.dao.findByCity(city.getStr("id")));
		setAttr("business", Business.dao.findAll());
		setAttr("dataDicList", Datadic.dao.findByGroup("goods_price_interval"));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("auditStatus", AppContextData.AUDITED);
		params.put("businessId", getPara("businessId"));
		params.put("cityId", city.getStr("id"));
		params.put("areaId", getPara("areaId"));
		params.put("smallPrice", getPara("smallPrice"));
		params.put("largePrice", getPara("largePrice"));

		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 15;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 15;// 最大100条
		}
		Page<Store> result = Store.dao.list(pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("advertAd", advertAd);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/index/dianpusousuo.html");
	}
}
