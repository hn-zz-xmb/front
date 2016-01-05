package com.meishi.front.controller.store.joingame;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.controller.store.AppraiseController;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class EggsController extends BaseController {
	private static Logger log = Logger.getLogger(AppraiseController.class);

	/**
	 * // Object[][] prizeArr = new Object[][] { // // 里面的指针转动 // { 1, "平板电脑", 3
	 * }, // { 2, "数码相机", 5 }, // { 3, "音箱设备", 10 }, // { 4, "4G优盘", 12 }, // {
	 * 5, "Q币10元", 20 }, // {6, "下次没准就能中哦", 50 } // };
	 * 
	 */
	public void award() {
		JSONObject json = new JSONObject();
		String storeId = getPara("storeId");
		String programId = getPara("programId");
		Member member = Member.dao.findById(getUserIds());
		ActiveProgram activeProgram = ActiveProgram.dao.findById(programId);
		List<ActiveProgramItem> activeProgramItems = ActiveProgramItem.dao
				.findActiveProgramItem(programId);
		// 查询是否中奖
		Integer prizeCount = ActivePrizeInfo.dao.prizeCount(
				member.getStr("id"), DateUtil.format(new Date(), "yyyyMMdd")
						+ "%", storeId, programId);
		if (prizeCount > 0) {
			json.put("msg", 3);
			renderJson(json.toString());
			return;
		}

		// 查询是否超过限制
		Integer count = ActivePrizeInfo.dao.findCountBydate(
				member.getStr("id"), storeId,
				activeProgram.getStr("activetype_id"), programId,
				DateUtil.format(new Date(), "yyyyMMdd") + "%");
		if (count >= activeProgram.getInt("repeat_prize_count")) {
			json.put("msg", 2);
			renderJson(json.toString());
			return;
		}
		// 封装

		List<Object[]> prizeArr = new ArrayList<Object[]>();

		Integer totalCount = 0;// 计算总数
		for (int i = 0; i < activeProgramItems.size(); i++) {
			totalCount += activeProgramItems.get(i).getInt("item_count");
		}

		// 设置未中奖信息
		double cal_isPrize = totalCount
				/ (double) activeProgram.getInt("partake_num");// 中奖概率
		Object[] result_ = new Object[4];
		result_[0] = "-1";
		result_[1] = "你未中奖";
		result_[2] = (int) ((1 - cal_isPrize) * 100);
		result_[3] = "";
		prizeArr.add(result_);

		for (int i = 0; i < activeProgramItems.size(); i++) {
			Object[] result = new Object[4];
			ActiveProgramItem item = activeProgramItems.get(i);
			double cal = item.getInt("item_count")
					/ activeProgram.getInt("partake_num");// 概率
			result[0] = item.get("id");
			result[1] = item.get("name");
			result[2] = (int) (cal * 100);
			result[3] = item.get("active_prize_item_id");
			prizeArr.add(result);
		}

		Integer prizeId = getRand(prizeArr);

		String itemId = (String) prizeArr.get(prizeId)[0];
		String provavilit = (String) prizeArr.get(prizeId)[3];
		if (StringUtil.isNotBlank(itemId) && !"-1".equals(itemId)) {
			ActiveProgramItem.dao.updateCount(itemId);// 更新数量
		}
		Store store = Store.dao.findById(activeProgram.get("store_id"));
		ActivePrizeItem item = ActivePrizeItem.dao.findById(provavilit);
		ActivePrizeInfo info = new ActivePrizeInfo();
		info.set("id", ToolUtil.getUuidByJdk(true))
				.set("user_id", member.get("id"))
				.set("store_id", activeProgram.get("store_id"))
				.set("store_name", store.get("name"))
				.set("active_type_id", activeProgram.get("activetype_id"))
				.set("active_program_id", activeProgram.get("id"))
				.set("active_program_name", activeProgram.get("name"))
				.set("active_program_item_id", itemId)
				.set("prize_code", "JL" + GenerateUtils.consumCode())
				.set("status", 1)
				.set("loose_time", activeProgram.get("end_time"))
				.set("prize_time",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("prize_item_name",
						item != null ? item.get("item_name") : "");

		ActivePrizeInfo.dao.saveInfo(info, item, member.getStr("id"));
		json.put("prize", prizeArr.get(prizeId)[1]);
		json.put("msg", prizeId == 0 ? 0 : 1);// 提示信息);
		renderJson(json.toString());
	}

	// 根据概率获取奖项
	public Integer getRand(List<Object[]> prizeArr) {
		Integer result = null;
		Integer obj[] = new Integer[prizeArr.size()];
		for (int i = 0; i < prizeArr.size(); i++) {
			obj[i] = (Integer) prizeArr.get(i)[2];
		}

		try {
			int sum = 0;// 概率数组的总概率精度
			for (int i = 0; i < obj.length; i++) {
				sum += obj[i];
			}
			for (int i = 0; i < obj.length; i++) {// 概率数组循环
				int randomNum = new Random().nextInt(sum);// 随机生成1到sum的整数
				if (randomNum < obj[i]) {// 中奖
					result = i;
					break;
				} else {
					sum -= obj[i];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
