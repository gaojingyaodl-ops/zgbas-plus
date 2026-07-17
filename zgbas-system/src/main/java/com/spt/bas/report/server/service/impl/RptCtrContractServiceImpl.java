package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.entity.RptCtrContractSearch;
import com.spt.bas.report.client.vo.*;
import com.spt.bas.report.server.dao.RptCtrContractMapper;
import com.spt.bas.report.server.service.IRptCtrContractService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 11:27
 */
@Component
public class RptCtrContractServiceImpl implements IRptCtrContractService {

    @Autowired
    private RptCtrContractMapper ctrContractMapper;
    @Value("${file.show.url}")
    private String fileShowUrl;

    /**
     * 合同列表
     *
     * 全部收货按钮显示逻辑 = 出库数量 > 收货数量
     * @param vo 多查询条件
     */
    @Override
    public Page<RptCtrContractVo> findPageCtrContract(RptCtrContractSearch vo) {
        List<RptCtrContractVo> list = ctrContractMapper.findPageCtrContract(vo);
        list.forEach(ctrContractVo -> {
            String debtCertificateFileId = ctrContractVo.getDebtCertificateFileId();
            if (StringUtils.isNotBlank(debtCertificateFileId)) {
                String[] split = debtCertificateFileId.split(",");
                if (ArrayUtils.isNotEmpty(split)) {
                    ctrContractVo.setDebtCertificateFileUrl(fileShowUrl + "/view/show/" + split[0]);
                }
            }
        });
        Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
        Page<RptCtrContractVo> pageVo = new PageImpl<>(list, pageable, vo.getCount());
        return pageVo;
    }

    /**
     * 合同详细
     *
     * @param vo
     * @return
     */
    @Override
    public RptCtrContractDetailVo getCtrContract(RptCtrContractSearch vo) {
        RptCtrContractDetailVo ctrContract = ctrContractMapper.getCtrContract(vo);
        if (ctrContract != null && !ctrContract.getProducts().isEmpty()) {
            BigDecimal warehouseTotalAmount = BigDecimal.ZERO;
            BigDecimal confirmReceiptTotalAmount = BigDecimal.ZERO;
            for (RptCtrProductDetailVo product : ctrContract.getProducts()) {
                warehouseTotalAmount = warehouseTotalAmount.add(product.getWarehouseAmount());
                confirmReceiptTotalAmount = confirmReceiptTotalAmount.add(product.getConfirmReceiptAmount());
            }
            ctrContract.setWarehouseAmount(warehouseTotalAmount);
            ctrContract.setConfirmReceiptAmount(confirmReceiptTotalAmount);
        }
        return ctrContract;
    }

    /**
     * 服务合同详细
     *
     * @param vo
     * @return
     */
    @Override
    public RptCtrServiceContractVo getServiceContract(RptCtrContractSearch vo) {

        RptCtrServiceContractVo serviceContract = ctrContractMapper.getServiceContract(vo);
        if (serviceContract != null && !StringUtils.isEmpty(serviceContract.getServiceContractNo())) {
            // billedFlg
            if (serviceContract.getBilledAmount().compareTo(serviceContract.getTotalAmount()) >= 0) {
                serviceContract.setBilledFlg(true);
            } else {
                serviceContract.setBilledFlg(false);
            }
            // dealedFlg
            if (serviceContract.getDealedAmount().compareTo(serviceContract.getTotalAmount()) >= 0) {
                serviceContract.setDealedFlg(true);
            } else {
                serviceContract.setDealedFlg(false);
            }
        }
        return serviceContract;
    }

    /**
     * 获取合同的业务操作记录
     *
     * @param contractNo
     * @return
     */
    @Override
    public List<RptContractOperationVo> getContractOperationList(String contractNo) {
        List<RptContractOperationVo> contractOperationList = ctrContractMapper.getContractOperationList(contractNo);
        // 添加orderNo字段
        for (int i = 0; i < contractOperationList.size(); i++) {
            contractOperationList.get(i).setOrderNo(i + 1);
            String subject = contractOperationList.get(i).getSubject();
            String title = subject.substring(0, subject.indexOf("]") + 1);
            contractOperationList.get(i).setSubject(title + "[]");
        }
        return contractOperationList;
    }

