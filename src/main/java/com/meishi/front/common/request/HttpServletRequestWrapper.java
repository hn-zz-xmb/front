package com.meishi.front.common.request;


import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rsp on 14-12-9.
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 重写并过滤getParameter方法
     */
    @Override
    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml4(super.getParameter(name));
    }

    /**
     * 重写并过滤getParameterValues方法
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (null == values){
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = StringEscapeUtils.escapeHtml4(values[i]);
        }
        return values;
    }

    /**
     * 重写并过滤getParameterMap方法
     */
    /**
     * 重写并过滤getParameterMap方法
     */
    @Override
    public Map getParameterMap() {
        HashMap<String,String[]> paraMap = new HashMap(super.getParameterMap());
        // 对于paraMap为空的直接return
        if (null == paraMap || paraMap.isEmpty()) {
            return paraMap;
        }

        for (Map.Entry entry : paraMap.entrySet()) {
            String key = (String)entry.getKey();
            String[] values     = (String[]) entry.getValue();
            if (null == values) {
                continue;
            }
            String[] newValues  = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                newValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
            }
            paraMap.put(key, newValues);
        }
        return paraMap;
    }
}
