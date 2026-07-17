package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.vo.RptCtrContractFinanceSearch;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptCtrContractFinanceVoMapper;
import com.spt.bas.report.server.service.IRptCtrContractFinanceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @Author: wm
 * @Date: Created in 2022-06-21 10:24
 */
@Component
public class RptCtrContractFinanceServiceImpl implements IRptCtrContractFinanceService {

    @Autowired
    private RptCtrContractFinanceVoMapper ctrContractFinanceVoMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    /**
     * 合同列表
     *
     * @param vo 多查询条件
     */
    @Override
    public Page<RptCtrContractFinanceVo> findContractFinancePage(RptCtrContractFinanceSearch vo) {
        List<RptCtrContractFinanceVo> list = ctrContractFinanceVoMapper.findContractFinancePage(vo);

        if (CollectionUtils.isNotEmpty(list)) {
            DeptSearchVo deptSearchVo = new DeptSearchVo(BasConstants.ZG_ENTERPRISE_ID);
            List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
            Map<Long, SysDeptSdk> deptMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(deptAll)) {
                deptMap = deptAll.stream()
                        .collect(Collectors.toMap(SysDeptSdk::getDeptId, dept -> dept, (existing, replacement) -> existing));
            }
            for (RptCtrContractFinanceVo financeVo : list) {
                SysDeptSdk dept = deptMap.get(financeVo.getDeptId());
                if (Objects.nonNull(dept)) {
                    financeVo.setDeptName(dept.getDeptName());
                } else {
                    Long matchUserId = financeVo.getMatchUserId();
                    SysUserSdk user = authOpenFacade.findUserById(matchUserId);
                    SysDeptSdk sysDeptSdk = deptMap.get(user.getDeptId());
                    if (Objects.nonNull(sysDeptSdk)) {
                        financeVo.setDeptName(sysDeptSdk.getDeptName());
                    }
                }
            }
        }
        Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
        Page<RptCtrContractFinanceVo> pageVo = new PageImpl<>(list, pageable, vo.getCount());
        return pageVo;
    }
}