    /**
     * 获取合同的发货详情
     *
     * @param contractNo
     * @return
     */
    @Override
    public RptCtrWarehouseDetailVo getDeliveryOutDetail(String contractNo) {
        RptCtrWarehouseDetailVo result = ctrContractMapper.getDeliveryOutDetail(contractNo);
        if (result != null) {
            List<RptCtrWarehouseDeliveryVo> deliveryList = result.getDeliveryList();
            List<RptCtrWarehouseDeliveryVo> deliveryVoList = new ArrayList<>();
            BigDecimal warehouseTotalAmount = BigDecimal.ZERO;
            BigDecimal warehouseTotalNumber = BigDecimal.ZERO;
            if (!deliveryList.isEmpty()) {
                for (RptCtrWarehouseDeliveryVo vo : deliveryList) {
                    if(StringUtils.isNotBlank(vo.getWaybillCode())){
                        // 是否则一订单
                        RptArriveVehicleVo arriveVehicle = ctrContractMapper.getArriveVehicle(vo.getWaybillCode());
                        if(arriveVehicle != null){
                            warehouseTotalAmount = warehouseTotalAmount.add(vo.getWarehouseAmount());
                            warehouseTotalNumber = warehouseTotalNumber.add(vo.getWarehouseNumber());
                            deliveryVoList.add(vo);
                        }
                    } else {
                        warehouseTotalAmount = warehouseTotalAmount.add(vo.getWarehouseAmount());
                        warehouseTotalNumber = warehouseTotalNumber.add(vo.getWarehouseNumber());
                        deliveryVoList.add(vo);
                    }
                }
            }
            result.setWarehouseTotalAmount(warehouseTotalAmount);
            result.setWarehouseTotalNumber(warehouseTotalNumber);
            result.setDeliveryList(deliveryVoList);
        }
        return result;
    }

    /**
     * 获取合同的未发货的详情
     *
     * @param contractNo
     * @return
     */
    @Override
    public RptCtrUnDeliveryOutVo getUndeliveryOutDetail(String contractNo) {
        RptCtrUnDeliveryOutVo undeliveryOutDetail = ctrContractMapper.getUndeliveryOutDetail(contractNo);
        if (undeliveryOutDetail != null && !undeliveryOutDetail.getProducts().isEmpty()) {
            for (RptCtrUnDeliveryOutDetailVo product : undeliveryOutDetail.getProducts()) {
                // 已入库数量大于未出库数量
                if (product.getDeliveryInNumber().compareTo(product.getDealNumber()) >= 0) {
                    // 可出库的最大数量 （取已入库数量和未出库数量较小值）
                    product.setApplyNumber(product.getDealNumber().min(product.getDeliveryInNumber()));
                }else {
                    // 已入库数量小于未出库数量
                    product.setApplyNumber(BigDecimal.ZERO);
                }
            }
        }
        return undeliveryOutDetail;
    }

    /**
     * 查询支付货款的历史详情
     *
     * @param contractNo
     * @return
     */
    @Override
    public RptCtrPayVo getPayDetail(String contractNo) {
        return ctrContractMapper.getPayDetail(contractNo);
    }

    /**
     * 获取合同的付服务费的历史详情
     *
     * @param contractNo 销售合同号
     * @return
     */
    @Override
    public RptCtrPayVo getServicePayDetail(String contractNo) {
        return ctrContractMapper.getServicePayDetail(contractNo);
    }

    /**
     * 获取合同的货款开票的历史详情
     *
     * @param contractNo
     * @return
     */
    @Override
    public RptCtrBillVo getBillDetail(String contractNo) {
        return ctrContractMapper.getBillDetail(contractNo);
    }

    /**
     * 获取合同的服务费开票的历史详情
     *
     * @param contractNo 销售合同
     * @return
     */
    @Override
    public RptCtrBillVo getServiceBillDetail(String contractNo) {
        return ctrContractMapper.getServiceBillDetail(contractNo);
    }

    /**
     * 获取合同的确认收货的历史详情
     *
     * @param contractNo
     * @return
     */
    @Override
    public RptCtrConfirmReceiptVo getConfirmReceiptDetail(String contractNo) {
        RptCtrConfirmReceiptVo result = ctrContractMapper.getConfirmReceiptDetail(contractNo);
        if (result != null) {
            BigDecimal confirmReceiptTotalAmount = BigDecimal.ZERO;
            for (RptCtrConfirmReceiptDetailVo vo : result.getConfirmReceiptList()) {
                confirmReceiptTotalAmount = confirmReceiptTotalAmount.add(vo.getConfirmReceiptAmount());
            }
            result.setConfirmReceiptTotalAmount(confirmReceiptTotalAmount);
        }
        return result;
    }

}
