package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.vo.ApplyDcsxChooseVo;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.client.vo.DcsxShowVo;
import com.spt.bas.client.vo.UpdateDcsxContractVo;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IApplyDcsxService  extends IBaseService<ApplyCtrDCSX> {

    List<ApplyCtrDCSX> findByDCSXApproveIdAll(Long approveId);

    Page<DcsxShowVo> findPageContract(ContractSearchVo queryVo);

    ApplyCtrDCSX sumPageContract(ContractSearchVo searchVo);

    ApplyDcsxChooseVo findById(Long contractId);

    ApplyCtrDCSX findByDCSXApproveId(Long approveId);

    ApplyCtrDCSX findByContractNo(String contractNo);

    void updateFileId(UpdateDcsxContractVo vo);

    void updateStatus(Long id, BigDecimal amount, String contractStatus , String status);
    
    void addConfirmReceiptNumber(Long contractId, BigDecimal dealAmount, Date confirmReceiptDate, String approveNo) throws ApplicationException;

    String getDcsxTemplateContract(ApplyCtrDCSX entity);

    List<ApplyCtrDCSX> autoDcsxPayAmount();

    List<ApplyCtrDCSX> autoDcsxPayBondAmount();

    List<ApplyCtrDCSX> autoDcsxReceiveAmount();

    void addPayRefundAmount(Long contractId, BigDecimal dealAmount) throws ApplicationException;
    
    void addReceiveRefundAmount(Long contractId, BigDecimal dealAmount) throws ApplicationException;

    List<PmApprove> filterAutoSignWithPay(List<PmApprove> autoSignApproveList);

    /**
     * 查找60天内未申请代采赊销付款的代采赊销订单
     * @param companyName
     * @return
     */
    List<ApplyCtrDCSX> findHb60DayNotApplyList(String companyName);
}
