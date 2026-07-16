/**
 *
 */
package com.spt.bas.web.shiro;

import com.spt.auth.sdk.entity.SysMenuSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.MenuSearchVo;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.shiro.IShiroService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangjian
 *
 */
@Component
public class ShiroService  implements IShiroService{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public boolean initSection(Ini.Section section, String appCd) {
        try {
            initMenu(section,appCd);
            // initResource(section,appCd);
            ShiroUtil.filterInited = true;
            return true;
        } catch (Exception e) {
            logger.warn("初始化权限资源失败",e);
        }
        return false;
    }

	private void initMenu(Ini.Section section,String appCd){
		logger.info(">>>>>>init menu to permission<<<<<<");
        MenuSearchVo searchVo = new MenuSearchVo();
        searchVo.setAppCode(appCd);
        searchVo.setEnableFlg(true);
        List<SysMenuSdk> menus = authOpenFacade.findAllMenu(searchVo);
        for (SysMenuSdk menu : menus) {
			List<String> perms = new ArrayList<>();
//			if (StringUtils.isNotEmpty(menu.getComponent())) {
//				for (SysPermission permission : menu.getPermissions()) {
//					perms.add(permission.getPermCd());
//				}
//			}
			String strPerms = CollectionUtil.array2String(perms);
			if (StringUtils.isNotBlank(strPerms)) {
				section.put(menu.getComponent(), MessageFormat.format(PREMISSION_STRING, strPerms));
			}
		}
	}

//    private void initResource(Ini.Section section,String appCd){
//        logger.info(">>>>>>init resource to permission<<<<<<");
//        List<SysResource> list = adminOpenClient.findResourceByApp(appCd);
//        // 循环Resource的url,逐个添加到section中。section就是filterChainDefinitionMap,
//        // 里面的键就是链接URL,值就是存在什么条件才能访问该链接
//        if (list==null) {
//            return;
//        }
//        for (Iterator<SysResource> it = list.iterator(); it.hasNext();) {
//            SysResource resource = it.next();
//            // 如果不为空值添加到section中
//            if (StringUtils.isNotBlank(resource.getAuth())){
//                //如果授权方式不为空，直接添加到section中，
//                section.put(resource.getUrl(), resource.getAuth());
//            }else{
//                //如果授权方式为空，根据权限表配置
//                List<String> perms = new ArrayList<String>();
//                if (StringUtils.isNotEmpty(resource.getUrl())) {
//                    for (SysPermission permission : resource.getPermissions()) {
//                        perms.add(permission.getPermCd());
//                    }
//                }
//                String strPerms = CollectionUtil.array2String(perms);
//                if (StringUtils.isNotBlank(strPerms)) {
//                    section.put(resource.getUrl(), MessageFormat.format(PREMISSION_STRING, strPerms));
//                }
//            }
//        }
//    }

}
