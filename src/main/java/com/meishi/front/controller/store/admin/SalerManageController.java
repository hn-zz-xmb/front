package com.meishi.front.controller.store.admin;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.dll.StoreBannerImgDll;
import com.meishi.front.common.dll.StoreLogoImgDll;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.FoodImage;
import com.meishi.model.MemberLink;
import com.meishi.model.Store;
import com.meishi.util.StringUtil;
import com.meishi.util.beetl.render.CaptchaRender;
import com.meishi.util.msg.SendMsgTemplate;

//@Controller(controllerKey = "/store/manage")
@Before(StoreInteceptor.class)
public class SalerManageController extends BaseController {
	private static Logger log = Logger.getLogger(SalerManageController.class);

	// 设置店铺地理位置
	public void setStorePosition() {
		JSONObject result = new JSONObject();
		if (StringUtils.isEmpty(getPara("storeId"))) {
			result.put("isFlag", false);
			result.put("error", "店铺信息有误");
			renderJson(result.toString());
		}
		if (StringUtils.isEmpty(getPara("lat"))) {
			result.put("isFlag", false);
			result.put("error", "地理信息有误");
			renderJson(result.toString());
		}
		Store store = Store.dao.findById(getPara("storeId"));
		store.set("position_lat", getPara("lat"))
				.set("position_lng", getPara("lng")).update();
		result.put("isFlag", true);
		renderJson(result.toString());
	}

	// 修改店铺手机号码
	public void toupdatemobie() {
		Store store = Store.dao.findById(getPara("storeId"));
		setAttr("store", store);
		render("/store/admin/store/modifyPhoneDialog.html");
	}

	/**
	 * 验证手机激活码
	 */
	public void validPhoneCode() {
		JSONObject result = new JSONObject();
		Store store = getModel(Store.class);
		if (!store.getStr("phone").equals(getPara("oldphone"))) {
			result.put("status", false);
			result.put("error", "原手机号不正确");
			renderJson(result.toString());
			return;
		}
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("mobilecode"), SendMsgTemplate.USE_CODE,
				getPara("store.phone"));

		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", false);
			result.put("error", "激活码不存在或已失效");
			renderJson(result.toString());
			return;
		}
		if (!CaptchaRender.validate(getSessionAttr("register_code").toString(),
				getPara("checkcode"))) {
			result.put("status", "n");
			result.put("info", "验证码输入错误");
			renderJson(result.toString());
			return;
		}
		store.update();
		result.put("status", true);
		renderJson(result.toString());
	}

	// 跳转店铺管理页面
	public void storeSetting() {
		Store store = getSessionAttr("store");
		Store store1 = Store.dao.findStoreById(store.getStr("id"));
		store1.set("description",
				StringEscapeUtils.unescapeHtml4(store1.getStr("description")));
		setAttr("store", store1);
		render("/store/admin/store/form.html");
	}

	/**
	 * 保存店铺
	 */
	public void update() {
		Store store = getModel(Store.class);
		if (store.getInt("is_take_out") == 0) {
			store.set("take_out_fee", 0).set("full_money", 0)
					.set("delivery_scope", null);
		}
		store.set("description",
				StringEscapeUtils.unescapeHtml4(store.getStr("description")));
		String root = PropertyConfig.me().getProperty("config.pc");
		FoodImage logoImg = FoodImage.dao.findByStore(store.getStr("id"),
				"store_logo");
		FoodImage bannerImg = FoodImage.dao.findByStore(store.getStr("id"),
				"store_banner");
		if (StringUtil.isBlank(store.getStr("logo_image"))) {
			if (logoImg != null) {
				FoodImage.dao.deleteById(logoImg.getStr("id"), root);// 删除商品的图片
			}
		} else {
			if (logoImg != null) {
				if (!logoImg.getStr("middle_url").equals(
						store.getStr("logo_image"))) {
					FoodImage.dao.deleteById(logoImg.getStr("id"), root);// 删除商品的图片
					upLogoImg(store);
				}
			} else {
				upLogoImg(store);
			}
		}

		if (StringUtil.isBlank(store.getStr("banner_image"))) {
			if (bannerImg != null) {
				FoodImage.dao.deleteById(bannerImg.getStr("id"), root);// 删除商品的图片
			}
		} else {
			if (bannerImg != null) {
				if (!bannerImg.getStr("large_url").equals(
						store.getStr("banner_image"))) {
					FoodImage.dao.deleteById(bannerImg.getStr("id"), root);// 删除商品的图片
					upBannerImg(store);
				}
			} else {
				upBannerImg(store);
			}
		}
		store.update();
		redirect("/salerManage/storeSetting");
	}

	// logo图片处理
	public void upLogoImg(Store store) {
//		FoodImage uploadImg = new UploadImg();
//		uploadImg
//				.set("id", ToolUtil.getUuidByJdk(true))
//				.set("item_id", store.getStr("id"))
//				.set("store_id", store.getStr("id"))
//				.set("img_type", "store_logo")
//				.set("create_time",
//						DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));

		// 生成图片
//		String[] path = StoreLogoImageDll.getAllImage(store
//				.getStr("logo_image"));
//		uploadImg.set("small_url", path[0]).set("middle_url", path[1])
//				.set("large_url", path[2]).save();
		String path = StoreLogoImgDll.getImage(store.getStr("logo_image"));
		if(path!=null){
			store.set("logo_image", path);
		}
	}

	// 条幅处理
	public void upBannerImg(Store store) {
//		FoodImage uploadImg = new FoodImage();
//		uploadImg
//				.set("id", ToolUtil.getUuidByJdk(true))
//				.set("item_id", store.getStr("id"))
//				.set("store_id", store.getStr("id"))
//				.set("img_type", "store_banner")
//				.set("create_time",
//						DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));

		// 生成图片
//		String[] path = StoreBannerImageDll.getAllImage(store
//				.getStr("banner_image"));
//		uploadImg.set("small_url", path[0]).set("middle_url", path[1])
//				.set("large_url", path[2]).save();
		String path = StoreBannerImgDll.getImage(store.getStr("banner_image"));
		if(path!=null){
			store.set("banner_image", path);
		}
	}

	// 修改店铺地图显示位置
	public void updatePosition() {
		Store store = getModel(Store.class);
		JSONObject result = new JSONObject();
		if (store.update()) {
			setSessionAttr("store", store);
			result.put("isFlag", true);
		} else {
			result.put("isFlag", false);
		}
		renderJson(result.toString());
	}
}
