package com.spt.bas.server.service;

import com.spt.bas.client.dto.CtrContractDto;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.DzdAgreement;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICtrContractService extends IBaseService<CtrContract> {

    List<CtrContract> findApproveByOrder(Long approveId);

    Page<ContractShowVo> findPageContract(ContractSearchVo queryVo);

    Page<CtrContractChooseVo> findPageChoose(ContractSearchVo queryVo);

    CtrContract sumPageContract(ContractSearchVo searchVo);

    CtrContract findByStockDetailId(Long stockDetailId);

    List<Long> findIdByLinkContractId(String id);

    CtrContract findContractByLinkContractId(Long linkContractId);

    List<CtrContract> findByLinkContractIdLink(String linkContractId);

    List<CtrContract> findByIdIn(Long[] arr);

    List<CtrContract> findByIds(List<Long> ids);

    CtrContractChooseVo findByContractId(Long contractId);

    CtrServiceContractChooseVo findByServiceContractId(Long serviceContractId);

    CtrContract findByContractNo(String contractNo);

    CtrContractDetailVo findDetailByContractId(Long contractId);

    ApproveFormPrintVo printApplyConfirm(Long contractId);

    BigDecimal getTotalAmountByCompanyId(Long companyId, Long enterpriseId, String contractType);

    Boolean existOrder(Long companyId, Long enterpriseId, String contractType);

    List<PmApproveHistoryVo> getApproveHistory(Long buyContractId, Long sellContractId);

    CtrContract findByContractNoV2(String contractNo);

    CtrContract findBuyContractBySellContractId(Long sellContractId);

    CtrContract findSellContractByBuyContractId(Long buyContractId);

    /**
     * 通过approveId查询合同id
     *
     * @param approveId
     * @return
     */
    List<CtrContract> findByApproveId(Long approveId);
    List<CtrContract> findByApproveIds(List<Long> approveIds);

    /**
     * 通过companyId查询合同
     *
     * @param companyId
     * @return
     */
    List<CtrContract> findByCompanyId(Long companyId);

    List<CtrContract> findByAllSellContract();

    List<CtrContract> findCtrContractBreach();

    void updateContractData( String contractNo,  BigDecimal actualContractAmount,  BigDecimal  deductibleAmount,  BigDecimal originalContractAmount);

    List<CtrContract> findByContractTypeDCSXBl();

    void updateStatusByContractNo(String factorStatus, String contractNo);

    CtrContract findByCustomerOrderCode(String customerOrderCode);
    // 订单预警
    Page<CtrContract> findByOrderWarn(ContractOrderVo queryVo);
    // 诉讼管理
    Page<CtrContract> findByLitigation(ContractOrderVo queryVo);

    List<CtrContract> findUnDelivery(Long companyId);

    Page<CtrContract> findUnDeliveryPage(PageSearchVo pageSearchVo);

    // 查询某一个罚息合同
    Page<CtrContract> findByCompanyInterest(CtrContractDto ctrContractDto);

    boolean judgeUseSpecialBankContractId(Long contractId);

    boolean judgeUseSpecialBankApplyMatchDetailId(Long applyMatchDetailId);

    /**
     * 查询合同出入库明细
     * @param contractIds
     * @return
     */
    Map<Long, ApplyDeliveryExportVo> getDeliveryExportVo(List<Long> contractIds);

    List<CtrContract> autoPayAmount();

    List<CtrContract> autoPayBondAmount();
    
    List<CompanyOrderResVo> findCompanyOrder(String minute);

    List<CtrContract> findByContractNoLikes(String contractNo);

    Page<CtrContract> findCtrContractPage(Pageable page);
    
    Integer selectAllCount();

    List<ApplyReceive> findDiscountContractList(CtrContract ctrContract);

    List<ApplyReceive> findTpDiscountContractList(CtrContract ctrContract);

    List<CtrContract> findOverdueContractListByCompanyId(Long companyId);
    Boolean findUnDelivery3Day(Long companyId);

    CtrContract findContractByVirtualId(Long virtualId);

    CtrContract findSpecialChainContract(Long approveId);

    void updatePiccPushFlg(Long contractId, Boolean piccPushFlg);
    void updatePiccDeclareStatus(Long contractId, String piccDeclareStatus);
    void updatePiccReceiveFlg(Long contractId, Boolean piccReceiveFlg);

    CtrContract sumByLitigation(ContractOrderVo queryVo);

    List<PmApprove> filterAutoSignWithPay(List<PmApprove> autoSignApproveList);
    
    DzdAgreement getDzdAgreement(ProtocolDocumentSearchVo searchVo);
}

