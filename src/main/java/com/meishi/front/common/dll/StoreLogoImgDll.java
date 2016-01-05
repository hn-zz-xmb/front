package com.meishi.front.common.dll;

import java.io.File;

import com.meishi.util.ImageUtils;
import com.meishi.front.config.PropertyConfig;
import com.meishi.util.FileUtils;
import com.meishi.util.ToolUtil;

public class StoreLogoImgDll {

	private static final int PC_WIDTH=80;
    private static final int PC_HEIGHT=80;
    
    private static final int WX_WIDTH=103;
    private static final int WX_HEIGHT=103;
    
    public static String getImage(String path){
    	String old_root= PropertyConfig.me().getProperty("config.temp");
        String oldPath=old_root+path;

        File file = new File(oldPath);
        if(!file.exists()){
        	return null;
        }

        String root= PropertyConfig.me().getProperty("config.pc");
        String loadPath=FileUtils.generateDateDir(root)+"/"+ ToolUtil.getUuidByJdk(true)+".jpg";
        String newPath=root+loadPath;
        boolean result=new ImageUtils().GetPic(oldPath, newPath, PC_HEIGHT, PC_WIDTH, 80);
        if(!result) return null;
        
        String root1= PropertyConfig.me().getProperty("config.wx");
        FileUtils.generateDateDir(root1);
        String newPath1=root1+loadPath;
        boolean result1=new ImageUtils().GetPic(oldPath, newPath1, WX_HEIGHT, WX_WIDTH, 80);
        if(!result1) return null;
        return loadPath;
    }
}
