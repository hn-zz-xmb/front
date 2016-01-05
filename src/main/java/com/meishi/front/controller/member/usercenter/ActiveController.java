package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.ActivePrizeInfo;
import com.meishi.model.Member;
import com.meishi.model.Message;

import java.util.HashMap;
import java.util.Map;

//@Controller(controllerKey = "/account/activeprizeinfo")
@Before(LoginInteceptor.class)
public class ActiveController extends BaseController {
	private static Logger log = Logger.getLogger(ActiveController.class);

	public void AllActivePrizeInfo() {
		Member member=Member.dao.findById(getUserIds(),"id,username");
		/*Map<Integer, Integer> result = ActivePrizeInfo.dao.findCount(member.getStr("id"));
		int totalCount = 0;
		for (int count : result.values()) {
			totalCount += count;
		}
		setAttr("count", result);
		setAttr("totalCount", totalCount);*/
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", getPara("status"));
		params.put("userId", member.getStr("id"));
		params.put("startTime", getPara("startTime"));
		params.put("endTime", getPara("endTime"));
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 8;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 8;// 最大100条
		}
		Page<ActivePrizeInfo> result = ActivePrizeInfo.dao.AllActivePrizeInfo(
				pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("div", getPara("div"));
		setAttr("url", getPara("url"));
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/prizeList1.html");
	}

	public void ajaxPrizeInfo() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", getPara("status"));
		params.put("userId", member.getStr("id"));
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 8;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 8;// 最大100条
		}
		Page<ActivePrizeInfo> result = ActivePrizeInfo.dao.AllActivePrizeInfo(
				pageNum, pageSize, params);
		setAttr("page", result);
		setAttr("div", getPara("div"));
		setAttr("url", getPara("url"));
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		render("/usercenter/_prizeItem.html");
	}

	// 查询单个中奖记录详情
	public void prizeInfoDetail() {
		ActivePrizeInfo activePrizeInfo = ActivePrizeInfo.dao
				.findDetail(getPara("infoId"));
		setAttr("activePrizeInfo", activePrizeInfo);
		render("/usercenter/prizeDetail.html");
	}
	
	// 执行删除记录操作
	public void DelprizeInfo() {
		ActivePrizeInfo activePrizeInfo = ActivePrizeInfo.dao
				.delById(getPara("id"));
		setAttr("activePrizeInfo", activePrizeInfo);
		render("/usercenter/prizeList1.html");
	}
	//删除
    public void delete(){
    	String id=getPara("id");
    	if(id==null){
    		renderText("fail");
    	}else{
    		ActivePrizeInfo.dao.deleteById(id);
    	renderText("success");
    	}
    }
}
