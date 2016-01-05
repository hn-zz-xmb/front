package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.StoreNavigate;
import com.meishi.util.ToolUtil;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/store/admin/navigation")
@Before(StoreInteceptor.class)
public class NavigationController extends BaseController {
	private static Logger log = Logger.getLogger(NavigationController.class);

	// 导航管理
	// 查询店铺添加的导航
	public void findNavigate() {
		Map<String, Object> params = new HashMap<String, Object>();
		Store store = getSessionAttr("store");
		params.put("store_id", store.getStr("id"));
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 10;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 10;// 最大100条
		}
		Page<StoreNavigate> page = StoreNavigate.dao.pageBystore(pageNum,
				pageSize, params);
		setAttr("page", page);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/store/admin/storenavigate/navigationList.html");
	}

	// 添加导航(前往添加页面)
	public void toaddNavigate() {
		render("/store/admin/storenavigate/navigationForm.html");
	}

	// 添加导航(添加)
	public void addNavigate() {
		Store store = getSessionAttr("store");
		StoreNavigate sto = new StoreNavigate();
		sto.set("name", getPara("name"))
				.set("store_id", store.getStr("id"))
				.set("id", ToolUtil.getUuidByJdk(true))
				.set("px", getParaToInt("px"))
				.set("is_show", getParaToInt("isShow"))
				.set("substance",
						StringEscapeUtils.unescapeHtml4(getPara("substance")))
				.save();
		redirect("/salerManage/navigate/findNavigate");
	}

	// 删除导航
	public void deleteNavigate() {
		StoreNavigate.dao.deleteById(getPara("id"));
		redirect("/salerManage/navigate/findNavigate");
	}

	// 更新导航(前往页面)
	public void toupdateNavigate() {
		setAttr("navigate", StoreNavigate.dao.findById(getPara("id")));
		render("/store/admin/storenavigate/navigationEdit.html");
	}

	// 更新导航(更新)
	public void updateNavigate() {
		StoreNavigate sto = StoreNavigate.dao.findById(getPara("id"));
		sto.set("name", getPara("name"))
				.set("px", getParaToInt("px"))
				.set("is_show", getParaToInt("isShow"))
				.set("substance",
						StringEscapeUtils.unescapeHtml4(getPara("substance")))
				.update();
		redirect("/salerManage/navigate/findNavigate");
	}

	// 删除选中的导航
	public void delNavigations() {
		String ids = getPara("ids");
		String list[] = ids.split(",");
		for (int i = 0; i < list.length; i++) {
			if (list[i] != null && !"".equals(list[i])) {
				StoreNavigate.dao.deleteById(list[i]);
			}
		}
		renderText("success");
	}

	// 更换是否显示
	public void updateshow() {
		StoreNavigate sto = StoreNavigate.dao.findById(getPara("id"));
		if (sto.getInt("is_show") == 1) {
			sto.set("is_show", 0).update();
		} else {
			sto.set("is_show", 1).update();
		}
		redirect("/salerManage/navigate/findNavigate");
	}
}
