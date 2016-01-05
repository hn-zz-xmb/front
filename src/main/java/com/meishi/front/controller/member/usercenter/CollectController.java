package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Collect;
import com.meishi.model.Member;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/buyer/collect")
@Before(LoginInteceptor.class)
public class CollectController extends BaseController {
	private static Logger log = Logger.getLogger(CollectController.class);
	// 查询用户的收藏(默认查询的是用户收藏的商品)
	public void collectlist() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.getStr("id"));
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
		Page<Collect> collects = Collect.dao.findGoodsCollectByUserId(pageNum,
				pageSize, params);
		setAttr("collects", collects);
		setAttr("param", paramsToStr(params));
		render("");
	}

	// 删除用户收藏(商品)
	public void deleteGoodsCollect() {
		Collect.dao.deleteById(getPara("collect_id"));
		redirect("/buyer/collect/collectlist");
	}

	// 查询用户收藏(店铺)
	public void storecollect() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", member.getStr("id"));
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
		Page<Collect> collects = Collect.dao.findStoreCollectByUserId(pageNum,
				pageSize, params);
		setAttr("collects", collects);
		setAttr("param", paramsToStr(params));
		render("");
	}

	// 删除用户收藏(店铺)
	public void deleteStoreCollect() {
		Collect.dao.deleteById(getPara("collect_id"));
		redirect("/buyer/collect/storecollect");
	}

	// 添加收藏
	@ClearInterceptor
	public void saveCollect() {
		Member member = Member.dao.findById(getUserIds());
		Collect collect=Collect.dao.findByCollect(getPara("collect_type"), getPara("collect"), member.getStr("id"));
		if(collect==null){
			new Collect().set("user_id", member.getStr("id")).set("id",ToolUtil.getUuidByJdk(true))
			.set("collect", getPara("collect")).set("collect_type", getPara("collect_type")).save();
			setAttr("massege",true);
		}else{//已经收藏
			setAttr("massege",false);
		}
		setAttr("collect_type", getPara("collect_type"));
		render("/store/shoucangDialog.html");
	}
	@ClearInterceptor
	public void islogin() {
		JSONObject result = new JSONObject();
		// 验证是否登陆
		if (StringUtil.isBlank(getUserIds())) {
			result.put("isLogin", false);
			result.put("error", "请先登录");
			renderJson(result.toString());
			return;
		}
		result.put("isLogin", true);
		renderJson(result.toString());
	}

}
