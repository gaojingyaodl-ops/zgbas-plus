/**
 * 
 */
package com.spt.tools.shiro;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;
import org.springframework.beans.factory.FactoryBean;

/**
 * 初始化资源控制
 * 
 * @author huangjian
 * 
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {
	private IShiroSection shiroSection;
	private String filterChainDefinitions;

	/**
	 * 通过filterChainDefinitions对默认的url过滤定义
	 */
	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	@Override
	public Section getObject() throws Exception {
		// 获取所有Resource

		Ini ini = new Ini();
		// 加载默认的url
		ini.load(filterChainDefinitions);
		Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		shiroSection.initSection(section);
		return section;
	}

	@Override
	public Class<?> getObjectType() {
		return this.getClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setShiroSection(IShiroSection shiroSection) {
		this.shiroSection = shiroSection;
	}

}
