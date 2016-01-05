package com.meishi.front.controller.yun.report;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.Store;
import com.meishi.model.yun.CardRecordYun;
import com.meishi.model.yun.CardYun;
import com.meishi.model.yun.EmployeeYun;
import com.meishi.model.yun.OrderYun;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by niao on 15-8-24.
 */
@Before(StoreInteceptor.class)
public class CardReportController extends BaseController {

    /**
     * 会员统计报表
     */
//    public void card_add(){
//        String store_id = getSessionAttr("storeId");
//        //获取日期
//        String search_day= getPara("search_day");
//
//        if(StringUtil.isBlank(search_day)){
//            search_day= DateUtil.format(new Date(), DateUtil.pattern_y_m_d);
//        }
//        setAttr("search_day",search_day);
//
//        //处理日期
//        search_day=search_day.replace("-","").replace(" ","").replace(":","");
//
//        //1>查询新增会员
//        Integer cardCount= CardYun.dao.findCountByDate(search_day,store_id);
//        if(cardCount==null) cardCount = 0;
//
//        //24>查询赠送金额
//        BigDecimal giveMoney= CardRecordYun.dao.giveInfoByDate(search_day,store_id);
//        if(giveMoney==null) giveMoney = new BigDecimal(0.00);
//
//        //3>查询充值金额  - - - -
//        //4>查询充值次数  - - - -
//        //5>查询消费次数  - - - -  合并查询
//        //6>查询消费金额  - - - -  聚合函数
//        //7>查询红冲金额  - - - -
//        //8>查询红冲次数  - - - -
//
//        List<CardRecordYun> list= CardRecordYun.dao.rechargeInfoByDate(search_day,store_id);
//        Map<String,CardRecordYun> result=new HashMap<>();
//        for(CardRecordYun item : list){
//            result.put(item.getStr("record_type"),item);
//        }
//        list.clear();
//
//        setAttr("card_count", cardCount);
//        setAttr("give_money",giveMoney);
//        setAttr("result",result);
//
//        render("/store/admin/yun/report/card/card_add_report.html");
//    }


    public void card_add(){
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
                beginTime= DateUtil.format(yesterday, DateUtil.pattern_y_m_d);
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
        setAttr("employeeYunList", employeeYunList);

        Page<CardYun> page=CardYun.dao.findPageByDate(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/card/card_add_report.html");
    }

    public void card_custom(){
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
                beginTime= DateUtil.format(yesterday, DateUtil.pattern_y_m_d);
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
        params.put("record_type","order");

        Page<CardRecordYun> page=CardRecordYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/card/card_custom_report.html");
    }

    public void card_recharge(){
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
                beginTime= DateUtil.format(yesterday, DateUtil.pattern_y_m_d);
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
        params.put("record_type","recharge");

        params.put("operator",getPara("employee"));

        //1.加载员工列表
        List<EmployeeYun> employeeYunList=EmployeeYun.dao.findListByStore(store.getStr("id"));
        setAttr("employeeYunList", employeeYunList);

        Page<CardRecordYun> page=CardRecordYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/card/card_recharge_report.html");
    }

    public void not_recharge(){
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
                beginTime= DateUtil.format(yesterday, DateUtil.pattern_y_m_d);
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
        params.put("record_type","return_recharge");
        params.put("operator",getPara("employee"));

        //1.加载员工列表
        List<EmployeeYun> employeeYunList=EmployeeYun.dao.findListByStore(store.getStr("id"));
        setAttr("employeeYunList", employeeYunList);

        Page<CardRecordYun> page=CardRecordYun.dao.findPageByStore(pageNum, pageSize, params);
        setAttr("page",page);
        setAttr("params",params);
        setAttr("page_params",paramsToStr(params));
        render("/store/admin/yun/report/card/not_recharge_report.html");
    }
}
