package com.meishi.front.controller.index;

import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Article;
import com.meishi.model.ArticleType;
import com.meishi.model.Datadic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleController extends BaseController {
	public void articleDetail() {
		String typeId = getPara("typeId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("typeId", typeId);
		Article article = Article.dao.findByArticleId(getPara("articleId"));
		List<Article> top = Article.dao.findByarticleType(typeId);
		List<ArticleType> articleTypes = ArticleType.dao.findAll();
		List<Datadic> datadics = Datadic.dao.findByGroup("articleType");
		setAttr("articleTypes", articleTypes);
		setAttr("datadics", datadics);
		setAttr("params", params);
		setAttr("article", article);
		setAttr("top", top);
		setAttr("typeId", typeId);
		render("/index/article.html");
	}

	public void articleList() {
		String typeId = getPara("typeId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("typeId", typeId);
		Integer pageNum = getParaToInt("pageNum");
		Integer pageSize = getParaToInt("pageSize");
		if (pageNum == null || pageNum.intValue() < 1) {
			pageNum = 1;// 默认第一页
		}
		if (pageSize == null || pageSize.intValue() < 0) {
			pageSize = 12;// 默认每页显示10条
		} else if (pageSize.intValue() > 100) {
			pageSize = 12;// 最大100条
		}
		List<Article> top = Article.dao.findByarticleType(typeId);
		List<Datadic> datadics = Datadic.dao.findByGroup("articleType");
		List<ArticleType> articleTypes = ArticleType.dao.findAll();
		ArticleType articleType = ArticleType.dao.findById(typeId);
		Datadic datadic = Datadic.dao.findByCode(typeId);
		setAttr("articleTypes", articleTypes);
		setAttr("articleType", articleType);
		setAttr("top", top);
		Page<Article> result = Article.dao.findArticle(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		setAttr("typeId", typeId);
		render("/index/articleList.html");
	}
}
