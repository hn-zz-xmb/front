package com.meishi.front.common.ueditor;

import com.meishi.front.common.util.ImgCompress;
import com.meishi.front.config.PropertyConfig;
import com.meishi.front.controller.BaseController;
import com.meishi.util.StringUtil;
import com.meishi.util.upload.Uploader;

/**
 * Created by zen on 14-11-12.
 */
public class UmeditorController extends BaseController {
    public void index(){
        render("/umeditor.html");
    }




    //图片上传
    public void imageUp() throws Exception{
        Uploader up = new Uploader(getRequest());
//        up.setSavePath("/home/zen/upload");
        up.setSavePath(PropertyConfig.me().getProperty("config.temp"));
        String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
        up.setAllowFiles(fileType);
        up.setMaxSize(10000); //单位KB
        up.upload();
        
        //复制图片pc
        String file=PropertyConfig.me().getProperty("config.temp")+up.getLoadUrl();
        ImgCompress imgCom = new ImgCompress(file);  
        imgCom.setDest(PropertyConfig.me().getProperty("config.pc")+up.getLoadUrl());
        imgCom.resizeByWidth(891);
        
        //复制图片phone
        String file1=PropertyConfig.me().getProperty("config.temp")+up.getLoadUrl();
        ImgCompress imgCom1 = new ImgCompress(file);  
        imgCom1.setDest(PropertyConfig.me().getProperty("config.wx")+up.getLoadUrl());
        imgCom1.resizeByWidth(360); 
        
        String callback = getPara("callback");

        String result = "{\"name\":\""+ up.getFileName() +"\", \"originalName\": \""+ up.getOriginalName() +"\", \"size\": "+ up.getSize() +", \"state\": \""+ up.getState() +"\", \"type\": \""+ up.getType() +"\", \"url\": \""+ up.getLoadUrl() +"\"}";

        result = result.replaceAll( "\\\\", "\\\\" );

        if( StringUtil.isBlank(callback) ){
            getResponse().getWriter().print(result);
        }else{
//            String result_= "<script>"+ callback +"(" + result + ")</script>";
//            //getResponse().getWriter().print(StringEscapeUtils.unescapeHtml4(result_));
//            renderJson(StringEscapeUtils.unescapeHtml4(result_));
        }
    }

}
