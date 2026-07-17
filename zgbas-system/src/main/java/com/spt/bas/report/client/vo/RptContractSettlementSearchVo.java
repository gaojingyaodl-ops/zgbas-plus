package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * 结算表查询 VO
 */
@Data
public class RptContractSettlementSearchVo extends PageSearchVo {
    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务员
     */
    private String matchUserId;

    /**
     * 货名
     */
    private String productsName;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 我方抬头列表
     */
    private List<String> ourCompanyNames;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 收/付款状态
     */
    private Boolean dealedFlg;

    /**
     * 财务查询的合同的状态
     */
    private String financeContractStatus;

    /**
     * 签订日开始时间
     */
    private String contractTimeFrom;

    /**
     * 签订日结束时间
     */
    private String contractTimeTo;

    /**
     * 白条：1；代采：2；自营采购：3；自营销售：4
     * 代采赊销：5
     */
    private String budgetType;

    /**
     * 白条：1；代采：2；自营采购：3；自营销售：4
     * 代采赊销：5
     *  现在有的前端是多选，新增一个list
     */
    private List<String> budgetTypes;

    /**
     * 是否完成审批（销售合同）
     */
    private Boolean sellApproveFlg;

    /**
     * 是否盖章(签约)
     */
    private Boolean sellSealFlg;

    /**
     * 是否付款
     */
    private Boolean sellDealedFlg;

    /**
     * 是否收货
     */
    private Boolean sellWarehouseFlg;

    /**
     * 是否确认收货
     */
    private Boolean sellConfirmFlg;

    /**
     * 是否收票
     */
    private Boolean sellBilledFlg;

    /**
     * 是否作废
     */
    private Boolean sellInvalidFlg;

    /**
     * 是否完成
     */
    private Boolean sellCompleteFlg;

    /**
     * 是否逾期
     */
    private Boolean sellOverdueFlg;

    /**
     * 是否违约
     */
    private Boolean sellBreachFlg;

    /**
     * 是否完成审批
     */
    private Boolean buyApproveFlg;

    /**
     * 是否盖章
     */
    private Boolean buySealFlg;

    /**
     * 是否付款
     */
    private Boolean buyDealedFlg;

    /**
     * 是否收货
     */
    private Boolean buyWarehouseFlg;

    /**
     * 是否收票
     */
    private Boolean buyBilledFlg;

    /**
     * 是否作废
     */
    private Boolean buyInvalidFlg;

    /**
     * 是否完成
     */
    private Boolean buyCompleteFlg;

    /**
     * 是否逾期
     */
    private Boolean buyOverdueFlg;

    /**
     * 是否违约
     */
    private Boolean buyBreachFlg;

    /**
     * 是否正常
     */
    private Boolean commonFlg;

    /**
     * 是否罚金不足状态
     */
    private Boolean sellNoFineFlg;

    private List<Long> approveIds;

    /**
     * 付全款日开始
     */
    private String bpayFullTimeFrom;

    /**
     * 付全款日结束
     */
    private String bpayFullTimeTo;

    /**
     * 收全款日开始
     */
    private String spayFullTimeFrom;

    /**
     * 收全款日结束
     */
    private String spayFullTimeTo;

    /**
     * 区域
     */
    private String deptName;

    /**
     * 因决算统计页面品种/牌号/厂商是sql拼接出来的，故查询时在此特殊处理
     */
    private String productsNameOne;
    private String productsNameTwo;
    private String productsNameThree;
    private String level;

    /**
     * 不包含部门
     */
    private List<Long> notDeptIds;

    public List<Long> getNotDeptIds() {
        return notDeptIds;
    }

    public void setNotDeptIds(List<Long> notDeptIds) {
        this.notDeptIds = notDeptIds;
    }
}
