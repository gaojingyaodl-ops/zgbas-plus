package com.spt.bas.report.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.spt.bas.report.client.entity.RptInvoiceBillStatistics;
import com.spt.bas.report.client.vo.RptFunderVo;
import com.spt.bas.report.client.vo.RptInvoiceBillStatisticsVo;
import com.spt.bas.report.server.dao.RptInvoiceBillMapper;
import com.spt.bas.report.server.dao.RptInvoiceBillStatisticsMapper;
import com.spt.bas.report.server.service.IRptInvoiceBillStatisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RptInvoiceBillStatisticsServiceImpl implements IRptInvoiceBillStatisticsService {
    @Autowired
    private RptInvoiceBillStatisticsMapper rptInvoiceBillStatisticsMapper;
    @Autowired
    private RptInvoiceBillMapper rptInvoiceBillMapper;

    @Override
    public List<RptInvoiceBillStatistics> findInvoiceBillStatistics(RptInvoiceBillStatisticsVo searchVo) {
        List<RptInvoiceBillStatistics> invoiceBillStatisticsList = new ArrayList<>();
        String ourCompanyName = searchVo.getOurCompanyName();
        // 没有查看所有的权限
        if (!searchVo.getViewAllFlg()) {
            RptFunderVo funderVo = rptInvoiceBillMapper.selectFunderByUserId(searchVo.getUserId());
            // 判断是否是资金方
            if (funderVo != null) {
                String companyNames = funderVo.getCompanyNames();
                if (StringUtils.isEmpty(companyNames)) {
                    return invoiceBillStatisticsList;
                }
                // 判断是否搜索我方
                if (StringUtils.isNotEmpty(ourCompanyName)) {
                    if (companyNames.contains(ourCompanyName)) {
                        searchVo.setOurCompanyNameList(Collections.singletonList(ourCompanyName));
                    } else {
                        return invoiceBillStatisticsList;
                    }
                } else {
                    searchVo.setOurCompanyNameList(Arrays.asList(companyNames.split(",")));
                }
            } else {
                return invoiceBillStatisticsList;
            }
        } else {
            if (StringUtils.isNotEmpty(ourCompanyName)) {
                searchVo.setOurCompanyNameList(Collections.singletonList(ourCompanyName));
            }
        }
        //获取开票数据
        List<RptInvoiceBillStatistics> invoiceList = rptInvoiceBillStatisticsMapper.findInvoiceBill(searchVo);
        // 获取收票数据
        List<RptInvoiceBillStatistics> invoiceReceiveList = rptInvoiceBillStatisticsMapper.findInvoiceReceiveBill(searchVo);
        // 合并数据
        List<RptInvoiceBillStatistics> resultList = mergeInvoiceLists(invoiceList, invoiceReceiveList);
        if (CollectionUtil.isNotEmpty(resultList)) {
            resultList.sort((invoice1, invoice2) -> invoice2.getInvoiceDate().compareTo(invoice1.getInvoiceDate()));
        }
        return resultList;
    }

    public List<RptInvoiceBillStatistics> mergeInvoiceLists(List<RptInvoiceBillStatistics> invoiceList, List<RptInvoiceBillStatistics> invoiceReceiveList) {
        // 使用 Map 来加速查找，key 是 "ourCompanyName + invoiceDate" 组合
        Map<String, RptInvoiceBillStatistics> invoiceMap = new HashMap<>();
        for (RptInvoiceBillStatistics invoice : invoiceList) {
            String key = invoice.getOurCompanyName() + "-" + invoice.getInvoiceDate();
            invoiceMap.put(key, invoice);
        }
        // 遍历 invoiceReceiveList，检查是否在 Map 中找到匹配项
        for (RptInvoiceBillStatistics receive : invoiceReceiveList) {
            String key = receive.getOurCompanyName() + "-" + receive.getInvoiceDate();
            // 如果找到匹配项，更新数据
            if (invoiceMap.containsKey(key)) {
                RptInvoiceBillStatistics matchedInvoice = invoiceMap.get(key);
                matchedInvoice.setInvoiceReceiveAmount(receive.getInvoiceReceiveAmount());
                matchedInvoice.setInvoiceReceiveNumber(receive.getInvoiceReceiveNumber());
            } else {
                // 如果没有找到匹配项，直接将 invoiceReceiveList 的对象添加到 invoiceList 中
                invoiceList.add(receive);
            }
        }
        return invoiceList;
    }
}
