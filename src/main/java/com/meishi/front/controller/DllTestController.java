package com.meishi.front.controller;


import com.meishi.util.ImageUtils;

/**
 * Created by Develop_RSP on 2015/3/17.
 */
public class DllTestController extends BaseController {

    public void index(){
        new ImageUtils().test();
        renderText("--");
    }
}
