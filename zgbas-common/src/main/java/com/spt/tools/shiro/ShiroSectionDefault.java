/**
 * 
 */
package com.spt.tools.shiro;

import org.apache.shiro.config.Ini.Section;

/**
 * 不做任何拦截
 * 
 * @author huangjian
 * 
 */
public class ShiroSectionDefault implements IShiroSection {

	@Override
	public void initSection(Section section) {
	}

}
