package com.meishi.front.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Member;
import com.meishi.util.StringUtil;

import javax.servlet.http.HttpServletRequest;


/**
 * 第一拦截是否登录
 * Created by rsp on 14-11-24.
 */
public class LoginInteceptor implements Interceptor {

    private static Logger log = Logger.getLogger(LoginInteceptor.class);

    @Override
    public void intercept(ActionInvocation ai) {
        HttpServletRequest request = ai.getController().getRequest();
        HttpServletRequest response = ai.getController().getRequest();

        BaseController baseController=(BaseController)ai.getController();

//        PtSyslog reqSysLog = baseController.getAttr(GlobalHandler.reqSysLogKey);
//        baseController.setReqSysLog(reqSysLog);

        if(StringUtil.isBlank(baseController.getUserIds()) && !checkCookie(baseController)){
            log.info("未登录!");

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
            log.info(e.getMessage());
            e.printStackTrace();
            log.info("返回失败提示页面!");
           toPage(baseController,2);
        }
        //ai.getController().getSessionAttr("");
    }


    /**
     * 跳转页面
     */
    public void toPage(BaseController controller,int type) {
        if(type == 1){// 未登录处理
            controller.redirect("/login?ret_url="+controller.getRequest().getServletPath());
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
    private boolean checkCookie(BaseController controller){
    	String username = controller.getCookie("username");
		String ticket = controller.getCookie("ticket");
		System.out.println(ticket);
		if(username==null || ticket==null){
			return false;
		}
		Member member = Member.dao.findByLogin(username);
		if(ticket.equals(member.getStr("ticket"))){
			controller.setSessionAttr("user_", member);
			return true;
		}
		return false;
    }
}
