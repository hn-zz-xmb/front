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

public class BigwheelController extends BaseController {
	private static Logger log = Logger.getLogger(AppraiseController.class);

	/**
	 * \ // Object[][] prizeArr = new Object[][]{ //
	 * //id,min,max，prize【奖项】,v【中奖率】 // //外面的转盘转动 // // {1,1,14,"一等奖",1}, // //
	 * {2,346,364,"一等奖",1}, // // {3,16,44,"不要灰心",10}, // //
	 * {4,46,74,"神马也没有",10}, // // {5,76,104,"祝您好运",10}, // //
	 * {6,106,134,"二等奖",2}, // // {7,136,164,"再接再厉",10}, // //
	 * {8,166,194,"神马也没有",10}, // // {9,196,224,"运气先攒着",10}, // //
	 * {10,226,254,"三等奖",5}, // // {11,256,284,"要加油哦",10}, // //
	 * {12,286,314,"神马也没有",10}, // // {13,316,344,"谢谢参与",10} // // //里面的指针转动 //
	 * {1,1,14,"一等奖",1}, // {2,346,364,"一等奖",1}, // {3,16,44,"不要灰心",10}, //
	 * {4,46,74,"神马也没有",10}, // {5,76,104,"祝您好运",10}, // {6,106,134,"二等奖",2}, //
	 * {7,136,164,"再接再厉",10}, // {8,166,194,"神马也没有",10}, //
	 * {9,196,224,"运气先攒着",10}, // {10,226,254,"三等奖",5}, //
	 * {11,256,284,"要加油哦",10}, // {12,286,314,"神马也没有",10}, //
	 * {13,316,344,"谢谢参与",10} // };
	 * 
	 */
	public void award() {
		JSONObject json = new JSONObject();
		Member member = Member.dao.findById(getUserIds());
		String storeId = getPara("storeId");
		String programId = getPara("programId");
		ActiveProgram activeProgram = ActiveProgram.dao.findById(programId);
		// 查询是否中奖
		Integer prizeCount = ActivePrizeInfo.dao.prizeCount(
				member.getStr("id"), DateUtil.format(new Date(), "yyyyMMdd")
						+ "%", storeId, programId);
		if (prizeCount > 0) {
			json.put("msg", "你已中奖，今天不能在抽奖了");
			json.put("isRight", false);
			renderJson(json.toString());
			return;
		}

		// 查询是否超过限制
		// String time = "";

		Integer count = ActivePrizeInfo.dao.findCountBydate(
				member.getStr("id"), storeId,
				activeProgram.getStr("activetype_id"), programId,
				DateUtil.format(new Date(), "yyyyMMdd") + "%");
		if (count >= activeProgram.getInt("repeat_prize_count")) {
			json.put("msg", "你的抽奖次数已经用完!");
			json.put("isRight", false);
			renderJson(json.toString());
			return;
		}

		List<ActiveProgramItem> activeProgramItems = ActiveProgramItem.dao
				.findActiveProgramItem(programId);
		// 封装

		List<Object[]> prizeArr = new ArrayList<Object[]>();

		Integer totalCount = 0;// 计算总数
		for (int i = 0; i < activeProgramItems.size(); i++) {
			totalCount += activeProgramItems.get(i).getInt("item_count");
		}

		// 设置未中奖信息
		double cal_isPrize = totalCount
				/ (double) activeProgram.getInt("partake_num");// 中奖概率
		Object[] result_ = new Object[6];
		result_[0] = "-1";
		result_[1] = 31;
		result_[2] = 89;
		result_[3] = "你未中奖";
		result_[4] = (int) ((1 - cal_isPrize) * 100);
		result_[5] = "";
		prizeArr.add(result_);

		// 设置中奖信息
		for (int i = 0; i < activeProgramItems.size(); i++) {
			ActiveProgramItem item = activeProgramItems.get(i);
			if (totalCount == 0)
				continue;
			double cal = item.getInt("item_count")
					/ (double) activeProgram.getInt("partake_num");// 每次概率
			if (item.getInt("px") == 1) {
				Object[] r1 = new Object[6];
				r1[0] = item.get("id");
				r1[1] = 1;
				r1[2] = 29;
				r1[3] = item.get("name");
				r1[4] = (int) (cal * 100);
				r1[5] = item.get("active_prize_item_id");
				prizeArr.add(r1);

				Object[] r2 = new Object[6];
				r2[0] = item.get("id");
				r2[1] = 331;
				r2[2] = 359;
				r2[3] = item.get("name");
				r2[4] = (int) (cal * 100);
				r2[5] = item.get("active_prize_item_id");
				prizeArr.add(r2);
			} else {
				Object[] result = new Object[6];
				result[0] = item.get("id");
				result[1] = 360 - (item.getInt("px") - 1) * 60 - 30 + 1;
				result[2] = 360 - (item.getInt("px") - 2) * 60 - 30 - 1;
				result[3] = item.getStr("name");
				result[4] = (int) (cal * 100);
				result[5] = item.get("active_prize_item_id");
				prizeArr.add(result);
			}
		}
		Object result[] = award(prizeArr);// 抽奖后返回角度和奖品等级
		// 1 得到奖项 更新数量
		String itemId = (String) result[3];// 判断是否中奖
		String provavilit = (String) result[4];// 奖项类型ID

		// 1.更新奖品数量
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

		json.put("angle", result[0]);
		json.put("msg", result[2]);
		json.put("isRight", true);
		renderJson(json.toString());
	}

	// 抽奖并返回角度和奖项
	public Object[] award(List<Object[]> prizeArr) {
		// 概率数组
		Integer obj[] = new Integer[prizeArr.size()];
		for (int i = 0; i < prizeArr.size(); i++) {
			obj[i] = (Integer) prizeArr.get(i)[4];
		}
		Integer prize = getRand(obj); // 根据概率获取奖项id
		// 旋转角度
		int angle = new Random().nextInt((Integer) prizeArr.get(prize)[2]
				- (Integer) prizeArr.get(prize)[1])
				+ (Integer) prizeArr.get(prize)[1];
		String msg = (String) prizeArr.get(prize)[3];// 提示信息
		String prizeId = (String) prizeArr.get(prize)[0];// 提示信息
		return new Object[] { angle, prize, msg, prizeId,
				prizeArr.get(prize)[5] };
	}

	// 根据概率获取奖项
	public Integer getRand(Integer obj[]) {
		Integer result = null;
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
