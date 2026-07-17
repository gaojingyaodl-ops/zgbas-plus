package com.spt.bas.server.service;


import com.spt.bas.client.entity.ApplyCompanyLicense;
import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.entity.CompanyLicense;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.tools.data.annotation.ServiceTransactional;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IApproveWaitDealService extends IBaseService<ApproveWaitDeal> {

    Page<ApproveWaitDeal> findPageWaitDeal(ApproveWaitSearchVo queryVo);

    Page<ApproveWaitDeal> findPageWaitDealById(ApproveWaitSearchVo queryVo);

    void updateStatus(ApproveWaitSearchVo queryVo);

    void updateFlg(ApproveWaitSearchVo queryVo);

    List<ApproveWaitDeal> findPageWaitDealCount(ApproveWaitSearchVo queryVo);

    String findSubject(ApproveWaitSearchVo queryVo);
    // 定时任务：修改履约状态时，添加待办事项
    void doContractSaveWaitDeal(@RequestBody CtrContract searchVo);

    void saveWaitDeal(ApproveWaitDealVo searchVo);

    void addUnDeliveryDeal(CtrContract contract, List<Long> userIdList);

    void addEvaluateUserDeal(EvaluateUserApproveWaitDealVo evaluateApproveWaitDealVo);

    void addZipFileDeal(ContractSearchVo searchVo, String queryDateStr, String path);

    void addZipFileDeal(Long enterpriseId, Long userId, String queryDateStr, String path);

    void addZipFileInvoiceDcsx(Long enterpriseId, Long userId, String queryDateStr, String path);

    void addCompanyZipFileDeal(BsCompanySearchVo searchVo, String queryDateStr, String path);

    /**
     * 保存双签附件导出通知
     * @param companyLicense 公司证照相关信息
     * @param path 路径
     */
    @ServiceTransactional
    void addCompanyLicenseFile(ApplyCompanyLicense companyLicense, List<CompanyLicense> path);

    Long getUserWaitDealNum(String userId);

    void addCompanyCreditFileDeal(BsCompanySearchVo searchVo, String s, String path);
}
