/**
 * 
 */
package com.spt.tools.core.cmd;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spt.tools.core.cache.LocalCacheManager;

/**
 * @author huangjian
 *
 */
public class CommandExecutor implements Runnable {
	private static Logger log = LoggerFactory.getLogger(CommandExecutor.class);

	@Autowired(required = false)
	private ICommand commandExecutor;

	public void run() {
		// 初始化命令行指令输入流
		Scanner sc = new Scanner(System.in);
		try {
			while (true) {
				System.out.print(">>");
				String cmd = sc.nextLine();

				if (cmd == null || cmd.length() == 0) // 空输入
				{
					continue;
				}
				if (innerCmd(cmd)) {
					continue;
				}
				outCmd(cmd);

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			sc.close();
		}

	}

	private boolean innerCmd(String cmd) {
		if (cmd.trim().equalsIgnoreCase("exit")) {
			// 退出进程
			log.info("application exit");
			System.exit(0);
		} else if (cmd.trim().equalsIgnoreCase("show client count")) {
			// 显示连接中的客户端数量
			log.info("show client count");
		} else if (cmd.trim().equalsIgnoreCase("show runtime")) {
			// 显示runtime信息
			showRuntimeInfo();
		} else if (cmd.trim().equalsIgnoreCase("show memory")) {
			// 显示内存信息
			showMemoryInfo();
		} else if (cmd.trim().equalsIgnoreCase("refresh cache")) {
			// 刷新缓存
			LocalCacheManager.refreshAll();
		} else {
			return false;
		}
		log.info("inner command executed");
		return true;
	}

	private void outCmd(String cmd) {
		if (commandExecutor != null) {
			try {
				boolean rst = commandExecutor.executeCommand(cmd);
				if (rst) {
					log.info("command executed!");
				}else {
					log.info("unknown command!");
				}
			} catch (Exception e) {
				log.error("command executed error!", e);
			}
		}
	}

	/**
	 * 显示运行时信息
	 */
	private void showRuntimeInfo() {
		RuntimeMXBean rmb = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
		log.info("ClassPath: " + rmb.getClassPath());
		log.info("LibraryPath: " + rmb.getLibraryPath());
		log.info("VmVersion: " + rmb.getVmVersion());
	}

	/**
	 * 显示内存信息
	 */
	private void showMemoryInfo() {
		int m = (int) Runtime.getRuntime().totalMemory() / 1024;
		log.info("Total VM memory is " + m);
		m = (int) Runtime.getRuntime().freeMemory() / 1024;
		log.info("Free VM memory is " + m);
		log.info("Max VM memeory is " + Runtime.getRuntime().maxMemory() / 1024);
	}
}
