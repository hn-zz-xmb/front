package com.meishi.front.common.dll;

import com.meishi.util.ImageUtils;
import com.meishi.front.config.PropertyConfig;
import com.meishi.util.FileUtils;
import com.meishi.util.ToolUtil;

public class WeixinStoreImgDll {

	private static final int WIDTH=360;
    private static final int HEIGHT=214;
    
    public static String getImage(String path){
    	String old_root= PropertyConfig.me().getProperty("config.temp");
        String oldPath=old_root+path;

        String root= PropertyConfig.me().getProperty("config.wx");
        String loadPath=FileUtils.generateDateDir(root)+"/"+ ToolUtil.getUuidByJdk(true)+".jpg";
        String newPath=root+loadPath;
        boolean result=new ImageUtils().GetPic(oldPath, newPath, HEIGHT, WIDTH, 80);
        if(!result) return "";
        
        String root1= PropertyConfig.me().getProperty("config.pc");
        FileUtils.generateDateDir(root1);
        String newPath1=root1+loadPath;
        boolean result1=new ImageUtils().GetPic(oldPath, newPath1, HEIGHT, WIDTH, 80);
        if(!result1) return "";
        return loadPath;
    }
}
