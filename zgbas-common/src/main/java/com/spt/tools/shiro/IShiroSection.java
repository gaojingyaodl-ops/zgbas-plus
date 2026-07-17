package com.spt.tools.shiro;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;

/**
 * 默认资源初始化接口
 *
 * @author wlddh
 * @sea {@link ShiroChainMetaSource} {@link ShiroSectionDefault}
 */
@FunctionalInterface
public interface IShiroSection {

	void initSection(Section section);

	static void initDefault(Ini.Section section) {
		section.put("/open/**", "anon");
		section.put("/ws/**", "anon");
		section.put("/wx/**", "anon");
		section.put("/register/**", "anon");
		section.put("/static/**", "anon");
		section.put("/favicon.ico", "anon");
		section.put("/static/**", "anon");
		// 内部 server API(BasServer 合并进单体后的自回环目标):旧微服务架构下为独立进程、不受 Web Shiro 管,
		// 单体内复刻为 anon,避免登录期间 Feign 自回环落到 /**=user 兜底造成循环依赖。
		section.put("/spt-bas-server/**", "anon");
		section.put("/login", "authc");
		section.put("/logout", "logout");
	}
}
