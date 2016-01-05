package com.meishi.front.controller.yun.report;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.front.vo.OrderYunVo;
import com.meishi.model.Store;
import com.meishi.model.yun.*;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by niao on 15-8-21.
 */
@Before(StoreInteceptor.class)
public class MenuReportController extends BaseController {

    /**
     * 反结账报表
     */
    public void anti(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);

        Page<AntiOrderYun> page=AntiOrderYun.dao.findPage(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/anti_order_report.html");
    }

    /**
     * 撤单报表
     */
    public void cancel(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);

        Page<OrderYun> page=OrderYun.dao.findCancelPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/cancel_order_report.html");
    }

    /**
     * 详细日报表
     */
    public void detail_day(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);

        Page<OrderYun> page=OrderYun.dao.findDetailDayPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/detail_day_report.html");
    }

    /**
     * 菜单排名报表
     */
    public void menu_px(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("foodTypeId",getPara("foodTypeId"));

        //Page<OrderYun> page=OrderYun.dao.findCancelPageByStore(pageNum, pageSize, params);
        //计算cal
        //1.拉取已经完成的菜品
        Page<FoodYun> page=FoodYun.dao.menuPx(pageNum,pageSize,params);

        //2.加载菜品分类
        List<FoodTypeYun> foodTypeYunList=FoodTypeYun.dao.findByStore(store.getStr("id"));
        setAttr("foodTypeYunList",foodTypeYunList);

        setAttr("page", page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/menu_px_report.html");
    }

    /**
     * 菜单销售统计
     */
    public void menu_sales(){
        Store store = getSessionAttr("store");

//        Integer pageNum = getParaToInt("pageNum");
//        Integer pageSize = getParaToInt("pageSize");
//
//        if (pageNum == null || pageNum.intValue() < 1) {
//            pageNum = 1;// 默认第一页
//        }
//        if (pageSize == null || pageSize.intValue() < 0) {
//            pageSize = 30;// 默认每页显示10条
//        } else if (pageSize.intValue() > 100) {
//            pageSize = 30;// 最大100条
//        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1": //上月
                Calendar last_start_date_temp = Calendar.getInstance();
                last_start_date_temp.setTime(now);
                last_start_date_temp.add(Calendar.MONTH, -1);
                last_start_date_temp.set(Calendar.DAY_OF_MONTH, 1);

                Calendar last_end_date_temp = Calendar.getInstance();
                last_end_date_temp.setTime(now);
                last_end_date_temp.add(Calendar.MONTH, -1);
                last_end_date_temp.set(Calendar.DAY_OF_MONTH, last_end_date_temp.getActualMaximum(Calendar.DAY_OF_MONTH));

                beginTime=DateUtil.format(last_start_date_temp.getTime(),DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(last_end_date_temp.getTime(),DateUtil.pattern_y_m_d);
                break;
            case "2":   //自定义
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:    //当月
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DAY_OF_MONTH, 1);

                beginTime=DateUtil.format(date_temp.getTime(),DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);

        List<OrderYun> list=OrderYun.dao.menuSales(params);
        setAttr("list",list);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/menu_sales_report.html");
    }

    /**
     * 订单支付统计
     */
    public void order_pay(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("pay_type",getPara("pay_type"));

        Page<OrderYun> page=OrderYun.dao.findPayInfoPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/order_pay_report.html");
    }

    /**
     * 账单优惠
     */
    public void order_yh(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("yh_type",getPara("yh_type"));

        Page<OrderYun> page = OrderYun.dao.findYhPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/order_yh_report.html");
    }

    /**
     * 退菜统计
     */
    public void return_food(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("foodTypeId",getPara("foodTypeId"));

        Page<OrderItemReturnYun> page= OrderItemReturnYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page", page);

        //2.加载菜品分类
        List<FoodTypeYun> foodTypeYunList=FoodTypeYun.dao.findByStore(store.getStr("id"));
        setAttr("foodTypeYunList",foodTypeYunList);

        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/return_food_report.html");
    }

    /**
     * 交接班统计
     */
    public void shift(){
        Store store = getSessionAttr("store");

        Integer pageNum = getParaToInt("pageNum");
        Integer pageSize = getParaToInt("pageSize");

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;// 默认第一页
        }
        if (pageSize == null || pageSize.intValue() < 0) {
            pageSize = 30;// 默认每页显示10条
        } else if (pageSize.intValue() > 100) {
            pageSize = 30;// 最大100条
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storeId", store.getStr("id"));

        //定义查询参数
        String date=getPara("date");
        String beginTime=getPara("beginTime");
        String endTime=getPara("endTime");

        if(StringUtil.isBlank(date)){
            date="0";
        }

        Date now=new Date();

        switch (date){
            case "1":
                Calendar date_temp = Calendar.getInstance();
                date_temp.setTime(now);
                date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                Date yesterday=date_temp.getTime();
                beginTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                break;
            case "2":
                if(StringUtil.isBlank(beginTime)){
                    beginTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }

                if(StringUtil.isBlank(endTime)){
                    endTime=DateUtil.format(now, DateUtil.pattern_y_m_d);
                }
                break;
            default:
                beginTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                endTime=DateUtil.format(now,DateUtil.pattern_y_m_d);
                break;
        }

        params.put("date",date);
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);
        params.put("operator",getPara("employee"));

        //1.加载员工列表
        List<EmployeeYun> employeeYunList=EmployeeYun.dao.findListByStore(store.getStr("id"));
        setAttr("employeeYunList",employeeYunList);

        Page<ShiftRecordYun> page=ShiftRecordYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/menu/shift_report.html");
    }

    /**
     * 简要日报表
     */
    public void simple_day(){
        try {
            Store store = getSessionAttr("store");
            String store_id = store.getStr("id");
            //获取日期
            String search_day= getPara("search_day");

            String date=getPara("date");
            if(StringUtil.isBlank(date)){
                date="0";
            }
            setAttr("date",getPara("date"));

            Date now=new Date();

            switch (date){
                case "1":
                    Calendar date_temp = Calendar.getInstance();
                    date_temp.setTime(now);
                    date_temp.set(Calendar.DATE, date_temp.get(Calendar.DATE) - 1);

                    Date yesterday=date_temp.getTime();
                    search_day=DateUtil.format(yesterday,DateUtil.pattern_y_m_d);
                    break;
                case "2":
                    if(StringUtil.isBlank(search_day)){
                        search_day=DateUtil.format(now, DateUtil.pattern_y_m_d);
                    }
                    break;
                default:
                    search_day=DateUtil.format(now,DateUtil.pattern_y_m_d);
                    break;
            }

            setAttr("search_day",search_day);

            //处理日期
            search_day=search_day.replace("-","").replace(" ","").replace(":","");

            //1>付款信息(现金,银行卡,会员卡,美团,代金券,网付,优惠金额)
            List<OrderPayYun> payYunList= OrderPayYun.dao.findByDate(search_day, store_id);
            setAttr("payYunList",payYunList);
            //1-2>查询未付款金额
//            BigDecimal notPayMoney= OrderItemYun.dao.findNotPayMoney(search_day,store_id);
//            setAttr("notPayMoney",notPayMoney);
            //1-3>总金额
            BigDecimal payedAllMoney=OrderPayYun.dao.findAllMoney(search_day,store_id);
            setAttr("payedAllMoney",payedAllMoney);
            //2>订单信息(已付订单,未付订单,撤单,反结账,优惠数量,会员消费订单)
            //2-1>优惠金额-数量
            OrderYun yhOrder=OrderYun.dao.findYhByDate(search_day, store_id);
            setAttr("yhOrder",yhOrder);
            //2-2>已付订单数量
            Integer payedCount=OrderYun.dao.findCountByStatus(search_day, store_id, "payed");
            setAttr("payedCount",payedCount);
            //2-3>未付订单数量
//            Integer notPayCount=OrderYun.dao.findNotPayCount(search_day, store_id);
            OrderYun notPay =OrderYun.dao.findNotPayInfo(store_id);
            setAttr("notPayCount",notPay.get("count_"));
            setAttr("notPayMoney",notPay.get("money_"));
            //2-4>撤单数量
            Integer cancelCount=OrderYun.dao.findCountByStatus(search_day, store_id, "cancel");
            setAttr("cancelCount",cancelCount);
            //2-5>反结账数量
            Integer antiCount=AntiOrderYun.dao.findCount(search_day,store_id);
            setAttr("antiCount",antiCount);
            //2.6>会员消费数量
            Integer cardOrderCount= CardRecordYun.dao.findOrderCount(search_day,store_id);
            setAttr("cardOrderCount",cardOrderCount);
            //3>菜品信息(序号,菜类,销售数量,销售金额,退菜数量,退菜金额)
            //定义菜品信息
            Map<String,OrderYunVo> orderMap=new HashMap<>();
            //3-1>查询payed
            List<OrderItemYun> orderYunList=OrderItemYun.dao.findPayedByDate(search_day, store_id);
            for(OrderItemYun orderyun_ : orderYunList) {
                OrderYunVo vo = new OrderYunVo();
                vo.setFoodTypeName(orderyun_.getStr("food_type_name"));
                vo.setPayCount(orderyun_.getInt("count_"));
                vo.setPayMoney(orderyun_.getBigDecimal("money_"));
                vo.setReturnCount(0);
                vo.setReturnMoney(new BigDecimal(0.00));

                String foodTypeId = orderyun_.getStr("food_type_id");
                if (StringUtil.isBlank(foodTypeId)){
                    foodTypeId = "";
                }
                orderMap.put(foodTypeId, vo);
            }
            //3-2>查询return
            List<OrderItemReturnYun> orderItemReturnYunList=OrderItemReturnYun.dao.findByDate(search_day, store_id);
            for(OrderItemReturnYun orderreturnyun_ : orderItemReturnYunList){
                String foodTypeId=orderreturnyun_.getStr("food_type_id");
                if (StringUtil.isBlank(foodTypeId)) foodTypeId = "";

                if(orderMap.containsKey(foodTypeId)){
                    orderMap.get(foodTypeId).setReturnCount(orderreturnyun_.getInt("count_"));
                    orderMap.get(foodTypeId).setReturnMoney(orderreturnyun_.getBigDecimal("money_"));
                }else {
                    OrderYunVo vo = new OrderYunVo();
                    vo.setFoodTypeName(orderreturnyun_.getStr("food_type_name"));
                    vo.setReturnCount(orderreturnyun_.getInt("count_"));
                    vo.setReturnMoney(orderreturnyun_.getBigDecimal("money_"));
                    vo.setPayCount(0);
                    vo.setPayMoney(new BigDecimal(0.00));
                    orderMap.put(foodTypeId, vo);
                }
            }
            //3-3>查询撤单
            List<OrderItemYun> orderItemCancelYunList=OrderItemYun.dao.findCancelByDate(search_day, store_id);
            for(OrderItemYun orderCancelyun_ : orderItemCancelYunList){
                String foodTypeId=orderCancelyun_.getStr("food_type_id");
                if (StringUtil.isBlank(foodTypeId)) foodTypeId = "";

                if(orderMap.containsKey(foodTypeId)){
                    OrderYunVo tmp=orderMap.get(foodTypeId);   //撤单数据加到退菜上面
                    orderMap.get(foodTypeId).setReturnCount(orderCancelyun_.getInt("count_")+tmp.getReturnCount());
                    orderMap.get(foodTypeId).setReturnMoney(orderCancelyun_.getBigDecimal("money_").add(tmp.getReturnMoney()));
                }else {
                    OrderYunVo vo = new OrderYunVo();
                    vo.setFoodTypeName(orderCancelyun_.getStr("food_type_name"));
                    vo.setReturnCount(orderCancelyun_.getInt("count_"));
                    vo.setReturnMoney(orderCancelyun_.getBigDecimal("money_"));
                    vo.setPayCount(0);
                    vo.setPayMoney(new BigDecimal(0.00));
                    orderMap.put(foodTypeId, vo);
                }
            }
            setAttr("orderMap",orderMap);
            render("/store/admin/yun/report/menu/simple_day_report.html");
        }catch (Exception ex){
            System.out.print(ex.getStackTrace());
        }
    }
}
