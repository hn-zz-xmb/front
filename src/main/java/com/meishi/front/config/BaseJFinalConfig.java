package com.meishi.front.config;

import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.meishi.front.common.controller.*;
import com.meishi.front.common.ueditor.UmeditorController;
import com.meishi.front.controller.DllTestController;
import com.meishi.front.controller.goods.CartController;
import com.meishi.front.controller.goods.FoodController;
import com.meishi.front.controller.goods.FoodPackageController;
import com.meishi.front.controller.goods.SetMealController;
import com.meishi.front.controller.index.*;
import com.meishi.front.controller.login.LoginController;
import com.meishi.front.controller.login.LogoutController;
import com.meishi.front.controller.member.MemberController;
import com.meishi.front.controller.member.RegisterController;
import com.meishi.front.controller.member.safecenter.*;
import com.meishi.front.controller.member.usercenter.*;
import com.meishi.front.controller.order.BuyCouponController;
import com.meishi.front.controller.order.OrderController;
import com.meishi.front.controller.order.couponpay.CouponAlipayController;
import com.meishi.front.controller.order.couponpay.CouponNotifyController;
import com.meishi.front.controller.pay.PayController;
import com.meishi.front.controller.pay.alipay.AlipayBankController;
import com.meishi.front.controller.pay.alipay.AlipayController;
import com.meishi.front.controller.pay.alipay.AlipayNotifyController;
import com.meishi.front.controller.store.*;
import com.meishi.front.controller.store.admin.*;
import com.meishi.front.controller.store.admin.material.MaterialGoodsManageController;
import com.meishi.front.controller.store.admin.material.MaterialOrderController;
import com.meishi.front.controller.store.joingame.BigwheelController;
import com.meishi.front.controller.store.joingame.EggsController;
import com.meishi.front.controller.yun.OrderPayYunController;
import com.meishi.front.controller.yun.report.CardReportController;
import com.meishi.front.controller.yun.report.MaterialReportController;
import com.meishi.front.controller.yun.report.MenuReportController;
import com.meishi.front.handler.XssHandler;
import com.meishi.front.thread.TimerResources;
import com.meishi.model.*;
import com.meishi.model.yun.*;
import com.meishi.util.beetl.render.CustomBeetlRenderFactory;

public class BaseJFinalConfig extends JFinalConfig {
	private static Logger log = Logger.getLogger(BaseJFinalConfig.class);

	@Override
	public void configConstant(Constants constants) {
		PropertyConfig conf = PropertyConfig.me();
		conf.loadPropertyFile("/init.properties");

		constants.setEncoding(conf.getProperty("config.encode"));
		constants.setDevMode(conf.getPropertyToBoolean("config.devMode"));
		constants.setUploadedFileSaveDirectory(conf
				.getProperty("config.temp"));
		constants.setMainRenderFactory(new CustomBeetlRenderFactory());

		// GroupTemplate groupTemplate = CustomBeetlRenderFactory.groupTemplate;
		// groupTemplate.registerFormat("dateFormat", new MyDateFormat());
		// groupTemplate.registerFunctionPackage("strutil", new StringFormat());

		constants.setError404View("/error/404.html");
		constants.setError401View("/error/401.html");
		constants.setError403View("/error/403.html");
		constants.setError500View("/error/500.html");

	}

