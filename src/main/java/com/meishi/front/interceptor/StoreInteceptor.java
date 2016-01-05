package com.meishi.front.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rsp on 14-11-24.
 * 第二拦截店家
 */
public class StoreInteceptor implements Interceptor {

    private static Logger log = Logger.getLogger(StoreInteceptor.class);

    @Override
    public void intercept(ActionInvocation ai) {
        HttpServletRequest request = ai.getController().getRequest();
        HttpServletRequest response = ai.getController().getRequest();

        BaseController baseController=(BaseController)ai.getController();

//        PtSyslog reqSysLog = baseController.getAttr(GlobalHandler.reqSysLogKey);
//        baseController.setReqSysLog(reqSysLog);

        if(baseController.getSessionAttr("store")==null){
            log.info("未选择店铺!");

            log.info("访问失败时保存日志!");
//            reqSysLog.set("status", "0");//失败
//            reqSysLog.set("description", "未登录");
//            reqSysLog.set("cause", "2");//2 未登录

            toPage(baseController, 1);
            return;
        }

        try {
            ai.invoke();
        }catch (Exception e){
            log.info("业务逻辑代码遇到异常时保存日志!");
//            reqSysLog.set("status", "0");//失败
//            reqSysLog.set("description", e.getMessage());
//            reqSysLog.set("cause", "3");//业务代码异常

            log.info("返回失败提示页面!");
//            toInfoJsp(contro, 5);
            toPage(baseController,2);
        }
        ai.getController().getSessionAttr("");
    }


    /**
     * 跳转页面
     */
    public void toPage(BaseController controller,int type) {
        if(type == 1){// 未选择店铺
            controller.redirect("/storeManage");
            return ;
        }
        String toPage = "/common/msgAjax.html";
        //判断是否是ajax
        String referer = controller.getRequest().getHeader("X-Requested-With");
        if(null == referer || referer.isEmpty()){
            toPage = "/common/msg.html";
        }

        controller.setAttr("referer", referer);
        controller.setAttr("msg", "error");
        controller.render(toPage);
    }
}
