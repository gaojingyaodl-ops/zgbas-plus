package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.entity.RptCompanyReceivables;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptCompanyReceivablesSearchVo;
import com.spt.bas.report.server.dao.RptCompanyReceivablesMapper;
import com.spt.bas.report.server.service.IRptCompanyReceivablesService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class RptCompanyReceivablesServiceImpl implements IRptCompanyReceivablesService {

    @Autowired
    private RptCompanyReceivablesMapper rptCompanyReceivablesMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    
    
    /**
     * 开票分页查询
     *
     */
    @Override
    public Page<RptCompanyReceivables> findRptCompanyReceivablesPage(RptCompanyReceivablesSearchVo searchVo) {
        List<RptCompanyReceivables> companyReceivablesList = new ArrayList<>();
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptCompanyReceivablesMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                Page<RptCompanyReceivables> pageVo = new PageImpl<>(companyReceivablesList, pageable, searchVo.getCount());
                return pageVo;
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        
        List<RptCompanyReceivables> list = rptCompanyReceivablesMapper.findRptCompanyReceivablesPage(searchVo);
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCompanyReceivables> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }
    
    /**
     * 开票分页查询
     *
     */
    @Override
    public Page<RptCompanyReceivables> findRptCompanyReceivablesDetailPage(RptCompanyReceivablesSearchVo searchVo) {
        List<RptCompanyReceivables> companyReceivablesList = new ArrayList<>();
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptCompanyReceivablesMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                Page<RptCompanyReceivables> pageVo = new PageImpl<>(companyReceivablesList, pageable, searchVo.getCount());
                return pageVo;
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        
        List<RptCompanyReceivables> list = rptCompanyReceivablesMapper.findRptCompanyReceivablesDetailPage(searchVo);
        if (CollectionUtils.isNotEmpty(list)) {
            DeptSearchVo deptSearchVo = new DeptSearchVo(BasConstants.ZG_ENTERPRISE_ID);
            List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
            Map<Long, SysDeptSdk> deptMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(deptAll)) {
                deptMap = deptAll.stream()
                        .collect(Collectors.toMap(SysDeptSdk::getDeptId, dept -> dept, (existing, replacement) -> existing));
            }
            for (RptCompanyReceivables receivables : list) {
                SysDeptSdk dept = deptMap.get(receivables.getDeptId());
                if (Objects.nonNull(dept)) {
                    receivables.setDeptName(dept.getDeptName());
                } else {
                    Long matchUserId = receivables.getMatchUserId();
                    SysUserSdk user = authOpenFacade.findUserById(matchUserId);
                    SysDeptSdk sysDeptSdk = deptMap.get(user.getDeptId());
                    if (Objects.nonNull(sysDeptSdk)) {
                        receivables.setDeptName(sysDeptSdk.getDeptName());
                    }
                }
            }
        }
        
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptCompanyReceivables> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }

    @Override
    public RptCompanyReceivables findRptCompanyReceivablesSum(RptCompanyReceivablesSearchVo searchVo) {
        List<String> ourCompanyNameList = new ArrayList<>();
        if(searchVo.getFunderFlg()) {
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptCompanyReceivablesMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
            } else {
                
                return new RptCompanyReceivables();
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);
        return rptCompanyReceivablesMapper.findRptCompanyReceivablesSum(searchVo);
    }
}
