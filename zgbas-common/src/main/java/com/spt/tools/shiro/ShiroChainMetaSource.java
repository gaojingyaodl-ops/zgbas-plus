/**
 * 
 */
package com.spt.tools.shiro;

import org.apache.shiro.config.Ini.Section;
import org.springframework.beans.factory.annotation.Autowired;

import com.spt.tools.shiro.util.ShiroUtil;

/**
 * 初始化资源控制
 * 
 * @author huangjian
 * 
 */
public class ShiroChainMetaSource implements IShiroSection{
	@Autowired(required = false)
	private IShiroService shiroService;

	@Override
	public void initSection(Section section) {
		IShiroSection.initDefault(section);
		if (shiroService != null) {
			shiroService.initSection(section, ShiroUtil.appCd);
		}
		// 其他地址都需要登录
		section.put("/**", "user");
	}
	
	public void setShiroService(IShiroService shiroService) {
		this.shiroService = shiroService;
	}

}
