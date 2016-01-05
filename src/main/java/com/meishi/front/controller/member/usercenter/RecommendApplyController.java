package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.RecommendApply;
import com.meishi.model.RecommendMember;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.Date;

@Before(LoginInteceptor.class)
public class RecommendApplyController extends BaseController {

	private static Logger log = Logger.getLogger(RecommendApply.class);

	// 提交申请
	public void index() {
		JSONObject result = new JSONObject();
		RecommendApply recommendApply = getModel(RecommendApply.class);
		RecommendMember recommendMember = RecommendMember.dao
				.findBymemberId(getUserIds());
		if (recommendMember == null) {
			if (StringUtil.isBlank(recommendApply.getStr("id"))) {
				RecommendApply recommend = RecommendApply.dao
						.findBymemberId(getUserIds());
				if (recommend == null) {
					recommendApply
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("apply_id", getUserIds())
							.set("apply_time",
									DateUtil.format(new Date(),
											DateUtil.pattern_ymd_hms))
							.set("audit_status",
									AppContextData.RECOMMEND_APPLY_GOING)
							.save();
					result.put("isLogin", true);
				} else {
					result.put("error", "已有申请记录");
					result.put("isLogin", false);
				}
			}else{
				recommendApply.set("apply_time",DateUtil.format(new Date(),DateUtil.pattern_ymd_hms))
						.set("audit_status", AppContextData.RECOMMEND_APPLY_GOING).update();
				result.put("isLogin", true);
			}
		} else {
			result.put("error", "已经是推荐人了");
			result.put("isLogin", false);
		}
		renderJson(result.toString());
	}

	// 提交申请页面
	public void ajaxapply() {
		render("/usercenter/recommendApply.html");
	}

	// 查看失败原因
	public void ajaxreson() {
		String id = getPara("id");
		if (id != null) {
			RecommendApply recommendApply = RecommendApply.dao.findById(id);
			setAttr("recommendApply", recommendApply);
			render("/usercenter/refuse.html");
		} else {
			renderError(500);
		}
	}

	// 跳转重新申请页面
	public void ajaxReapply() {
		String id = getPara("id");
		if (id != null) {
			RecommendApply recommendApply = RecommendApply.dao.findById(id);
			setAttr("recommendApply", recommendApply);
			render("/usercenter/recommendApply.html");
		} else {
			renderError(500);
		}
	}

}
