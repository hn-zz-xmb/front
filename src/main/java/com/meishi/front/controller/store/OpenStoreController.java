package com.meishi.front.controller.store;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.FileUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.msg.SendMsgTemplate;





import net.sf.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

//@Controller(controllerKey = "/store/openstore")
@Before(LoginInteceptor.class)
public class OpenStoreController extends BaseController {
	private static Logger log = Logger.getLogger(OpenStoreController.class);

	// 申请开店第一步，跳转到选择店铺类型页面
	public void index() {
		render("/store/openstore/open_store_first.html");
	}

	// 申请开店第二步，根据店铺类型查询该类型下所有店铺套餐
	public void secondStep() {
		List<StoreLevel> storeLevels = StoreLevel.dao
				.findLevelByStoreType(getPara("storeType"));
		setAttr("storeType", getPara("storeType"));
		setAttr("storeLevels", storeLevels);
		render("/store/openstore/open_store_second.html");
	}

	// 申请开店第三步
	public void thirdStep() {
		List<Business> businesses = Business.dao.findAll();
		List<Province> provinces = Province.dao.findAll();
		String type = getPara("storeType");
		String level = getPara("level");
		if (StringUtil.isBlank(level) || StringUtil.isBlank(type)) {
			redirect("/openstore");
			return;
		}
		setAttr("businessList", businesses);
		setAttr("provinceList", provinces);
		setAttr("type", type);
		setAttr("level", level);
		render("/store/openstore/open_store_third.html");
	}

	// 保存注册店铺信息并验证
	public void saveStore() {
		JSONObject result = new JSONObject();
		Store store = getModel(Store.class);
		Member member = Member.dao.findById(getUserIds());
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("phoneCode"), SendMsgTemplate.REGIST_STORE_CODE,
				store.getStr("phone"));
		// StringUtils.isEmpty(memberLink)
		// ||
		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", "n");
			result.put("info", "手机动态码不正确或者已过期!");
			renderJson(result.toString());
			return;
		} else {
			store.set("id", ToolUtil.getUuidByJdk(true))
					.set("user_id", member.getStr("id"))
					.set("reg_time",
							DateUtil.format(new Date(), "yyyyMMddHHmmss"))
					.set("open_status", 1).set("template_id", "default")
					.set("credit_degree", 0)
					.set("audit_status", AppContextData.UNAUDITED)
					.set("is_take_out", 0).set("may_recommend", 2)
					.set("has_recommend", 0).set("may_recommend_sm", 2)
					.set("has_recommend_sm", 0);
			if(StringUtil.isNotBlank(store.getStr("card_image"))){
				upcardimg(store);
			}
			
			if(StringUtil.isNotBlank(store.getStr("licence_image"))){
				upimg(store);
			}
			store.save();
			result.put("status", "y");
			renderJson(result.toString());
		}
	}

	// 重新申请店铺
	public void againapply() {
		JSONObject result = new JSONObject();
		Store store = getModel(Store.class);
		if (store.get("id") == "" || store.get("id") == null) {
			result.put("status", "n");
			result.put("info", "申请失败");
			renderJson(result.toString());
			return;
		}
		Store oldstore=Store.dao.findById(store.getStr("id"));
		if(StringUtil.isBlank(store.getStr("card_image"))){			
			if(StringUtil.isNotBlank(oldstore.getStr("card_image"))){
				deletecardimg(oldstore);
			}
		}else{
			if(StringUtil.isBlank(oldstore.getStr("card_image"))){
				upcardimg(store);
			}else{
				deletecardimg(oldstore);
				upcardimg(store);
			}
		}
		
		if(StringUtil.isBlank(store.getStr("licence_image"))){			
			if(StringUtil.isNotBlank(oldstore.getStr("licence_image"))){
				deleteimg(oldstore);
			}
		}else{
			if(StringUtil.isBlank(oldstore.getStr("licence_image"))){
				upimg(store);
			}else{
				deleteimg(oldstore);
				upimg(store);
			}
		}
		
		store.set("open_status", 1)
				.set("audit_status", AppContextData.UNAUDITED).update();
		result.put("status", "y");
		renderJson(result.toString());
	}
	
	//身份证图片处理
	public void upcardimg(Store store){
		String old_root = PropertyConfig.me().getProperty("config.temp");
		String oldPath = old_root + store.getStr("card_image");

		String root = PropertyConfig.me().getProperty("config.pc");
		String loadPath = FileUtils.generateDateDir(root) + "/"
				+ ToolUtil.getUuidByJdk(true) + ".jpg";
		String newPath = root + loadPath;
		
		FileUtils.Copy(oldPath, newPath);
		store.set("card_image",loadPath);
	}
	//身份证图片删除
	public void deletecardimg(Store store){		
		String rootPath = PropertyConfig.me().getProperty("config.pc");
		File f=new File(rootPath+store.getStr("card_image"));
        if(f.exists())f.delete();
	}
	//执照图片处理
	public void upimg(Store store){
		String old_root = PropertyConfig.me().getProperty("config.temp");
		String oldPath = old_root + store.getStr("licence_image");

		String root = PropertyConfig.me().getProperty("config.pc");
		String loadPath = FileUtils.generateDateDir(root) + "/"
				+ ToolUtil.getUuidByJdk(true) + ".jpg";
		String newPath = root + loadPath;
		
		FileUtils.Copy(oldPath, newPath);
		store.set("licence_image",loadPath);
	}
	//执照图片删除
	public void deleteimg(Store store){		
		String rootPath = PropertyConfig.me().getProperty("config.pc");
		File f=new File(rootPath+store.getStr("licence_image"));
        if(f.exists())f.delete();
	}
}
