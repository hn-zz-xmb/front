package com.meishi.front.common.controller;

import com.jfinal.upload.UploadFile;
import com.meishi.front.controller.BaseController;
import net.sf.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by zen on 14-11-12.
 */
public class UploadController extends BaseController {

    public void index(){
        render("/upload.html");
    }

    /**
     * 上传图片
     */
    public void img(){
        JSONObject result=new JSONObject();
        String root=getFile().getSaveDirectory();
        String distPath=getFolder(root);
        //存储路径
        String savePath=root+distPath+"/";
        UploadFile file= getFile("file",savePath);
        String fileName=getName(file.getOriginalFileName());
        file.getFile().renameTo(new File(root+distPath+"/"+fileName));
        //访问路径
        String loadPath="/"+distPath+"/"+fileName;

        if(file == null){
            result.put("status","FAIL");
        }else {
            result.put("status", "SUCCESS");
            result.put("url", loadPath);
        }
        renderJson(result.toString());
    }


    /**
     * 文件类型判断
     *
     * @param fileName
     * @return
     */
    private boolean checkFileType(String fileName) {
//        Iterator<String> type = Arrays.asList(this.allowFiles).iterator();
//        while (type.hasNext()) {
//            String ext = type.next();
//            if (fileName.toLowerCase().endsWith(ext)) {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @return string
     */
    private String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 依据原始文件名生成新文件名
     * @return
     */
    private String getName(String fileName) {
        Random random = new Random();
        return "" + random.nextInt(10000)
                + System.currentTimeMillis() + this.getFileExt(fileName);
    }

    /**
     * 根据字符串创建本地目录 并按照日期建立子目录返回
     * @param path
     * @return
     */
    private String getFolder(String path) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        String tmpPath=formater.format(new Date());
        path += "/" +tmpPath;
//		File dir = new File(this.getPhysicalPath(path));
        File dir = new File(path);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                return "";
            }
        }
        return tmpPath;
    }

}
