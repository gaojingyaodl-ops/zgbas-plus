package com.spt.tools.shiro;

import org.apache.shiro.config.Ini;

/**
 * 资源动态加载接口
 * 
 * @author wlddh
 *
 */
public interface IShiroService {

	boolean initSection(Ini.Section section, String appCd);

}
