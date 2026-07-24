package com.spt.bas.purchase.wx.server.util;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.pm.constant.PmConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 部门工具类
 * @Author: gaojy
 * @create 2022/12/6 10:11
 * @version: 1.0
 * @description:
 */
// Phase 8 (D-P8-01 minimal fix, behavior-equivalent): explicit bean name disambiguates this from
// com.spt.bas.server.util.DeptUtils (both @Component, simple name DeptUtils → default bean name
// "deptUtils" collides → ConflictingBeanDefinitionException). Source isolated via separate apps;
// monolith's broad com.spt scan requires explicit name. Mirrors FileController precedent.
// No semantic change.
@Component("wxDeptUtils")
public class DeptUtils {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    /**
     * 根据用户ID及部门类型查询指定类型的用户所在部门
     * @param userId
     * @param deptType
     * @return
     */
    public SysDeptSdk getDeptByUserIdAndDeptType(Long userId, String deptType){
        if (Objects.isNull(userId)){
            return null;
        }
        if (StringUtils.isBlank(deptType)){
            deptType = PmConstants.NODE_TYPE_DEPT;
        }
        SysDeptSdk dept = authOpenFacade.findDeptByUserId(userId);
        return getDept(dept, deptType);
    }

    /**
     * 递归查询指定类型部门
     * @param dept
     * @param deptType
     * @return
     */
    private SysDeptSdk getDept(SysDeptSdk dept, String deptType) {
        if (Objects.nonNull(dept)) {
            if (StringUtils.equals(deptType, dept.getDeptType())) {
                return dept;
            } else {
                dept = dept.getParent();
                getDept(dept, deptType);
            }
        }
        return dept;
    }
}
