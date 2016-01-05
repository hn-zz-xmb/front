package com.meishi.front.common.dll;

import com.meishi.util.ImageUtils;
import com.meishi.front.config.PropertyConfig;
import com.meishi.util.FileUtils;
import com.meishi.util.ToolUtil;

public class GoodsImgDll {

	private static final int PC_LARGE_WIDTH=569;
    private static final int PC_LARGE_HEIGHT=341;
    
    private static final int PC_SMALL_WIDTH=276;
    private static final int PC_SMALL_HEIGHT=218;
    
    private static final int PC_MINI_WIDTH=114;
    private static final int PC_MINI_HEIGHT=68;
    
    private static final int WX_LARGE_WIDTH=360;
    private static final int WX_LARGE_HEIGHT=300;
    
    private static final int WX_SMALL_WIDTH=103;
    private static final int WX_SMALL_HEIGHT=103;
    
    public static String getLargeImage(String path){
    	String old_root= PropertyConfig.me().getProperty("config.temp");
        String oldPath=old_root+path;

        String root= PropertyConfig.me().getProperty("config.pc");
        String loadPath=FileUtils.generateDateDir(root)+"/"+ ToolUtil.getUuidByJdk(true)+".jpg";
        String newPath=root+loadPath;
        boolean result=new ImageUtils().GetPic(oldPath, newPath, PC_LARGE_HEIGHT, PC_LARGE_WIDTH, 80);
        if(!result) return "";
        
        String root1=PropertyConfig.me().getProperty("config.wx");
        String newPath1=root1+loadPath;
        boolean result1=new ImageUtils().GetPic(oldPath, newPath1, WX_LARGE_HEIGHT, WX_LARGE_WIDTH, 80);
        if(!result1) return "";
        return loadPath;
    }
    
    public static String getSmallImage(String path){
    	String old_root= PropertyConfig.me().getProperty("config.temp");
        String oldPath=old_root+path;

        String root= PropertyConfig.me().getProperty("config.pc");
        String loadPath=FileUtils.generateDateDir(root)+"/"+ ToolUtil.getUuidByJdk(true)+".jpg";
        String newPath=root+loadPath;
        boolean result=new ImageUtils().GetPic(oldPath, newPath, PC_SMALL_HEIGHT, PC_SMALL_WIDTH, 80);
        if(!result) return "";
        
        String root1=PropertyConfig.me().getProperty("config.wx");
        FileUtils.generateDateDir(root1);
        String newPath1=root1+loadPath;
        boolean result1=new ImageUtils().GetPic(oldPath, newPath1, WX_SMALL_HEIGHT, WX_SMALL_WIDTH, 80);
        if(!result1) return "";
        return loadPath;
    }
    
    public static String getMiniImage(String path){
    	String old_root= PropertyConfig.me().getProperty("config.temp");
        String oldPath=old_root+path;

        String root= PropertyConfig.me().getProperty("config.pc");
        String loadPath=FileUtils.generateDateDir(root)+"/"+ ToolUtil.getUuidByJdk(true)+".jpg";
        String newPath=root+loadPath;
        boolean result=new ImageUtils().GetPic(oldPath, newPath, PC_MINI_HEIGHT, PC_MINI_WIDTH, 80);
        if(!result) return "";
        return loadPath;
    }
    
}
