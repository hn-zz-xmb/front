package com.meishi.front.controller.index;

import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Article;
import com.meishi.model.Datadic;

import java.util.List;

//@Controller(controllerKey = "/")
public class HelpController extends BaseController {
	private static Logger log = Logger.getLogger(HelpController.class);

	public void index() {
		List<Article> article = Article.dao
				.findByarticleType(AppContextData.USER_HELP);
		List<Datadic> datadics = Datadic.dao.findByGroup("articleType");
		setAttr("article", article);
		setAttr("datadics", datadics);
		render("/index/help.html");
	}

	public void findHelpDetail() {
		Article article = Article.dao.findByArticleId(getPara("articleId"));
		setAttr("article", article);
		render("");
	}
}
