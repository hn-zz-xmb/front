package com.meishi.front.common.dll;

import java.io.File;

import com.meishi.util.ImageUtils;
import com.meishi.front.config.PropertyConfig;
import com.meishi.util.FileUtils;
import com.meishi.util.ToolUtil;

public class StoreBannerImgDll {

	private static final int WIDTH = 1180;
	private static final int HEIGHT = 150;
	
	public static String getImage(String path){
		String old_root = PropertyConfig.me().getProperty("config.temp");
		String oldPath = old_root + path;

		File file = new File(oldPath);
        if(!file.exists()){
        	return null;
        }

		String root = PropertyConfig.me().getProperty("config.pc");
		String loadPath = FileUtils.generateDateDir(root) + "/"
				+ ToolUtil.getUuidByJdk(true) + ".jpg";
		String newPath = root + loadPath;
		boolean result = new ImageUtils().GetPic(oldPath, newPath,
				HEIGHT, WIDTH, 80);
		if (!result)
			return null;

		String root1= PropertyConfig.me().getProperty("config.wx");
		FileUtils.generateDateDir(root1);
		String newPath1=root1+loadPath;
		boolean result1=new ImageUtils().GetPic(oldPath, newPath1, WIDTH, HEIGHT, 80);
		if(!result1) return null;

		return loadPath;
	}
	
}
