package com.meishi.front.controller.store.admin;

import java.io.Serializable;
import java.math.BigDecimal;

public class ActivePrizeItemVo implements Serializable {
	private String item_name;
	private String item_desc;
	private Integer endday;
	private BigDecimal couponmoney;
	private BigDecimal limitmoney;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_desc() {
		return item_desc;
	}

	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}

	public Integer getEndday() {
		return endday;
	}

	public void setEndday(Integer endday) {
		this.endday = endday;
	}

	public BigDecimal getCouponmoney() {
		return couponmoney;
	}

	public void setCouponmoney(BigDecimal couponmoney) {
		this.couponmoney = couponmoney;
	}

	public BigDecimal getLimitmoney() {
		return limitmoney;
	}

	public void setLimitmoney(BigDecimal limitmoney) {
		this.limitmoney = limitmoney;
	}
}
