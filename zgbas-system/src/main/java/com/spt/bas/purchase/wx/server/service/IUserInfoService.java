package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.bas.client.entity.BillInfoRequest;
import com.spt.bas.purchase.wx.client.entity.SaveInfo;
import com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition;
import com.spt.bas.purchase.wx.client.vo.*;
import com.spt.bas.purchase.wx.server.payload.*;
import com.spt.bas.purchase.wx.server.vo.*;
import com.spt.sign.client.vo.AxqUrlVo;
import com.spt.tools.core.exception.ApplicationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息补全服务
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 14:03
 */
public interface IUserInfoService {


    /**
     * 获取用户所选的企业类别
     *
     * @return
     */
    CustomVo getPmsCompanyType();

    /**
     * 获取临时保存信息
     *
     * @return
     */
    String getCompanyInfo();

    /**
     * 获取量身定制配置参数
     *
     * @return
     */
    Map<String, Object> getCustomSetting();

    /**
     * 用户服务开通信息
     *
     * @return
     */
    ServiceOpeningInfoVo getSptServieOpeningInfo();

    /**
     * 更新服务开通信息
     */
    void updateUserStatus(ServiceOpeningInfoRequest serviceOpeningInfoRequest);

    /**
     * 获取补充资料临时保存信息
     *
     * @return
     */
    SupplyInfoVo findLatestNotifySupplyInfo();

    /**
     * 开通服务提交审核
     */
    void updateCompanyDetail();

    /**
     * 获取额度信息
     *
     * @return
     */
    QuotaInfoVo getQuotaInfo();

    /**
     * '上传证件'步骤 获取临时保存信息
     *
     * @return
     */
    CompanyBaseInfoVo findLatestNotifyBaseInfo();


    /**
     * 上传并识别工商营业执照信息
     *
     * @param fileRequest
     * @param req
     * @return LicenseVo 营业执照信息
     */
    LicenseVo uploadAndOcrLicenseCode(UploadBase64Request fileRequest, HttpServletRequest req);

    /**
     * 上传并识别身份证
     *
     * @param req
     * @return
     */
    IdentityCardVo uploadAndOcrIdentityCard(UploadBase64Request req, HttpServletRequest request);

    /**
     * 临时保存
     *
     * @param request
     */
    void appletNotifyBaseInfo(CompanyBaseInfoRequest request);

    /**
     * 发起资料审核
     * @param request
     */
    void applyBaseInfo(CompanyBaseInfoRequest request);

    /**
     * 临时保存委托授权
     *
     * @param request
     */
    void appletNotifyEntrust(EntrustRequest request);

    /**
     * 发起委托授权申请
     */
    void applyEntrust();
    
    void applyEntrustCms(ApplyEntrust entrust);

    /**
     * 获取委托授权临时保存
     *
     * @return
     */
    EntrustVo findLatestNotifyEntrust();

    /**
     * 生成委托授权书
     *
     * @return
     */
    String generatePowerOfAttorney(HttpServletRequest request);

    /**
     * 签署提交
     *
     * @param request
     * @return
     */
    BaseVo updateEntrustDetail(EntrustRequest request);

    /**
     * 获取委托授权信息
     *
     * @param request
     * @return
     */
    EntrustVo getEntrustDetail(BaseRequest request);

    /**
     * 获取入金金额和账户信息
     *
     * @return
     */
    PriceAndAccountVo getDepositInfo();

    /**
     * 提交入金申请
     *
     * @param request
     * @return
     */
    BaseVo updateDeposit(BaseRequest request);

    /**
     * 补充资料 临时保存
     *
     * @param request
     */
    void appletNotifySupplyInfo(SupplyInfoRequest request);

    /**
     * cfca申请提交
     *
     * @param request
     * @return
     */
    void updateCfcaApproved(CfcaRequest request);

    /**
     * 获取企业已经开通的CFCA信息
     * @return
     */
    CfcaVo getCfcaInfo();

    /**
     * 获取CFCA服务费金额和账户信息
     *
     * @param
     * @return
     */
    PriceAndAccountVo getCfcaPayFeeInfo(BaseRequest request);

    /**
     * cfca支付服务费提交申请
     *
     * @param request
     */
    void updateCfcaPayFee(BaseRequest request);

    /**
     * 新增企业配置的仓库信息
     *
     * @param request
     */
    Long addWarehouse(WarehouseRequest request);

    /**
     * 获取企业配置的仓库信息列表
     *
     * @return
     */
    List<WarehouseVo> getWarehouseList();

    /**
     * 删除仓库信息
     *
     * @param request
     * @return
     */
    List<WarehouseVo> deleteWarehouse(WarehouseRequest request);

    /**
     * 获取企业配置的某仓库信息
     *
     * @param request
     * @return
     */
    WarehouseVo getWarehouse(WarehouseRequest request);

    /**
     * 修改企业配置的仓库信息
     *
     * @param request
     */
    void updateWarehouse(WarehouseRequest request);

    /**
     * 查询企业配置的仓库个数
     *
     * @return
     */
    int queryWarehouseNum();

    /**
     * 提交成为合伙人
     */
    void applyPartner();

    /**
     * 查询推荐码
     *
     * @return
     */
    Long queryPartnerCode();

    /**
     * 查询企业所属业务员的电话
     * @return
     */
    String queryPartnerPhone();

    /**
     * 额度测试提交申请
     */
    void quotaTest(QuotaTestRequest request);

    /**
     * 2.3.1营业执照副本加盖公章上传
     *
     * @param req
     * @param request
     * @return
     */
    UploadFileVo uploadAndOcrLicenseCodeWithSeal(UploadBase64Request req, HttpServletRequest request);

    /**
     * 提交意见反馈
     *
     * @param request
     */
    void saveFeedback(FeedbackRequest request);

    /**
     * 获取意见反馈类型
     * @return
     */
    List<Map<String, String>> getFeedbackType();

    /**
     * 获取付款银行信息
     * @param request
     * @return
     */
    PayBankInfoVo getPaybankInfo(PayBankInfoRequest request);

    /**
     * 获取企业配置的发票信息
     *
     * @return
     */
    BillInfoVo getBillsInfo();

    /**
     * 添加企业发票信息
     * @param billInfoRequest
     */
    void addBillsInfo(BillInfoRequest billInfoRequest);

    AxqUrlVo axqContract(ContractNoRequest contractNoRequest);

    AxqUrlVo axqGoodReceive(DeliveryOutNoRequest deliveryOutNoRequest);

    void successContract(String contractNo) throws ApplicationException, IOException;

    void successGoodReceive(String contractNo) throws ApplicationException, IOException;

    void successDebtCertificate(String contractNo) throws IOException;

    Map authFaceRecognition(AuthFaceRecognition authFaceRecognition) throws Exception;


    /**
     * cfca开户合并流程
     * @param request
     */
    void ApplyWxCfca(CfcaRequest request );

    AxqUrlVo axqDebtCertificate(ContractNoRequest contractNoRequest);

    /**
     * 查询债权凭证
     * @param
     */
    String  selectCreditor(ContractNoRequest contractNoRequest );

    /**
     * 协助线上化申请
     * @param vo
     */
    void saveSaveInfo(CompanyOnLineApplyVo vo );

    SaveInfo findSaveInfo(Long companyId, Long userId, String type, Boolean commitFlg);
}
