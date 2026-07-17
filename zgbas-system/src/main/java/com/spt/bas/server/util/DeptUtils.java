package com.spt.bas.server.util;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.OwnRegionEnum;
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
@Component
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
        SysDeptSdk dept = null;
        SysUserSdk user = authOpenFacade.findUserById(userId);
        if (Objects.nonNull(user) && Objects.nonNull(user.getDeptId())){
            dept = authOpenFacade.findDeptById(user.getDeptId());
        }
        return getDept(dept, deptType);
    }

    /**
     * 根据用户ID及部门类型查询指定类型的用户所在部门
     * @param deptId
     * @param deptType
     * @return
     */
    public SysDeptSdk getDeptByDeptIdAndDeptType(Long deptId, String deptType){
        if (Objects.isNull(deptId)){
            return null;
        }
        if (StringUtils.isBlank(deptType)){
            deptType = PmConstants.NODE_TYPE_DEPT;
        }
        SysDeptSdk dept = authOpenFacade.findDeptById(deptId);
        return getDept(dept, deptType);
    }

    /**
     * 查询用户所属区域
     * @param sysDept
     * @return
     */
    public String getOwningRegion(SysDeptSdk sysDept) {
        return Objects.nonNull(sysDept) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDept.getDeptName()))
                ? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDept.getDeptName())).getRegionCode()
                : "";
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
                Long parentId = dept.getParentId();
                if (Objects.nonNull(parentId)) {
                    SysDeptSdk parent = authOpenFacade.findDeptById(parentId);
                    return getDept(parent, deptType);
                }
            }
        }
        return dept;
    }
}
