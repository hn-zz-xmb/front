package com.meishi.front.common;

import com.jfinal.log.Logger;
import com.meishi.front.config.PropertyConfig;

public abstract class AppContextData {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AppContextData.class);


	/**
	 * # 域名或者服务器IP，多个逗号隔开，验证Referer时使用
	 */

	public static final String DOMAIN = PropertyConfig.me().getProperty(
			"config.domain");
	// 默认城市
	public static final String DEFAULT_CITY = "402882e448303b070148303fce95007a";
	// 平台账号
	public static final String PLATFORM_COIN_ID = "F06EC4D95BA44DE1A5F9A9B72B6C09C2";
	// 订单状态
	public static final String ORDER_STATUS = "order_status";
	// 帮助中心id
	public static final String USER_HELP = "";
	public static final String PAYED = "payed";// 已付款(未消费)
	public static final String SUBMITTED = "submitted";// 已提交(未付款)
	public static final String FINISHED = "finished";// 已完成(已消费)
	public static final String CLOSED = "closed";// 关闭
	public static final String SHIPPED = "shipped";// 已配送
	// 店铺审核状体
	public static final String AUDITED = "audited";
	public static final String UNAUDITED = "unaudited";
	public static final String REFUSE = "refuse";
	// 店铺类型
	public static final String NORMAL = "normal";
	public static final String MATERIAL = "material";
	// 消费方式
	public static final String ARRIVE_EAT = "arrive_eat";// 到店消费
	public static final String TAKE_OUT = "take_out";// 外卖
	// 收藏类型
	public static final String COLLECT_TYPE = "collect_type";
	public static final String COLLECT_GOODS = "collect_goods";
	public static final String COLLECT_STORE = "collect_store";
	public static final String COLLECT_POST = "collect_post";
	public static final String COLLECT_PLATE = "collect_plate";

	public static final String MEMBER_OPERATE_REGIST = "regist";// 用户操作:注册
	public static final String MEMBER_OPERATE_RESETPWD = "resetPwd";// 用户操作,修改密码
	public static final String MEMBER_OPERATE_MODIFYPWD = "modifyPwd";// 用户操作,修改密码
	public static final String MEMBER_OPERATE_MODIFYPAYPWD = "modifyPayPwd";// 用户操作,修改密码
	public static final String MEMBER_OPERATE_VERIFYEMAIL = "verifyEmail";// 用户操作,绑定邮箱
	public static final String MEMBER_OPERATE_MODIFYPHONE = "modifyPhone";// 用户操作,修改手机号码
	public static final String MEMBER_OPERATE_REGISTER_STORE = "registerStore";// 用户操作,注册店铺

	
	/**
	 * cai
	 */	
    //已下单
    public static final String ORDER_ITEM_STATUS_ALREADY = "already";
    //已付款
    public static final String ORDER_ITEM_STATUS_PAYED = "payed";
    //已退单
    public static final String ORDER_ITEM_STATUS_RETURNED = "returned";
    //撤单
    public static final String ORDER_ITEM_STATUS_CANCEL = "cancel";    
    //已支付
    public static final String ORDER_STATUS_PAYD = "payed";
    //开单
    public static final String ORDER_STATUS_INIT = "init";
    //撤单
    public static final String ORDER_STATUS_CANCEL = "cancel";
	
	/**
	 * 用户登录状态码
	 */
	public static final int login_info_0 = 0;// 用户不存在
	public static final int login_info_1 = 1;// 停用账户
	public static final int login_info_2 = 2;// 密码错误次数超限
	public static final int login_info_3 = 3;// 密码验证成功
	public static final int login_info_4 = 4;// 密码验证失败

	// 订单付款类型
	public static final String INTERNAL = "I";// 平台支付
	public static final String EXTERNAL = "E";// 支付宝支付

	/**
	 * 推荐人申请状态
	 */
	// public static final String
	// RECOMMEND_APPLY_STATUS="recommend_apply_status";

	public static final String RECOMMEND_APPLY_GOING = "going";
	public static final String RECOMMEND_APPLY_SUCCESS = "success";
	public static final String RECOMMEND_APPLY_FAILD = "faild";

	/*
	 * 返代金券
	 * */
	public static final String coupon_source_activity="coupon_activity";//活动获得
	public static final String coupon_source_buy="coupon_buy";//购买
	public static final String coupon_source_return="coupon_return";//店家返还
	
	/**
	 * 订单状态
	 * 
	 * 实现带有抽象方法的枚举
	 * 
	 * @author jiqinlin
	 * applied("退款"), success("成功"), faild("失败"), going("进行中")	,applyreturn("买家申请退货"),closed("交易关闭")
	 * cancel("买家取消"),
	 */
	public enum OrderStatus {
		// 通过括号赋值,而且必须带有一个参构造器和一个属性跟方法，否则编译出错
		// 赋值必须都赋值或都不赋值，不能一 部分赋值一部分不赋值；如果不赋值则不能写构造器，赋值编译也出错
		submitted("未付款"), payed("已付款"), finished("已消费"),
		cancelled("已取消"),shipped("卖家已经发货"),
		refunding("申请退款"),refunded("退款完成"),ruchued("已入厨");

		private final String value;
		private static final OrderStatus[] copyOfValues = values();

		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		OrderStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static String forValue(String name) {
			for (OrderStatus value : copyOfValues) {
				if (value.name().equals(name)) {
					return value.getValue();
				}
			}
			return null;
		}
	}

	/**
	 * 店铺活动类型
	 *
	 */
	public enum StoreActiveType {
		bigwheel("大转盘"), eggs("砸金蛋"), hangcard("刮刮卡");
		private final String value;

		StoreActiveType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * 银行枚举
	 * 
	 * 实现带有抽象方法的枚举
	 * 
	 * @author jiqinlin
	 *
	 */
	public enum Bank {
		// 通过括号赋值,而且必须带有一个参构造器和一个属性跟方法，否则编译出错
		// 赋值必须都赋值或都不赋值，不能一部分赋值一部分不赋值；如果不赋值则不能写构造器，赋值编译也出错
		CMD("CMB-DEBIT"), CCB("CCB-DEBIT"), ICBC("ICBC-DEBIT"), COMM(
				"COMM-DEBIT"), GDB("GDB-DEBIT"), BOC("BOC-DEBIT"), CEB(
				"CEB-DEBIT"), SPDB("SPDB-DEBIT"), PSBC("PSBC-DEBIT"), BJBANK(
				"BJBANK"), SHRCB("SHRCB"), WZCBB2C("WZCBB2C-DEBIT"), CMBC(
				"CMBC"), BJRCB("BJRCB"), SPA("SPA-DEBIT");

		private final String value;
		private static final Bank[] copyOfValues = values();

		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		Bank(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static boolean checkValue(String value) {
			for (Bank value_ : copyOfValues) {
				if (value_.value.equals(value)) {
					return true;
				}
			}
			return false;
		}
	}

}
