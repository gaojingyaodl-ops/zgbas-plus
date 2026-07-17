package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.dto.CtrContractDto;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.DzdAgreement;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/ctr/contract", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface ICtrContractClient extends BaseClient<CtrContract> {

    /**
     * 获取合同列表
     * @param queryVo
     * @return
     */
    @PostMapping("findPageVo")
    PageDown<CtrContract> findPageVo(@RequestBody PageSearchVo queryVo);

    @PostMapping("updateFileId")
    void updateFileId(FileIdUpdateVo vo);

    @PostMapping("updateDebtCertificateFileId")
    void updateDebtCertificateFileId(FileIdUpdateVo vo);

    @PostMapping("updateCtrFileId")
    void updateCtrFileId(FileIdUpdateVo vo);

    @PostMapping("updateInvoiceFileId")
    void updateInvoiceFileId(FileIdUpdateVo vo);

    @PostMapping("updateWarehouseFileId")
    void updateWarehouseFileId(FileIdUpdateVo vo);

    @PostMapping("updateDoubleCheckFileId")
    void updateDoubleCheckFileId(FileIdUpdateVo vo);

    @PostMapping("invalidTheContract")
    void invalidTheContract(@RequestBody CtrConctractInvalidVo vo);

    @PostMapping("findApproveByOrder")
    List<CtrContract> findApproveByOrder(@RequestBody CtrContract ctr);

    @PostMapping("findPageContract")
    PageDown<ContractShowVo> findPageContract(@RequestBody ContractSearchVo queryVo);

    @PostMapping("findPageChoose")
    PageDown<CtrContractChooseVo> findPageChoose(@RequestBody ContractSearchVo queryVo);

    @PostMapping("sumPageContract")
    CtrContract sumPageContract(@RequestBody ContractSearchVo searchVo);

    @PostMapping("findByStockDetailId")
    CtrContract findByStockDetailId(@RequestBody Long stockDetailId);

    @PostMapping("findContractByLinkContractId")
    CtrContract findContractByLinkContractId(@RequestBody Long linkContractId);

    @PostMapping("findByLinkContractIdLink")
    List<CtrContract> findByLinkContractIdLink(@RequestBody String linkContractId);

    @PostMapping("findByIdIn")
    List<CtrContract> findByIdIn(@RequestBody String[] arr);

    @PostMapping("findIdByLinkContractId")
    List<Long> findIdByLinkContractId(@RequestBody String id);

    @PostMapping("findByContractId")
    CtrContractChooseVo findByContractId(@RequestBody Long contractId);

    @PostMapping("findByServiceContractId")
    CtrServiceContractChooseVo findByServiceContractId(Long serviceContractId);

    @PostMapping("findByContractNo")
    CtrContract findByContractNo(@RequestBody CtrContract ctrContract);

    @PostMapping("updateConfirmReceiveNumber")
    void updateConfirmReceiveNumber(@RequestBody CtrContractOphisRequest ophisRequest);

    /**
     * 合同签约
     */
    @PostMapping("doSigning")
    void doSigning(@RequestBody CtrContractSignRequest req);

    @PostMapping("findDetailByContractId")
    CtrContractDetailVo findDetailByContractId(@RequestBody Long contractId);

    @PostMapping("refreshContractText")
    void refreshContractText(@RequestBody Long contractId);

    /**
     * 收货证明预览
     */
    @PostMapping("printApplyConfirm")
    ApproveFormPrintVo printApplyConfirm(@RequestBody Long contractId);

    @PostMapping("updateContractAmount")
    void updateContractAmount(@RequestBody CtrContractUpdateVo updateVo);

    @PostMapping("getApproveHistory")
    List<PmApproveHistoryVo> getApproveHistory(@RequestBody ContractSearchVo searchVo);

    @PostMapping("findByContractNoV2")
    CtrContract findByContractNoV2(@RequestBody String contractNo);

    /**
     * 通过一个合同id查询这个预算的上下游合同
     * @param contractId
     * @return
     */
    @PostMapping("findContractsByContractId")
    List<CtrContract> findContractsByContractId(@RequestBody Long contractId);

    /**
     * 校验是否是背靠背合同
     * @param contracNo
     * @return
     */
    @PostMapping("checkIsBkb")
    boolean checkIsBkb(String contracNo);

    /**
     * 校验是否是托盘合同
     * @param contracNo
     * @return
     */
    @PostMapping("checkIsTP")
    boolean checkIsTP(String contracNo);

    /**
     * 通过approveId查询合同id
     */
    @PostMapping("findByApproveId")
    List<CtrContract> findByApproveId(@RequestBody Long approveId);

    @PostMapping("findAllContractByApproveId")
    List<CtrContract> findAllContractByApproveId(@RequestBody Long approveId);

    /**
     * 通过approveId查询合同id
     */
    @PostMapping("findByApproveIds")
    List<CtrContract> findByApproveIds(@RequestBody List<Long> approveId);

    /**
     * 通过companyId查询合同
     * @param companyId
     * @return
     */
    @PostMapping("findByCompanyId")
    List<CtrContract> findByCompanyId(@RequestBody Long companyId);

    @GetMapping("updateContractData")
    void updateContractData(@RequestParam("contractNo") String contractNo,@RequestParam("actualContractAmount") BigDecimal actualContractAmount, @RequestParam("deductibleAmount") BigDecimal  deductibleAmount, @RequestParam("originalContractAmount") BigDecimal originalContractAmount);

    @PostMapping("updateGoodsFileId")
    void updateGoodsFileId(FileIdUpdateVo vo);

    @PostMapping("updateStatusByContractNo")
    void updateStatusByContractNo(@RequestParam("factorStatus") String factorStatus, @RequestParam("contractNo") String contractNo);

    @PostMapping("violateFlgUpdate")
    void violateFlgUpdate(@RequestBody Long id);

    @PostMapping("clearPenalty")
    void clearPenalty(@RequestBody Long id);

    @PostMapping("refreshFactorStatus")
    CtrContract refreshFactorStatus(@RequestBody Long contractId);

    @PostMapping("findByCustomerOrderCode")
    CtrContract findByCustomerOrderCode(@RequestParam("customerOrderCode") String customerOrderCode);

    @PostMapping("updateDeliveryStaus")
     void updateDeliveryStaus(@RequestParam("id") Long id,@RequestParam("staus")  String staus);

    /**
     * 订单预警查询
     * @param queryVo
     * @return
     */
    @PostMapping("findByOrderWarn")
    PageDown<CtrContract> findByOrderWarn(@RequestBody ContractOrderVo queryVo);

    /**
     * 诉讼管理查询
     * @param queryVo
     * @return
     */
    @PostMapping("findByLitigation")
    PageDown<CtrContract> findByLitigation(@RequestBody ContractOrderVo queryVo);

    /**
     * 更新履约状态
     * @param id
     * @param status
     */
    @PostMapping("updatePerformanceStatus")
    void updatePerformanceStatus(@RequestParam("id")Long id,@RequestParam("status")String status);

    /**
     * 发货预警查询
     * @param pageSearchVo
     * @return
     */
    @PostMapping(value = "findUnDeliveryPage")
    PageDown<CtrContract> findUnDeliveryPage(@RequestBody PageSearchVo pageSearchVo);

    /**
     * 查询公司的罚息合同
     */
    @PostMapping("findByCompanyInterest")
    PageDown<CtrContract> findByCompanyInterest(@RequestBody CtrContractDto ctrContractDto);

    @PostMapping("judgeUseSpecialBankContractId")
    boolean judgeUseSpecialBankContractId(@RequestBody Long contractId);

    @PostMapping("judgeUseSpecialBankApplyMatchDetailId")
    boolean judgeUseSpecialBankApplyMatchDetailId(@RequestBody Long applyMatchDetailId);

    @PostMapping("getDeliveryExportVo")
    Map<Long, ApplyDeliveryExportVo> getDeliveryExportVo(@RequestBody List<Long> contractIds);

    @PostMapping("countByCompanyId")
    Long countByCompanyId(@RequestBody Long companyId);

    @PostMapping("downloadContractFileZip")
    DownLoadContractRespVo downloadContractFileZip(@RequestBody ContractSearchVo searchVo);

    @PostMapping("downloadContractFileMergePdf")
    DownLoadContractRespVo downloadContractFileMergePdf(@RequestBody List<CtrContractFileDownloadVo> fileDownloadVoList);

    @PostMapping("downloadDcsxContractFileMergePdf")
    DownLoadContractRespVo downloadDcsxContractFileMergePdf(@RequestBody List<CtrContractFileDownloadVo> fileDownloadVoList);

    @PostMapping("findByContractNoLikes")
    List<CtrContract> findByContractNoLikes(@RequestBody String contractNo);

    @PostMapping("findDiscountContractList")
    List<ApplyReceive> findDiscountContractList(@RequestBody CtrContract ctrContract);

    @PostMapping("findTpDiscountContractList")
    List<ApplyReceive> findTpDiscountContractList(@RequestBody CtrContract ctrContract);

    @PostMapping("findContractByIds")
    List<CtrContract> findContractByIds(@RequestBody Long[] arr);

    @PostMapping("findOverdueContractListByCompanyId")
    List<CtrContract> findOverdueContractListByCompanyId(@RequestBody Long companyId);
    
    @PostMapping("findUnDelivery3Day")
    public Boolean findUnDelivery3Day(@RequestBody Long companyId);

    @PostMapping("findSpecialChainContract")
    CtrContract findSpecialChainContract(@RequestBody Long approveId);
    @PostMapping("sumByLitigation")
    CtrContract sumByLitigation(ContractOrderVo searchVo);

    @PostMapping("filterAutoSignWithPay")
    List<PmApprove> filterAutoSignWithPay(@RequestBody List<PmApprove> autoSignApproveList);

    @PostMapping("getDzdAgreement")
    DzdAgreement getDzdAgreement(@RequestBody ProtocolDocumentSearchVo searchVo);
}

