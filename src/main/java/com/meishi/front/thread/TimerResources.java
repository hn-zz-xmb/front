package com.meishi.front.thread;

import com.meishi.model.PtResources;
import com.meishi.util.DateUtil;
import com.meishi.util.OSUtil;
import com.meishi.util.ToolUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时记录系统资源负载情况
 * @author 董华健
 */
public class TimerResources extends Timer {

	private static Logger log = Logger.getLogger(TimerResources.class);
	
	/**
	 * 定时任务对象
	 */
	private static TimerResources timer = null;
	
	/**
	 * 启动任务
	 */
	public static void start() {
		if(null != timer){
			log.info("启动失败，任务已经启动");
			return;
		}
		log.info("开始启动任务");
		timer = new TimerResources();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				log.info("任务执行开始");
				String osName = OSUtil.getOsName(); // 获取操作系统类型名称
				String ip = OSUtil.getOsLocalHostIp(); // 获取本机IP
				String hostName = OSUtil.getOsLocalHostName(); // 获取本机名称
				int cpuNumber = OSUtil.getOsCpuNumber(); // 获取CPU数量
				double cpuRatio = OSUtil.getOscpuRatio(); // cpu使用率
				if(cpuRatio < 0){
					cpuRatio = 0;
				}
				
				long phyMemory = OSUtil.getOsPhysicalMemory(); // 物理内存，总的可使用的
				long phyFreeMemory = OSUtil.getOsPhysicalFreeMemory(); // 物理内存，剩余
				
				long jvmTotalMemory = OSUtil.getJvmTotalMemory(); // JVM内存，内存总量
				long jvmFreeMemory = OSUtil.getJvmFreeMemory(); // JVM内存，空闲内存量
				long jvmMaxMemory = OSUtil.getJvmMaxMemory(); // JVM内存，最大内存量
				long gcCount = OSUtil.getJvmGcCount(); // 获取JVM GC次数

				PtResources resources = new PtResources();
				resources.set("id", ToolUtil.getUuidByJdk(true));
				resources.set("osname", osName);
				resources.set("ip", ip);
				resources.set("hostname", hostName);
				resources.set("cpunumber", cpuNumber);
				resources.set("cpuratio", cpuRatio);
				resources.set("phymemory", phyMemory);
				resources.set("phyfreememory", phyFreeMemory);
				resources.set("jvmtotalmemory", jvmTotalMemory);
				resources.set("jvmfreememory", jvmFreeMemory);
				resources.set("jvmmaxmemory", jvmMaxMemory);
				resources.set("gccount", gcCount);
				resources.set("createdate", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
				resources.save();
				
				log.info("任务执行结束");
			}
		}, 1000, 1000 * 60 * 2);// 启动项目一秒后执行，然后每次间隔2分钟
		log.info("启动任务完成");
	}
	
	/**
	 * 停止任务
	 */
	public static void stop(){
		if(null != timer){
			log.info("任务退出开始");
			timer.cancel();
			log.info("任务退出成功");
		}else{
			log.info("任务退出失败，任务为空");
		}
	}
	
}
