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
		section.put("/login", "authc");
		section.put("/logout", "logout");
	}
}
