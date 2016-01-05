package com.meishi.front.common.controller;

import com.meishi.front.controller.BaseController;
import com.meishi.util.beetl.render.CaptchaRender;

/**
 * Created by rsp on 14-11-17.
 */
public class CaptchaController extends BaseController {

    /**
     *
     */
	public void index() {
		CaptchaRender img = new CaptchaRender(4);
		setSessionAttr("register_code", img.getMd5RandonCode());
		render(img);
	}
}
