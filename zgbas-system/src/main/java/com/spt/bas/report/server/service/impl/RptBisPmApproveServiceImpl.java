package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptPmApproveMapper;
import com.spt.bas.report.server.service.IRptBisPmApproveService;
import com.spt.tools.core.bean.RespVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 10:14
 */
@Service
public class RptBisPmApproveServiceImpl implements IRptBisPmApproveService {

    @Autowired
    private RptPmApproveMapper pmApproveMapper;

    /**
     * 中光业务系统查询相关审批单数据接口
     *
     * @param searchVo 查询参数
     * @return 结果
     */
    @Override
    public RespVo<?> getPmApproveByUserId(RptBisSearchVo searchVo) {
        RespVo<List<RptBisPmApproveVo>> result = new RespVo<>();
        List<RptBisPmApproveResVo> approveResVoList = pmApproveMapper.getPmApproveByParam(searchVo);
        if (CollectionUtils.isEmpty(approveResVoList)) {
            result.setData(new ArrayList<>());
            return result;
        }
        List<RptBisPmApproveVo> resList = new ArrayList<>();
        for (RptBisPmApproveResVo e : approveResVoList) {
            RptBisPmApproveVo data = new RptBisPmApproveVo();
            data.setApproveNo(e.getApproveNo());
            if (StringUtils.equals(searchVo.getType(), "CL")) {
                setCL(e, data, resList);
            } else if (StringUtils.equals(searchVo.getType(), "GG")) {
                setGG(e, data, resList);
            }
        }
        result.setData(resList);
        return result;
    }

    @Override
    public List<RptCtrContractSettlementDateVo> getSettlementBusinessDate(RptCtrContractSettlementDateSearchVo searchVo) {
        return pmApproveMapper.getSettlementBusinessDate(searchVo);
    }


    /**
     * 公关费用
     *
     * @param e
     * @param data
     * @param resList
     */
    private void setGG(RptBisPmApproveResVo e, RptBisPmApproveVo data, List<RptBisPmApproveVo> resList) {
        data.setType("GG");
        if (StringUtils.equals(e.getProcessCode(), BasConstants.PROCESS_APPLY_MANAGE_BUSINESS_PAY)
                && StringUtils.equals(e.getCostType(), "12")) {
            // 管理费用申请
            data.setMoney(e.getDealAmount());
            resList.add(data);
        } else if (StringUtils.equals(e.getProcessCode(), BasConstants.PROCESS_APPLY_OPERATING_BUSINESS_PAY)
                && StringUtils.equals(e.getCostType(), "6")) {
            // 经营费用申请
            data.setMoney(e.getDealAmount());
            resList.add(data);
        }
    }

    /**
     * 差旅费用
     *
     * @param e
     * @param data
     * @param resList
     */
    private static void setCL(RptBisPmApproveResVo e, RptBisPmApproveVo data, List<RptBisPmApproveVo> resList) {
        data.setType("CL");
        if (StringUtils.equals(e.getProcessCode(), BasConstants.PROCESS_APPLY_MANAGE_BUSINESS_PAY)
                && StringUtils.equals(e.getCostType(), "10")) {
            // 管理费用申请
            data.setMoney(e.getDealAmount());
            resList.add(data);
        } else if (StringUtils.equals(e.getProcessCode(), BasConstants.PROCESS_APPLY_OPERATING_BUSINESS_PAY)
                && StringUtils.equals(e.getCostType(), "4")) {
            // 经营费用申请
            data.setMoney(e.getDealAmount());
            resList.add(data);
        }
    }
}