	@Override
	public void configHandler(Handlers handlers) {
		// log.debug("开启Xss过滤");
		handlers.add(new XssHandler("/static"));
		// 开启全局日志处理
		// handlers.add(new GlobalHandler());
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new SessionInViewInterceptor());
	}

	@Override
	public void configPlugin(Plugins plugins) {
		// sqlserver
		// loadPropertyFile("/init.properties");
		PropertyConfig config = PropertyConfig.me();
		// C3p0Plugin cp = new C3p0Plugin(config.getProperty("jdbc.url"),
		// config.getProperty("jdbc.username"),
		// config.getProperty("jdbc.password"),
		// config.getProperty("jdbc.driverClassName")); // 关键：使用C3P0
		// plugins.add(cp);
		log.info("configPlugin 配置Druid数据库连接池连接属性");
		DruidPlugin druidPlugin = new DruidPlugin(
				config.getProperty("jdbc.url"),
				config.getProperty("jdbc.username"),
				config.getProperty("jdbc.password"),
				config.getProperty("jdbc.driverClassName"));

		log.info("configPlugin 配置Druid数据库连接池大小");
		druidPlugin.set(config.getPropertyToInt("db.initialSize"),
				config.getPropertyToInt("db.minIdle"),
				config.getPropertyToInt("db.maxActive"));
		plugins.add(druidPlugin);

		plugins.add(new EhCachePlugin());

		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		plugins.add(arp);
		arp.setDialect(new AnsiSqlDialect());// 关键：使用AnsiSqlDialect
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		arp.setShowSql(true);

		arp.addMapping("pt_syslog", PtSyslog.class);
		arp.addMapping("pt_resources", PtResources.class);
		arp.addMapping("member", Member.class);
		arp.addMapping("food", Food.class);
		arp.addMapping("goods_type", GoodsType.class);
		arp.addMapping("goods_statistics", GoodsStatistics.class);
		arp.addMapping("goods_img", GoodsImg.class);
		arp.addMapping("store", Store.class);
		arp.addMapping("dataDic", Datadic.class);
		arp.addMapping("city", City.class);
		arp.addMapping("province", Province.class);
		arp.addMapping("area", Area.class);
		arp.addMapping("street", Street.class);
		arp.addMapping("set_meal", SetMeal.class);
		arp.addMapping("food_package", FoodPackage.class);
		arp.addMapping("member_link", MemberLink.class);
		arp.addMapping("advert", Advert.class);
		arp.addMapping("article", Article.class);
		arp.addMapping("article_type", ArticleType.class);
		arp.addMapping("store_catagory", StoreCatagory.class);
		arp.addMapping("store_level", StoreLevel.class);
		arp.addMapping("invite_info", InviteInfo.class);
		arp.addMapping("appraise", Appraise.class);
		arp.addMapping("cart_item", CartItem.class);
		arp.addMapping("business", Business.class);
		arp.addMapping("set_meal_category", SetMealCategory.class);
		arp.addMapping("set_meal_item", SetMealItem.class);
		arp.addMapping("food_package_item", FoodPackageItem.class);
		arp.addMapping("sign_in", SignIn.class);
		arp.addMapping("coin", Coin.class);
		arp.addMapping("coin_trade_record", CoinTradeRecord.class);
		arp.addMapping("account", Account.class);
		arp.addMapping("withdraw", Withdraw.class);
		arp.addMapping("points", Points.class);
		arp.addMapping("orders", Orders.class);
		arp.addMapping("order_detail", OrderDetail.class);
		arp.addMapping("store_navigate", StoreNavigate.class);
		arp.addMapping("coupon", Coupon.class);
		arp.addMapping("member_coupon", MemberCoupon.class);
		arp.addMapping("invite_info", InviteInfo.class);
		arp.addMapping("active_prize_info", ActivePrizeInfo.class);
		arp.addMapping("active_prize_item", ActivePrizeItem.class);
		arp.addMapping("active_program", ActiveProgram.class);
		arp.addMapping("active_program_item", ActiveProgramItem.class);
		arp.addMapping("active_type", ActiveType.class);
		arp.addMapping("recharge_order", RechargeOrder.class);
		arp.addMapping("weixin_store_matter", WeixinStoreMatter.class);
		arp.addMapping("weixin_store_carereply", WeixinStoreCarereply.class);
		arp.addMapping("weixin_store_conn", WeixinStoreConn.class);
		arp.addMapping("weixin_store_menu", WeixinStoreMenu.class);
		arp.addMapping("material_order", MaterialOrder.class);
		arp.addMapping("material_order_detail", MaterialOrderDetail.class);
		arp.addMapping("delivery", Delivery.class);
		arp.addMapping("material", Material.class);
		arp.addMapping("material_statistics", MaterialStatistics.class);
		arp.addMapping("material_type", MaterialType.class);
		arp.addMapping("material_img", MaterialImg.class);
		arp.addMapping("material_unit", MaterialUnit.class);
		arp.addMapping("receive_address", ReceiveAddress.class);
		arp.addMapping("friend_url", FriendUrl.class);
		arp.addMapping("collect", Collect.class);
		arp.addMapping("coupon_order", CouponOrder.class);
		arp.addMapping("recommend_member", RecommendMember.class);
		arp.addMapping("recommend_apply", RecommendApply.class);
		arp.addMapping("recommend_relation", RecommendRelation.class);
		arp.addMapping("recommend_money", RecommendMoney.class);
		arp.addMapping("platform_trade_record", PlatformTradeRecord.class);
		arp.addMapping("platform_coin", PlatformCoin.class);
		arp.addMapping("order_appraise",OrderAppraise.class);
        arp.addMapping("store_back_appraise", StoreBackAppraise.class);
        arp.addMapping("store_credit",StoreCredit.class);
        arp.addMapping("message",Message.class);
        arp.addMapping("food_type",FoodType.class);
        
        arp.addMapping("food_image", FoodImage.class);
        arp.addMapping("subscribe_order", SubscribeOrder.class);
        arp.addMapping("subscribe_order_item", SubscribeOrderItem.class);
        arp.addMapping("takeout_order", TakeoutOrder.class);
        arp.addMapping("takeout_order_item", TakeoutOrderItem.class);




		log.info("configPlugin 配置Druid数据库连接池连接属性");
		DruidPlugin druidPlugin1 = new DruidPlugin(
				config.getProperty("jdbc1.url"),
				config.getProperty("jdbc1.username"),
				config.getProperty("jdbc1.password"),
				config.getProperty("jdbc1.driverClassName"));

		log.info("configPlugin 配置Druid数据库连接池大小");
		druidPlugin1.set(config.getPropertyToInt("db.initialSize"),
				config.getPropertyToInt("db.minIdle"),
				config.getPropertyToInt("db.maxActive"));
		plugins.add(druidPlugin1);

		//plugins.add(new EhCachePlugin());

		ActiveRecordPlugin arp1 = new ActiveRecordPlugin("yun",druidPlugin1);
		plugins.add(arp1);
		arp1.setDialect(new AnsiSqlDialect());// 关键：使用AnsiSqlDialect
		arp1.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		arp1.setShowSql(true);

		//arp1.addMapping("takeout_order_item", TakeoutOrderItem.class);
		arp1.addMapping("order_pay", OrderPayYun.class);
		arp1.addMapping("food", FoodYun.class);
		arp1.addMapping("area", AreaYun.class);
		arp1.addMapping("card", CardYun.class);
		arp1.addMapping("card_record", CardRecordYun.class);
		arp1.addMapping("desk", DeskYun.class);
		arp1.addMapping("employee", EmployeeYun.class);
		arp1.addMapping("food_type", FoodTypeYun.class);
		arp1.addMapping("order_item", OrderItemYun.class);
		arp1.addMapping("[order]", OrderYun.class);
		arp1.addMapping("anti_order", AntiOrderYun.class);
		arp1.addMapping("shift_record", ShiftRecordYun.class);
		arp1.addMapping("order_item_return", OrderItemReturnYun.class);

		arp1.addMapping("material", MaterialYun.class);
		arp1.addMapping("material_type", MaterialTypeYun.class);
		arp1.addMapping("material_operator", MaterialOperatorYun.class);
		arp1.addMapping("material_in_store", MaterialInStoreYun.class);
		arp1.addMapping("material_inventory", MaterialInStoreYun.class);
		arp1.addMapping("material_loss", MaterialLossYun.class);
		arp1.addMapping("material_return", MaterialReturnYun.class);
		arp1.addMapping("material_stock", MaterialStockYun.class);
	}

	@Override
	public void configRoute(Routes routes) {
		routes.add("/", IndexController.class);
		routes.add("/help", HelpController.class);
		routes.add("/goodsSearch", GoodsSearchController.class);
		routes.add("/storeSearch", StoreSearchController.class);
		routes.add("/setmealSearch", SetMealSearchController.class);
		routes.add("/foodpackageSearch", FoodPackageSearchController.class);
		routes.add("/msg", SendMsgController.class);
		routes.add("/email", SendEmailController.class);
		routes.add("/upload", UploadController.class);
		routes.add("/umeditor", UmeditorController.class);
		routes.add("/store", StoreController.class);
		routes.add("/store/inviteInfo", InviteController.class);
		routes.add("/store/appraise", AppraiseController.class);
		routes.add("/goods", FoodController.class);
		routes.add("/setmeal", SetMealController.class);
		routes.add("/foodpackage",FoodPackageController.class);
		routes.add("/cart", CartController.class);
		routes.add("/order", OrderController.class);
		routes.add("/account/userinfo", UserInfoController.class);
		routes.add("/city", CityController.class);
		routes.add("/login", LoginController.class);
		routes.add("/article", ArticleController.class);
		routes.add("/logout", LogoutController.class);
		routes.add("/account", AccountController.class);
		routes.add("/account/recharge", RechargeOrderController.class);
		routes.add("/account/withdraw", WithDrawController.class);
		routes.add("/account/security", SecurityController.class);
		routes.add("/account/email", AccountEmailController.class);
		routes.add("/alipaybank", AlipayBankController.class);
		routes.add("/alipay", AlipayController.class);
		routes.add("/order/pay", PayController.class);
		routes.add("/alipayNotify", AlipayNotifyController.class);
		routes.add("/openstore", OpenStoreController.class);
		routes.add("/register", RegisterController.class);
		routes.add("/captcha", CaptchaController.class);
		routes.add("/salerManage/ordermanage", OrderManageController.class);
		routes.add("/salerManage", SalerManageController.class);
		routes.add("/salerManage/sureOrder", VerifyConsumeCodeController.class);
		routes.add("/buyerOrder", BuyerOrderController.class);
		routes.add("/buyerMaterOrder",BuyerMaterialOrderController.class);
		routes.add("/buyer", BuyerController.class);
		routes.add("/member", MemberController.class);
		routes.add("/active", ActiveController.class);
		routes.add("/storeManage", StoreManagerController.class);
		routes.add("/salerManage/goodsmanage", FoodManageController.class);
		routes.add("/salerManage/category", StoreCategoryController.class);
		routes.add("/salerManage/foodtype", FoodTypeController.class);
		routes.add("/salerManage/navigate", NavigationController.class);
		routes.add("/salerManage/setmeal", SetMealManageController.class);
		routes.add("/salerManage/foodpackage", FoodPackageManageController.class);
		routes.add("/salerManage/coupon", CouponManageController.class);
		routes.add("/buycoupon", BuyCouponController.class);
		routes.add("/couponpay", CouponAlipayController.class);
		routes.add("/couponpayNotify", CouponNotifyController.class);
		routes.add("/salerManage/inviteInfo", InviteInfoManageController.class);
		routes.add("/salerManage/activeCenter",
				ActiveCenterManageController.class);
		routes.add("/salerManage/joingame", GameController.class);
		routes.add("/salerManage/playgamebigwheel", BigwheelController.class);
		routes.add("/salerManage/playgameeggs", EggsController.class);
		routes.add("/salerManage/prizeItem",
				ActivePrizeItemManageController.class);
		routes.add("/salerManage/prizeInfo", ActivePrizeInfoController.class);
		routes.add("/salerManage/materialManage/ordermanage",
				MaterialOrderController.class);
		routes.add("/salerManage/materialManage/delivery",
				DeliveryController.class);
		routes.add("/salerManage/weixin/matter", WxMatterController.class);
		routes.add("/salerManage/weixin/conn", WxConnController.class);
		routes.add("/salerManage/weixin/menu", WeixinMenuController.class);
		routes.add("/salerManage/materialgoodsmanage",
				MaterialGoodsManageController.class);

		routes.add("/safecenter", SecurityController.class);
		routes.add("/safecenter/email", EmailController.class);
		routes.add("/safecenter/pwd", PwdController.class);
		routes.add("/safecenter/paypwd", PayPwdController.class);
		routes.add("/safecenter/phone", PhoneController.class);

		routes.add("/store/collect", CollectController.class);
		routes.add("/recommendApply", RecommendApplyController.class);
		routes.add("/recommend", RecommendController.class);
		routes.add("/appraise",OrderAppraiseController.class);
		routes.add("/account/message",MessageController.class);
        routes.add("/salerManage/backAppraise", StoreBackAppraiseController.class);
        routes.add("/salerManage/appraise",OrderAppraiseManageController.class);
        

        routes.add("test", DllTestController.class);
        
        //yun
        ///routes.add("yun",test", DllTestController.class);
        //routes.add("/salerManage/orderPayYun",OrderPayYunController.class);
		// 报表
		routes.add("/salerManage/yun/report/menu", MenuReportController.class);
		routes.add("/salerManage/yun/report/card", CardReportController.class);
		routes.add("/salerManage/yun/report/material", MaterialReportController.class);

	}

	@Override
	public void afterJFinalStart() {
		log.info("afterJFinalStart 启动操作日志入库线程");
		// ThreadSysLog.startSaveDBThread();

		log.info("afterJFinalStart 启动系统监控线程");
		TimerResources.start();
	}

	@Override
	public void beforeJFinalStop() {
		log.info("beforeJFinalStop 停止操作日志入库线程");
		// ThreadSysLog.setThreadRun(false);

		log.info("afterJFinalStart 停止系统监控库线程");
		TimerResources.stop();
	}

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8080, "/", 5);
	}

}
