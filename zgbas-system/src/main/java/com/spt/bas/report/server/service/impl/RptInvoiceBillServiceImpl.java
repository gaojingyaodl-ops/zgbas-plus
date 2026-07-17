package com.spt.bas.report.server.service.impl;

import com.spt.auth.sdk.cache.UserCache;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.report.client.entity.RptInvoiceBill;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptInvoiceBillSearchVo;
import com.spt.bas.report.server.dao.RptInvoiceBillMapper;
import com.spt.bas.report.server.service.IRptInvoiceBillService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class RptInvoiceBillServiceImpl implements IRptInvoiceBillService {

    @Autowired
    private RptInvoiceBillMapper rptInvoiceBillMapper;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    /**
     * 开票分页查询
     *
     */
    @Override
    public Page<RptInvoiceBill> findContractFinancePage(RptInvoiceBillSearchVo searchVo) {
        List<RptInvoiceBill> invoiceBillList = new ArrayList<>();
        List<String> ourCompanyNameList = new ArrayList<>();
        String ourCompanyName = searchVo.getOurCompanyName();
        if(searchVo.getFunderFlg()) {
            // 资金方权限使用dcsxOurCompanyNameList查询，非资金方权限使用dcsxOurCompanyName查询
            searchVo.setOurCompanyName(null);
            // 根据当前用户查询资金方管理数据
            RptFunderVo funderVo = rptInvoiceBillMapper.selectFunderByUserId(searchVo.getUserId());
            if(Objects.nonNull(funderVo)) {
                String companyNames = funderVo.getCompanyNames();
                String[] split = companyNames.split(",");
                for (String companyName : split) {
                    ourCompanyNameList.add(companyName);
                }
                if(StringUtils.isNotEmpty(ourCompanyName)) {
                    ourCompanyNameList = new ArrayList<>();
                    Boolean existFlg = false;
                    // 判断查询条件是否包含在资金方内
                    for (String companyName : split) {
                        if(companyName.contains(ourCompanyName)) {
                            ourCompanyNameList.add(companyName);
                            existFlg = true;
                        }
                    }
                    if(!existFlg){
                        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                        Page<RptInvoiceBill> pageVo = new PageImpl<>(invoiceBillList, pageable, searchVo.getCount());
                        return pageVo;
                    }
                }

            } else {
                Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
                Page<RptInvoiceBill> pageVo = new PageImpl<>(invoiceBillList, pageable, searchVo.getCount());
                return pageVo;
            }

        }
        searchVo.setOurCompanyNameList(ourCompanyNameList);

        String invoiceBIllIds = searchVo.getInvoiceBillIds();
        if (StringUtils.isNotBlank(invoiceBIllIds)) {
            String[] split = invoiceBIllIds.split(",");
            List<Long> invoiceBIllIdList = Arrays.stream(split).filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            searchVo.setInvoiceBillIdList(invoiceBIllIdList);
        }
        String currApproveUserId = searchVo.getCurrApproveUserId();
        if (StringUtils.isNotBlank(currApproveUserId)) {
            searchVo.setCurrApproveUserId("|" + currApproveUserId + "|");
        }
        List<RptInvoiceBill> list = rptInvoiceBillMapper.findRptInvoiceBillPage(searchVo);

        if (CollectionUtils.isNotEmpty(list)) {
            for (RptInvoiceBill invoiceBill : list) {
                if (StringUtils.isEmpty(invoiceBill.getDcsxContractNo())) {
                    invoiceBill.setDcsxInvoiceStatus(null);
                }
                invoiceBill.setCurrApproveUserName(UserCache.getUserName(invoiceBill.getCurrApproveUserId()));
            }
        }
        Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
        Page<RptInvoiceBill> pageVo = new PageImpl<>(list, pageable, searchVo.getCount());
        return pageVo;
    }
}
