package com.spt.bas.server.filter;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.SealUsageDcsxVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;

/**
 * 自动生成PDF文件签署
 *
 * @Author MoonLight
 * @Date 2023/3/30 15:45
 * @Version 1.0
 */
public interface IAutoSealPdfSignFilter {
    /**
     * 生成PDF下游合同附件文件签署
     *
     * @param approve
     * @param contract
     */
    void generateSealPDFSign(PmApprove approve, CtrContract contract);

    void generateVirtualSealPDFSign(PmApprove approve, CtrContract contract);

    void generateFLKSealPDFSign(PmApprove approve, CtrContract contract);

    void generateFLKPurchaseOrder(PmApprove approve, CtrContract contract);

    /**
     * 生成PDF补充协议附件文件签署
     * @param approve
     * @param contract
     */
    void generateProtocolSealPDFSign(PmApprove approve, CtrContract contract, CtrContract buyContract);

    /**
     * 生成PDF中游合同附件文件签署
     * @param approve
     * @param entity
     */
    void generateSealPDFSignDCSX(PmApprove approve, ApplyCtrDCSX entity) throws ApplicationException;

    /**
     * 生成PDF中游补充协议附件文件签署
     * @param approve
     * @param entity
     */
    void generateProtocolSealPDFSignDCSX(PmApprove approve, ApplyCtrDCSX entity) throws ApplicationException;

    /**
     * 生成PDF中游合同附件文件签署
     * @param approve
     * @param entity
     */
    void generateSealPDFSignDCSXV2(PmApprove approve, ApplyCtrDCSX entity) throws ApplicationException;

    /**
     * 更新中游合同文件签署附件
     * @param entity
     * @throws ApplicationException
     */
    void updateCfcaContractNo(SealUsageDcsxVo entity);

    String successSignContractByKeyword(String cfcaContractNo);

    String successFLKSignContractByKeyword(String cfcaContractNo, String contractNo);

    String successSignContractByKeywordDCSX(String cfcaContractNo, Long dcsxId);

    String successSignContractByKeyword(String cfcaContractNo, String contractNo);

    String successSignContractByKeywordSpecial(String cfcaContractNo, String contractNo);

    String successSignProtocolFileByKeyword(String cfcaContractNo, String contractNo);

    String successSignDcsxProtocolFileByKeyword(String cfcaContractNo, String contractNo);

    void successFLKPurchaseOrder(Long approveId);
}
