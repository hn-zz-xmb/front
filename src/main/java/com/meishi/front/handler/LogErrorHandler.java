package com.meishi.front.handler;

import com.jfinal.handler.Handler;
import com.jfinal.log.Logger;
import com.meishi.front.thread.ThreadSysLog;
import com.meishi.model.PtSyslog;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.WebUtil;
import com.meishi.util.beetl.render.CustomBeetlRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by rsp on 14-11-27.
 * 全局Handle：
 */
public class LogErrorHandler extends Handler {
    private static Logger log=Logger.getLogger(LogErrorHandler.class);

    // 排除的url，使用的target.startsWith匹配的
    private String exclude;

    public LogErrorHandler(String exclude) {
        this.exclude = exclude;
    }

    public static final String reqSysLogKey="reqSyslog";

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        // 对于非静态文件，和非指定排除的url实现过滤
        if (target.indexOf(".") == -1 && !target.startsWith(exclude)){
            log.info("初始化访问系统功能日志");
            PtSyslog reqSyslog=getSysLog(request);
            long starttime = DateUtil.getDateByTime();
            reqSyslog.set("startdate",DateUtil.format(new Date(),DateUtil.pattern_ymd_hms));
            request.setAttribute(reqSysLogKey,reqSyslog);

            log.info("设置Header");
            request.setAttribute("decorator", "none");
            response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
            response.setHeader("Pragma","no-cache"); //HTTP 1.0
            response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

            nextHandler.handle(target, request, response, isHandled);

            log.info("请求处理完毕，计算耗时");

            // 结束时间
            long endtime = DateUtil.getDateByTime();
            reqSyslog.set("enddate",DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));

            // 总耗时
            Long haoshi = endtime - starttime;
            reqSyslog.set("haoshi", haoshi);

            // 视图耗时
            long renderTime = 0;
            if(null != request.getAttribute(CustomBeetlRender.renderTimeKey)){
                renderTime = (long) request.getAttribute(CustomBeetlRender.renderTimeKey);
            }
            reqSyslog.set("viewhaoshi", renderTime);

            // action耗时
            reqSyslog.set("action_haoshi", haoshi - renderTime);

            log.info("日志添加到入库队列");
            ThreadSysLog.add(reqSyslog);
        }

    }

    /**
     * 创建日志对象，并初始化属性
     * @param request
     * @return
     */
    private PtSyslog getSysLog(HttpServletRequest request) {
//        return null;
        String requestPath = WebUtil.getRequestURIWithParam(request);
        String ip = WebUtil.getClientIpAddr(request);
        String referer = request.getHeader("Referer");
        String userAgent = request.getHeader("User-Agent");
        String cookie = request.getHeader("Cookie");
        String method = request.getMethod();
        String xRequestedWith = request.getHeader("X-Requested-With");
        String host = request.getHeader("Host");
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");
        String accept = request.getHeader("Accept");
        String connection = request.getHeader("Connection");

        PtSyslog reqSysLog = new PtSyslog();
        reqSysLog.set("id", ToolUtil.getUuidByJdk(true));
        reqSysLog.set("ip", ip);
        reqSysLog.set("requestpath", requestPath);
        reqSysLog.set("referer", referer);
        reqSysLog.set("useragent", userAgent);
        reqSysLog.set("cookie", cookie);
        reqSysLog.set("method", method);
        reqSysLog.set("xrequestedwith", xRequestedWith);
        reqSysLog.set("host", host);
        reqSysLog.set("accept_language", acceptLanguage);
        reqSysLog.set("accept_encoding", acceptEncoding);
        reqSysLog.set("accept", accept);
        reqSysLog.set("connection", connection);
        return reqSysLog;
    }
}
