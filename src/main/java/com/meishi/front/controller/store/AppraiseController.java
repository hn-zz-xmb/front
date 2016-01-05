package com.meishi.front.controller.store;

import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.OrderAppraise;
import com.meishi.model.Store;
import com.meishi.util.DateUtil;

import java.util.*;

//@Controller(controllerKey = "/store")
public class AppraiseController extends BaseController {
	private static Logger log = Logger.getLogger(AppraiseController.class);
	//查询所有评价
	public void getAllAppraise() {
		Store store = Store.dao.findBystoreId(getPara("storeId"));
//		// 近一周好、中、差三个评价的统计
//		Integer appWeek1 = Appraise.dao.findWeekCount(store.getStr("id"), 1);
//		Integer appWeek2 = Appraise.dao.findWeekCount(store.getStr("id"), 2);
//		Integer appWeek3 = Appraise.dao.findWeekCount(store.getStr("id"), 3);
//		// 近3个月好、中、差三个评价的统计
//		Integer appThreeMonth1 = Appraise.dao.findThreeMonthCount(
//				store.getStr("id"), 1);
//		Integer appThreeMonth2 = Appraise.dao.findThreeMonthCount(
//				store.getStr("id"), 2);
//		Integer appThreeMonth3 = Appraise.dao.findThreeMonthCount(
//				store.getStr("id"), 3);
//		// 近6个月好、中、差三个评价的统计
//		Integer appSixMonth1 = Appraise.dao.findSixMonthCount(
//				store.getStr("id"), 1);
//		Integer appSixMonth2 = Appraise.dao.findSixMonthCount(
//				store.getStr("id"), 2);
//		Integer appSixMonth3 = Appraise.dao.findSixMonthCount(
//				store.getStr("id"), 3);
//		// 查询所有的好中差评论的统计
//		Integer allCount1 = Appraise.dao.findAllAppCount(store.getStr("id"), 1);
//		Integer allCount2 = Appraise.dao.findAllAppCount(store.getStr("id"), 2);
//		Integer allCount3 = Appraise.dao.findAllAppCount(store.getStr("id"), 3);
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("storeId", getPara("storeId"));
//		Integer pageNum = getParaToInt("pageNum");
//		Integer pageSize = getParaToInt("pageSize");
//		if (pageNum == null || pageNum.intValue() < 1) {
//			pageNum = 1;// 默认第一页
//		}
//		if (pageSize == null || pageSize.intValue() < 0) {
//			pageSize = 6;// 默认每页显示10条
//		} else if (pageSize.intValue() > 100) {
//			pageSize = 6;// 最大100条
//		}
//		Page<Appraise> appraise = Appraise.dao.findAllAppraise(pageNum,
//				pageSize, params);
//		setAttr("appWeek1", appWeek1);
//		setAttr("appWeek2", appWeek2);
//		setAttr("appWeek3", appWeek3);
//		setAttr("appThreeMonth1", appThreeMonth1);
//		setAttr("appThreeMonth2", appThreeMonth2);
//		setAttr("appThreeMonth3", appThreeMonth3);
//		setAttr("appSixMonth1", appSixMonth1);
//		setAttr("appSixMonth2", appSixMonth2);
//		setAttr("appSixMonth3", appSixMonth3);
//		setAttr("allCount1", allCount1);
//		setAttr("allCount2", allCount2);
//		setAttr("allCount3", allCount3);
//		setAttr("appraise", appraise);
//		setAttr("params", params);
        //设置最近7天的时间
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        List<OrderAppraise> count=OrderAppraise.dao.findCountByStore(store.getStr("id"), DateUtil.format(calendar.getTime(),DateUtil.pattern_ymd_hms));
        //设置最近3个月天的时间
        Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.add(Calendar.MONTH, -3);
        List<OrderAppraise> count1=OrderAppraise.dao.findCountByStore(store.getStr("id"), DateUtil.format(calendar1.getTime(),DateUtil.pattern_ymd_hms));

        //设置最近6个月
        Calendar calendar2=Calendar.getInstance();
        calendar2.setTime(new Date());
        calendar2.add(Calendar.MONTH, -6);
        List<OrderAppraise> count2=OrderAppraise.dao.findCountByStore(store.getStr("id"), DateUtil.format(calendar2.getTime(),DateUtil.pattern_ymd_hms));

        List<OrderAppraise> total=OrderAppraise.dao.findCountByStore(store.getStr("id"), null);
		setAttr("store", store);
        setAttr("count",count);
        setAttr("count1",count1);
        setAttr("count2",count2);
        setAttr("total",count2);
		render("/store/pingjia.html");
	}
	//查询所有好评


    /**
     * 店铺首页评价导航
     * 查询评价
      */
	public void pingItem(){
        String storeId=getPara("storeId");
        Integer number=getParaToInt("number");
        String div=getPara("div");
        Map params=new HashMap<>();
        params.put("storeId",storeId);
        params.put("number",number);
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
        Page<OrderAppraise> page=OrderAppraise.dao.findPageByStore(storeId,number,pageNum,pageSize);
        setAttr("page",page);
        setAttr("page_params",paramsToStr(params));
        setAttr("url","goods/loadOrderAppraise");
        setAttr("div",div);
        render("/store/_pingjia_item.html");
    }
}
