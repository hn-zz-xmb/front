package com.meishi.front.controller.goods;

/**
 * Created by rsp on 14-12-6.
 */

import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.*;
import com.meishi.util.StringUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询套餐详情
 */
public class SetMealController extends BaseController {

	/**
	 * 套餐详情
	 */
	public void view() {
		String id = getPara("id");
		String type = getPara("type");
		City city = getSessionAttr("cur_city");
		if (city == null) {
			city = City.dao.findByName("郑州市");
		}
		if (StringUtil.isNotBlank(type) && type.equals("1")) {
			if (StringUtil.isNotBlank(id)) {
				List<Advert> advertAd = Advert.dao.findBy("main_pro",
						"setmealdetail_r", city.getStr("id"));
				SetMeal setMeal = SetMeal.dao.findById(id);
				if (setMeal != null) {
                    //处理description
                    String description=setMeal.getStr("description");
                    description= StringUtil.isBlank(description)?"":description;
                    setMeal.set("description", StringEscapeUtils.unescapeHtml4(description));
					List<SetMealItem> setMealItemList = SetMealItem.dao
							.findBySetmealId(setMeal.getStr("id"));
					 List<UploadImg> goodsImgs=UploadImg.dao.findBygoodId(setMeal.getStr("id"));
					setAttr("setmeal", setMeal);
					setAttr("goodsImgs", goodsImgs);
					// 计算折扣
//					BigDecimal disCount = setMeal
//							.getBigDecimal("price")
//							.divide(setMeal.getBigDecimal("original_price"), 2,
//									RoundingMode.HALF_UP)
//							.multiply(new BigDecimal(10));
//					setAttr("disCount", 0.0);
					setAttr("advertAd", advertAd);
					setAttr("setMealItemList", setMealItemList);
					setAttr("store",
							Store.dao.findBystoreId(setMeal.getStr("store_id")));
					render("/goods/套餐详情.html");
				} else {
					renderError(404);
				}
			}
		} else {
			// 代金券
			Coupon coupon = Coupon.dao.findById(id);
			List<Advert> advertAd = Advert.dao.findBy("main_pro",
					"coupondetail_r", city.getStr("id"));
			setAttr("advertAd", advertAd);
			setAttr("coupon", coupon);
			setAttr("store", Store.dao.findBystoreId(coupon.getStr("store_id")));
			render("/goods/yhxq.html");
		}
	}

    /**
     * 加载购买记录
     * Ajax load
     */
    public void loadSetMealBugHistory(){
        String setMealId=getPara("setMealId");
        Map params=new HashMap<>();
        params.put("setMealId",setMealId);
        //1.分页查询
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
        Page<Orders> ordersPage=Orders.dao.findSetMealBuyHistory(setMealId, pageNum, pageSize);
        setAttr("goodsBugHistoryPage",ordersPage);
        setAttr("url","goods/loadGoodsBugHistory");
        setAttr("div","tab3");
        setAttr("page_params", paramsToStr(params));
        render("/goods/_detail_buy_history.html");
    }
}