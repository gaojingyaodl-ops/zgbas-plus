package com.spt.bas.report.server.dao;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.report.client.entity.RptCtrContractSearch;
import com.spt.bas.report.client.vo.*;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author shengong
 */
@MyBatisDao
public interface RptCtrContractMapper {
    /**
     * 合同列表
     *
     * @param vo
     * @return
     */
    List<RptCtrContractVo> findPageCtrContract(RptCtrContractSearch vo);

    /**
     * 合同详情
     *
     * @param vo
     * @return
     */
    RptCtrContractDetailVo getCtrContract(RptCtrContractSearch vo);

    /**
     * 服务合同详细
     *
     * @param vo
     * @return
     */
    RptCtrServiceContractVo getServiceContract(RptCtrContractSearch vo);

    /**
     * 获取合同的业务操作记录
     *
     * @param contractNo
     * @return
     */
    List<RptContractOperationVo> getContractOperationList(String contractNo);

    /**
     * 获取合同的发货详情
     *
     * @param contractNo
     * @return
     */
    RptCtrWarehouseDetailVo getDeliveryOutDetail(String contractNo);

    /**
     * 获取则一回调到车信息详情
     *
     * @param contractNo
     * @return
     */
    RptArriveVehicleVo getArriveVehicle(String waybillCode);
    /**
     * 获取合同未发货的详情
     * @param contractNo
     * @return
     */
    RptCtrUnDeliveryOutVo getUndeliveryOutDetail(String contractNo);

    /**
     * 查询支付货款的历史详情
     * @param contractNo
     * @return
     */
    RptCtrPayVo getPayDetail(String contractNo);

    /**
     * 获取合同的付服务费的历史详情
     * @param contractNo
     * @return
     */
    RptCtrPayVo getServicePayDetail(String contractNo);

    /**
     * 通过销售合同号查询服务合同号
     * @param contractNo
     * @return
     */
    String findSerContractNoBySellContractNo(String contractNo);

    /**
     * 获取合同的货款开票的历史详情
     * @param contractNo
     * @return
     */
    RptCtrBillVo getBillDetail(String contractNo);


    /**
     * 获取合同的服务费开票的历史详情
     * @param contractNo
     * @return
     */
    RptCtrBillVo getServiceBillDetail(String contractNo);

    /**
     * 获取合同的确认收货的历史详情
     * @param contractNo
     * @return
     */
    RptCtrConfirmReceiptVo getConfirmReceiptDetail(String contractNo);

    /**
     * 查询进行/销项为开发票金额及个数
     * @param query 查询条件
     * @return 结果
     */
    RptNobillVo getNobill(RptIndexReportQuery query);

    /**
     * 查询当日
     * @param query
     * @return
     */
    List<RptIndexCommonVo> getBuinessStatistic(RptIndexReportQuery query);

    /**
     * 查询历史月份业务统计
     * @param query
     * @return
     */
    List<RptIndexCommonVo> getBuinessStatisticByMonth(RptIndexReportQuery query);

    /**
     * 查询业绩排行
     *
     * @param query 查询参数
     * @return 业绩排行
     */
    List<RptIndexCommonVo> getPerformanceRanking(RptIndexReportQuery query);

    /**
     * 过去10个月的毛利率
     *
     * @return 毛利率
     */
    List<RptGrossProfitMarginVo> grossProfitMargin(RptGrossProfitMarginSearchVo searchVo);

    /**
     * 代采赊销资金成本
     * @param searchVo
     * @return
     */
    List<RptGrossProfitMarginVo> getDcsxCapitalCost(RptGrossProfitMarginSearchVo searchVo);

    /**
     * 普通赊销资金成本
     * @param searchVo
     * @return
     */
    List<RptGrossProfitMarginVo> getCapitalCost(RptGrossProfitMarginSearchVo searchVo);

    List<CtrContract> findDataByUserIdAndDate(RptUserRoiVo vo);
}
