package com.meishi.front.controller.index;

import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.util.IPUtils;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Advert;
import com.meishi.model.Article;
import com.meishi.model.City;
import com.meishi.model.FriendUrl;
import com.meishi.util.WebUtil;

import java.util.List;

//@Controller(controllerKey = "/")
public class IndexController extends BaseController {
	private static Logger log = Logger.getLogger(IndexController.class);

	// 城市
	public void index() {
		City city = getSessionAttr("cur_city");
		if (city == null) {
			city = City.dao.findByName("郑州市");
			setSessionAttr("cur_city", city);
		}
		List<Advert> advertAd = Advert.dao.findBy("main_pro", "main_index",
				city.getStr("id"));
		List<Article> sjzx = Article.dao.findsj();
		List<Article> hyzx = Article.dao.findhy();
		List<Article> ysjk = Article.dao.findjk();
		List<Article> msdiy = Article.dao.findms();
		List<Article> ysxt = Article.dao.findys();
		setAttr("advertAdList", advertAd);
		setAttr("sjzx", sjzx);
		setAttr("hyzx", hyzx);
		setAttr("ysjk", ysjk);
		setAttr("msdiy", msdiy);
		setAttr("ysxt", ysxt);
		// log.info("测试访问首页");
		// log.debug("你好");
		// log.error("wrong");
		List<FriendUrl> friendUrls = FriendUrl.dao.findTop();
		setAttr("friendLinkList", friendUrls);
		render("/index/index.html");
	}

	// ajax定位
	public void ajaxPosition() {
		City city = getSessionAttr("cur_city");

		if (city == null) {
			// 获取当前城市
			String ip = WebUtil.getClientIpAddr(getRequest());
			String city_name = IPUtils.getIPADDR(ip);
			city = City.dao.findByName(city_name);
			if (city == null) {
				city = City.dao.findById(AppContextData.DEFAULT_CITY);
			}
			setSessionAttr("cur_city", city);
		}
		renderJson(city.toJson());
	}
	public void ajaxHotBrand(){
		City city = getSessionAttr("cur_city");
		if (city == null) {
			city = City.dao.findByName("郑州市");
			setSessionAttr("cur_city", city);
		}
		List<Advert> advertAd = Advert.dao.findBy("main_pro", "main_index",
				city.getStr("id"));
		setAttr("advertAdList", advertAd);
		render("/common/hotbrand.html");
	}
}
