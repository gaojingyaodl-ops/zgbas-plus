/**
 *
 */
package com.spt.bas.web.shiro;

import com.spt.auth.sdk.entity.SysAppSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.tools.core.util.SpringContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @author huangjian
 *
 */
@Component("shiroUtil")
public class ShiroUtil extends com.spt.tools.shiro.util.ShiroUtil {


    public static final String APPID = "appId";
    public static final String ENTERPRISEID = "enterpriseId";
    public static final String INDUSTRY = "industry";
    public static final String DEPTID = "deptId";
    public static final String DEPTABBR = "deptAbbr";

    private static final Logger log = LoggerFactory.getLogger(ShiroUtil.class);

    /** 获取当前登录用户对应的企业id */
    public static Long getCurrAppId() {

        Long appId = null;
        // 系统对应的应用
        List<BsDictData> dictDatas = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.BS_DICT_TYPE_SYSCONFIG);
        for (BsDictData bsDictData : dictDatas) {
            // 查找appCode
            if(BasConstants.ZG_APP_CODE.equals(bsDictData.getDictCd()) && bsDictData.getEnableFlg()){
                IAuthOpenFacade authOpenFacade = SpringContextHolder.getBean(IAuthOpenFacade.class);
                String appCode = bsDictData.getDictName();
                SysAppSdk app = authOpenFacade.findAppByCode(appCode);
                if(Objects.isNull(app)){
                    log.error("com.spt.bas.web.shiro.ShiroUtil.getCurrAppId() -> 获取应用id失败！");
                    return null;
                }
                return app.getId();
            }
        }
        log.error("com.spt.bas.web.shiro.ShiroUtil.getCurrAppId() -> 获取应用id失败！");
        return appId;
    }

    /** 获取登录用户企业id */
    public static Long getEnterpriseId() {
        return getProp(ENTERPRISEID);
    }

    /** 获取登录用户企业所属行业 */
    public static String getIndustry() {
        return getProp(INDUSTRY);
    }

    /** 获取登录用户部门简码 */
    public static String getDeptAbbr() {
        return getPropStr(DEPTABBR);
    }

    /** 获取登录用户部门ID */
    public static Long getDeptId() {
        return getProp(DEPTID);
    }

}
