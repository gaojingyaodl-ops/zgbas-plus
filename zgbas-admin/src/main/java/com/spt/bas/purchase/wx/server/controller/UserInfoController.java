package com.spt.bas.purchase.wx.server.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.spt.bas.client.entity.BillInfoRequest;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.client.payload.AuthFaceRecognition;
import com.spt.bas.purchase.wx.server.common.ApiResult;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.InfoStep;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.dao.CompanyUserDao;
import com.spt.bas.purchase.wx.server.payload.*;
import com.spt.bas.purchase.wx.server.service.IUserInfoService;
import com.spt.bas.purchase.wx.server.util.JwtUtil;
import com.spt.bas.purchase.wx.server.vo.CustomVo;
import com.spt.bas.purchase.wx.server.vo.ServiceOpeningInfoVo;
import com.spt.sign.client.remote.ICfcaSignClient;
import com.spt.sign.client.vo.AxqCommitRequest;
import com.spt.sign.client.vo.AxqUrlVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户信息补全相关接口
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-17 10:46
 */
@RestController
@RequestMapping(value = "/wx/userInfo")
@Api(tags = "微信小程序用户信息相关接口")
@ApiSort(value = 2)
@Slf4j
public class UserInfoController extends BaseController {

    @Autowired
    private IUserInfoService userInfoService;
    @Resource
    private ICfcaSignClient cfcaSignClient;
    @Resource
    private ICtrContractClient ctrContractClient;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CompanyUserDao userDao;
    /**
     * 2.1.1获取用户服务开通信息
     *
     * @return
     */
    @ApiOperation(value = "2.1.1获取用户服务开通信息")
    @PostMapping("/getSptServieOpeningInfo")
    @ApiOperationSupport(order = 1)
    public ApiResult getSptServieOpeningInfo() {
        ServiceOpeningInfoVo serviceOpeningInfoVo = userInfoService.getSptServieOpeningInfo();
        return ApiResult.ofSuccess(serviceOpeningInfoVo);
    }

    /**
     * 2.1.2 更新服务开通信息
     *
     * @return
     */
    @ApiOperation(value = "2.1.2更新服务开通信息")
    @PostMapping("/updateUserStatus")
    @ApiOperationSupport(order = 2)
    public ApiResult updateUserStatus(@Valid @RequestBody ServiceOpeningInfoRequest request) {
        userInfoService.updateUserStatus(request);
        return ApiResult.ofMessage("更新成功");
    }

    /**
     * 2.1.3 获取额度信息
     *
     * @return
     */
    @ApiOperation(value = "2.1.3 获取额度信息")
    @PostMapping("/getQuotaInfo")
    @ApiOperationSupport(order = 3)
    public ApiResult getQuotaInfo() {
        return ApiResult.ofSuccess(userInfoService.getQuotaInfo());
    }

    /**
     * 2.1.4 获取用户所选的企业类别
     *
     * @return
     */
    @ApiOperation(value = "2.1.4 获取用户所选的企业类别")
    @PostMapping("/getPmsCompanyType")
    @ApiOperationSupport(order = 4)
    public ApiResult getPmsCompanyType() {
        CustomVo pmsCompanyType = userInfoService.getPmsCompanyType();
        return ApiResult.ofSuccess(pmsCompanyType);
    }

    /**
     * 获取审批完成之后保存的临时信息
     * @return
     */
    @ApiOperation(value = "2.1.5 获取审批完成之后保存的临时信息")
    @PostMapping("/getCompanyInfo")
    @ApiOperationSupport(order = 5)
    public ApiResult getCompanyInfo() {
        return ApiResult.ofSuccess(JSONUtil.parseObj(userInfoService.getCompanyInfo()));
    }

    //2.2 额度测试================================================

    /**
     * 2.2.1.扫描工商营业执照
     *
     * @param fileRequest
     * @param req
     * @return
     */
    @ApiOperation(value = "2.2.1.扫描工商营业执照")
    @PostMapping("/uploadAndOcrLicenseCode")
    @ApiOperationSupport(order = 6)
    public ApiResult uploadAndOcrLicenseCode(@Valid @RequestBody UploadBase64Request fileRequest, HttpServletRequest req) {
        return ApiResult.ofSuccess(userInfoService.uploadAndOcrLicenseCode(fileRequest, req));
    }

    /**
     * 2.2.2 获取量身定制配置参数
     *
     * @return
     */
    @ApiOperation(value = "2.2.2 获取量身定制配置参数")
    @PostMapping("/getCustomSetting")
    @ApiOperationSupport(order = 7)
    public ApiResult getCustomSetting() {
        return ApiResult.ofSuccess(userInfoService.getCustomSetting());
    }

    /**
     * 2.2.3.额度测试提交
     *
     * @return
     */
    @ApiOperation(value = "2.2.3.额度测试提交")
    @PostMapping("/quotaTest")
    @ApiOperationSupport(order = 8)
    public ApiResult quotaTest(@RequestBody QuotaTestRequest request) {
        userInfoService.quotaTest(request);
        return ApiResult.ofSuccess(returnMap("infoStep", InfoStep.BASE_INFO.getInfoStep()));
    }

    // 2.3上传证件================================================

    /**
     * 2.3.1营业执照副本加盖公章上传
     * @return
     */
    @ApiOperation(value = "2.3.1营业执照副本加盖公章上传")
    @PostMapping("/uploadAndOcrLicenseCodeWithSeal")
    @ApiOperationSupport(order = 9)
    public ApiResult uploadAndOcrLicenseCodeWithSeal(@Valid @RequestBody UploadBase64Request req, HttpServletRequest request) {
        return ApiResult.ofSuccess(userInfoService.uploadAndOcrLicenseCodeWithSeal(req, request));
    }

    /**
     * 2.3.2上传并识别身份证信息
     *
     * @param req
     * @param request
     * @return
     */
    @ApiOperation(value = "2.3.2上传并识别身份证信息")
    @PostMapping("/uploadAndOcrIdentityCard")
    @ApiOperationSupport(order = 10)
    public ApiResult uploadAndOcrIdentityCard(@Valid @RequestBody UploadBase64Request req, HttpServletRequest request) {
        return ApiResult.ofSuccess(userInfoService.uploadAndOcrIdentityCard(req, request));
    }

    /**
     * 2.3.3获取临时保存信息
     *
     * @return
     */
    @ApiOperation(value = "2.3.3获取临时保存信息")
    @PostMapping("/findLatestNotifyBaseInfo")
    @ApiOperationSupport(order = 11)
    public ApiResult findLatestNotifyBaseInfo() {
        return ApiResult.ofSuccess(userInfoService.findLatestNotifyBaseInfo());
    }

    /**
     * 2.3.4临时保存企业信息
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "2.3.4临时保存企业信息")
    @PostMapping("/appletNotifyBaseInfo")
    @ApiOperationSupport(order = 12)
    public ApiResult appletNotifyBaseInfo(@RequestBody CompanyBaseInfoRequest request) {
        userInfoService.appletNotifyBaseInfo(request);
        return ApiResult.ofMessage("保存成功");
    }

    // 2.4.签署委托授权============================================================

    /**
     * 2.4.1委托授权临时保存
     *
     * @param entrustRequest
     * @return
     */
    @ApiOperation(value = "2.4.1委托授权临时保存")
    @PostMapping("/appletNotifyEntrust")
    @ApiOperationSupport(order = 13)
    public ApiResult appletNotifyEntrust(@RequestBody EntrustRequest entrustRequest) {
        userInfoService.appletNotifyEntrust(entrustRequest);
        return ApiResult.ofSuccess();
    }

    /**
     * 2.4.2获取委托授权临时保存
     *
     * @return
     */
    @ApiOperation(value = "2.4.获取委托授权临时保存")
    @PostMapping("/findLatestNotifyEntrust")
    @ApiOperationSupport(order = 14)
    public ApiResult findLatestNotifyEntrust() {
        return ApiResult.ofSuccess(userInfoService.findLatestNotifyEntrust());
    }

    /**
     * 2.4.3生成委托授权书
     * @return
     */
    @ApiOperation(value = "2.4.3生成委托授权书")
    @PostMapping("/generatePowerOfAttorney")
    @ApiOperationSupport(order = 15)
    public ApiResult generatePowerOfAttorney(HttpServletRequest request) {
        return ApiResult.ofSuccess(returnMap("fileId", userInfoService.generatePowerOfAttorney(request)));
    }

    /**
     * 2.4.4 发起委托授权申请
     * @return
     */
    @ApiOperation(value = "2.4.4.发起委托授权申请")
    @PostMapping("/applyEntrust")
    @ApiOperationSupport(order = 15)
    public ApiResult applyEntrust() {
        userInfoService.applyEntrust();
        return ApiResult.ofSuccess();
    }

    // 2.5入金测试========================================================================================================
    /**
     * 2.5.1获取入金金额和账户信息
     *
     * @return
     */
    @ApiOperation(value = "获取入金金额和账户信息")
    @PostMapping("/getDepositInfo")
    @ApiOperationSupport(order = 16)
    public ApiResult getDepositInfo() {
        return ApiResult.ofSuccess(userInfoService.getDepositInfo());
    }

    /**
     * 2.5.2提交入金申请
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "3.4.2提交入金申请")
    @PostMapping("/updateDeposit")
    @ApiOperationSupport(order = 17)
    public ApiResult updateDeposit(@RequestBody BaseRequest request) {
        return ApiResult.ofMessage("提交成功", userInfoService.updateDeposit(request));
    }

    /**
     * 2.5.2获取付款的银行账户信息
     * @return
     */
    @ApiOperation(value = "2.5.2获取付款的银行账户信息")
    @PostMapping("/getPaybankInfo")
    @ApiOperationSupport(order = 17)
    public ApiResult getPaybankInfo(@Valid @RequestBody PayBankInfoRequest request) {
        return ApiResult.ofSuccess(userInfoService.getPaybankInfo(request));
    }

    // 2.6补充资料========================================================================

    /**
     * 2.6.1 补充资料临时保存
     * @return
     */
    @ApiOperation(value = "2.6.1 补充资料临时保存")
    @PostMapping("/appletNotifySupplyInfo")
    @ApiOperationSupport(order = 18)
    public ApiResult appletNotifySupplyInfo(@RequestBody SupplyInfoRequest request) {
        userInfoService.appletNotifySupplyInfo(request);
        return ApiResult.ofSuccess();
    }

    /**
     * 2.6.2 获取补充资料临时保存信息
     * @return
     */
    @ApiOperation(value = "2.6.2 获取补充资料临时保存信息")
    @PostMapping("/findLatestNotifySupplyInfo")
    @ApiOperationSupport(order = 19)
    public ApiResult findLatestNotifySupplyInfo() {
        return ApiResult.ofSuccess(userInfoService.findLatestNotifySupplyInfo());
    }

    /**
     * 2.6.2 提交企业资料审核
     * @return
     */
    @ApiOperation(value = "2.6.2.提交企业资料审核")
    @PostMapping("/applyBaseInfo")
    @ApiOperationSupport(order = 19)
    public ApiResult applyBaseInfo(@RequestBody CompanyBaseInfoRequest request) {
        userInfoService.applyBaseInfo(request);
        return ApiResult.ofSuccess();
    }

    /**
     * 企业提交审核以后：
     *
     * 1）后台根据临时保持的信息以及companyApplyStatus、entrustApplyStatus、depositStatus三个字段的值，生成审批流；
     *
     * 2）若companyApplyStatus=0或者3，提起企业资料审批流
     * 2）若companyApplyStatus=0或者3，提起企业资料审批流
     *
     * 3）若entrustApplyStatus=0或者3，提起委托授权书的审批流
     *
     * 4）depositStatus=0或者3，提起入金测试的审批流。
     *
     * 5）审批流成功，则对应的字段值为4，若审批中，则对应的字段值为1，若失败，则对应的字段值为3，未发起，则对应的字段为0。
     */
    @ApiOperation(value = "2.6.3 企业提交审核")
    @PostMapping("/updateCompanyDetail")
    @ApiOperationSupport(order = 20)
    public ApiResult updateCompanyDetail() {
        userInfoService.updateCompanyDetail();
        return ApiResult.ofSuccess(returnMap("infoStep", InfoStep.SUPPLY_INFO_CHECKING.getInfoStep()));
    }


    /**
     * cfca 上传企业资料申请cfca合并接口
     * @param
     * @return
     */
    @ApiOperation(value = "上传企业资料申请cfca合并接口")
    @PostMapping("/updateCfcaApproved")
    @ApiOperationSupport(order = 21)
    public ApiResult updateCfcaApproved(@RequestBody CfcaRequest request) {
        userInfoService.applyBaseInfo(request);
        userInfoService.updateCfcaApproved(request);
        return ApiResult.ofSuccess();
    }

    /**
     * 2.17.生成应收账款债权凭
     *
     * @return
     */
    @ApiOperation(value = "2.17.生成应收账款债权凭")
    @PostMapping("/axqDebtCertificate")
    @ApiOperationSupport(order = 76)
    public ApiResult axqDebtCertificate(@RequestBody @Valid ContractNoRequest contractNoRequest, HttpServletRequest request) {
        String userid = jwtUtil.getUseridFromRequest(request);
        CompanyUser user = userDao.findByUserid(Long.valueOf(userid), true);
        contractNoRequest.setOperatorPhone(user.getLoginPhone());
        AxqUrlVo axqUrlVo = userInfoService.axqDebtCertificate(contractNoRequest);
        String resCode = axqUrlVo.getResCode();
        log.info("生成应收账款债权凭 contractNo:{},resCode:{},resMessage:{},shortUrl:{}", contractNoRequest.getContractNo(), resCode, axqUrlVo.getResMessage(), axqUrlVo.getUrl());
        if (StringUtils.isBlank(resCode) || !StringUtils.equals("60000000", resCode)) {
            throw new BaseException(Status.GET_SHORT_URL_ERROR);
        }
        return ApiResult.ofSuccess(axqUrlVo);
    }

    /**
     * cfca 上传企业资料申请cfca合并接口 2021/12/01
     * @param
     * @return
     */
    @ApiOperation(value = "cfca 上传企业资料申请cfca合并接口 2021/12/01")
    @PostMapping("/applyWxCfca")
    @ApiOperationSupport(order = 21)
    public ApiResult ApplyWxCfca(@RequestBody CfcaRequest request) {
        userInfoService.ApplyWxCfca(request);
        return ApiResult.ofSuccess();
    }


    /**
     * 3.7.2.获取企业已经开通的CFCA信息
     *
     * @return
     */
    @ApiOperation(value = "3.7.2.获取企业已经开通的CFCA信息")
    @PostMapping("/getCfcaInfo")
    @ApiOperationSupport(order = 22)
    public ApiResult getCfcaInfo() {
        return ApiResult.ofSuccess(userInfoService.getCfcaInfo());
    }

    /**
     * 3.10.1 新增企业配置的仓库信息
     *
     * @return
     */
    @ApiOperation(value = "3.10.1 新增企业配置的仓库信息")
    @PostMapping("/addWarehouse")
    @ApiOperationSupport(order = 24)
    public ApiResult addWarehouse(@RequestBody WarehouseRequest request) {
        Map<String, Long> result = new HashMap<>(1);
        Long wareId = userInfoService.addWarehouse(request);
        result.put("id", wareId);
        return ApiResult.ofSuccess(result);
    }

    /**
     * 3.10.2 获取企业配置的仓库信息列表
     *
     * @return
     */
    @ApiOperation(value = "3.10.2 获取企业配置的仓库信息列表")
    @PostMapping("/getWarehouseList")
    @ApiOperationSupport(order = 25)
    public ApiResult getWarehouseList() {
        return ApiResult.ofSuccess(returnMap("wareLists", userInfoService.getWarehouseList()));
    }

    /**
     * 3.10.3 删除仓库信息
     *
     * @return
     */
    @ApiOperation(value = "3.10.3 删除仓库信息")
    @PostMapping("/deleteWarehouse")
    @ApiOperationSupport(order = 26)
    public ApiResult deleteWarehouse(@RequestBody WarehouseRequest request) {
        return ApiResult.ofMessage("删除成功",returnMap("wareLists", userInfoService.deleteWarehouse(request)));
    }

    /**
     * 3.10.4 获取企业配置的某仓库信息
     *
     * @return
     */
    @ApiOperation(value = "3.10.4 获取企业配置的某仓库信息")
    @PostMapping("/getWarehouse")
    @ApiOperationSupport(order = 27)
    public ApiResult getWarehouse(@RequestBody WarehouseRequest request) {
        return ApiResult.ofSuccess(userInfoService.getWarehouse(request));
    }

    /**
     * 3.10.5 修改企业配置的仓库信息
     *
     * @return
     */
    @ApiOperation(value = "3.10.5 修改企业配置的仓库信息")
    @PostMapping("/updateWarehouse")
    @ApiOperationSupport(order = 28)
    public ApiResult updateWarehouse(@RequestBody WarehouseRequest request) {
        userInfoService.updateWarehouse(request);
        return ApiResult.ofSuccess();
    }

    /**
     * 3.10.6 查询企业配置的仓库个数
     *
     * @return
     */
    @ApiOperation(value = "3.10.6 查询企业配置的仓库个数")
    @PostMapping("/queryWarehouseNum")
    @ApiOperationSupport(order = 29)
    public ApiResult queryWarehouseNum() {
        return ApiResult.ofSuccess(returnMap("number", userInfoService.queryWarehouseNum()));
    }

    /**
     * 3.11.1.提交成为合伙人申请
     *
     * @return
     */
    @ApiOperation(value = "3.11.1.提交成为合伙人申请")
    @PostMapping("/applyPartner")
    @ApiOperationSupport(order = 30)
    public ApiResult applyPartner() {
        userInfoService.applyPartner();
        return ApiResult.ofMessage("提交成功");
    }

    /**
     * 3.11.2.查询推荐码
     * @return
     */
    @ApiOperation(value = "3.11.2.查询推荐码")
    @PostMapping("/queryPartnerCode")
    @ApiOperationSupport(order = 31)
    public ApiResult queryPartnerCode() {
        return ApiResult.ofSuccess(returnMap("inviteCode", userInfoService.queryPartnerCode()));
    }

    /**
     * 3.11.3.查询企业所属业务员的电话
     * @return
     */
    @ApiOperation(value = "3.11.3.查询企业所属业务员的电话")
    @PostMapping("/queryPartnerPhone")
    @ApiOperationSupport(order = 32)
    public ApiResult queryPartnerPhone() {
        return ApiResult.ofSuccess(returnMap("partnerPhone", userInfoService.queryPartnerPhone()));
    }

    // 2.12发票寄送信息====================================================================================================

    /**
     * 2.12.1.添加企业发票信息
     *
     * @return
     */
    @ApiOperation(value = "2.12.1.添加企业发票信息")
    @PostMapping("/addBillsInfo")
    @ApiOperationSupport(order = 33)
    public ApiResult addBillsInfo(@RequestBody BillInfoRequest billInfoRequest) {
        userInfoService.addBillsInfo(billInfoRequest);
        return ApiResult.ofSuccess();
    }

    /**
     * 2.12.2.获取企业配置的发票信息
     * @return
     */
    @ApiOperation(value = "2.12.2.获取企业配置的发票信息")
    @PostMapping("/getBillsInfo")
    @ApiOperationSupport(order = 34)
    public ApiResult getBillsInfo() {
        return ApiResult.ofSuccess(userInfoService.getBillsInfo());
    }

    /**
     * 7.1.1.提交意见反馈
     *
     * @return
     */
    @ApiOperation(value = "7.1.1.提交意见反馈")
    @PostMapping("/saveFeedback")
    @ApiOperationSupport(order = 71)
    public ApiResult saveFeedback(@RequestBody FeedbackRequest request) {
        userInfoService.saveFeedback(request);
        return ApiResult.ofSuccess();
    }

    /**
     * 7.1.2.获取意见反馈类型
     * @return
     */
    @ApiOperation(value = "7.1.2.获取意见反馈类型")
    @PostMapping("/getFeedbackType")
    @ApiOperationSupport(order = 71)
    public ApiResult getFeedbackType() {
        return ApiResult.ofSuccess(userInfoService.getFeedbackType());
    }


    // ***************2021\9\15新增***************************

    /**
     * 2.13.视频人脸识别认证
     *
     * @return
     */
    @ApiOperation(value = "2.13.视频人脸识别认证")
    @PostMapping("/authFaceRecognition")
    @ApiOperationSupport(order = 72)
    public ApiResult authFaceRecognition(@RequestBody @Valid AuthFaceRecognition authFaceRecognition)  {
        Map map = null;
        try {
            map = userInfoService.authFaceRecognition(authFaceRecognition);
        } catch (Exception e) {
            log.error("视频人脸识别认证异常", e);
            throw new BaseException(Status.ERROR);
        }
        return ApiResult.ofSuccess(map);
    }

    /**
     * 2.14.电子合同发起自动签署
     *
     * @return
     */
    @ApiOperation(value = "2.14.电子合同发起自动签署")
    @PostMapping("/axqContract")
    @ApiOperationSupport(order = 73)
    public ApiResult axqContract(@RequestBody @Valid ContractNoRequest contractNoRequest, HttpServletRequest request) {
        String userid = jwtUtil.getUseridFromRequest(request);
        CompanyUser user = userDao.findByUserid(Long.valueOf(userid), true);
        contractNoRequest.setOperatorPhone(user.getLoginPhone());
        AxqUrlVo axqUrlVo = userInfoService.axqContract(contractNoRequest);
        String resCode = axqUrlVo.getResCode();
        log.info("电子合同发起自动签署 contractNo:{},resCode:{},resMessage:{},shortUrl:{}", contractNoRequest.getContractNo(), resCode, axqUrlVo.getResMessage(), axqUrlVo.getUrl());
        if (StringUtils.isBlank(resCode) || !StringUtils.equals("60000000", resCode)) {
            throw new BaseException(Status.GET_SHORT_URL_ERROR);
        }
        return ApiResult.ofSuccess(axqUrlVo);
    }

    /**
     * 2.15.收货确认发起自动签署
     *
     * @return
     */
    @ApiOperation(value = "2.15.收货确认发起自动签署")
    @PostMapping("/axqGoodReceive")
    @ApiOperationSupport(order = 74)
    public ApiResult axqGoodReceive(@RequestBody DeliveryOutNoRequest deliveryOutNoRequest, HttpServletRequest request) {
        if (StrUtil.isEmpty(deliveryOutNoRequest.getDeliveryId()) || Objects.isNull(deliveryOutNoRequest.getConfirmReceiptDate())) {
            throw new BaseException(Status.PARAM_NOT_NULL);
        }
        String userid = jwtUtil.getUseridFromRequest(request);
        CompanyUser user = userDao.findByUserid(Long.valueOf(userid), true);
        deliveryOutNoRequest.setMatchUserPhone(user.getLoginPhone());
        AxqUrlVo axqUrlVo = userInfoService.axqGoodReceive(deliveryOutNoRequest);
        String resCode = axqUrlVo.getResCode();
        log.info("电子合同发起自动签署 contractNo:{},resCode:{},resMessage:{},shortUrl:{}", deliveryOutNoRequest.getContractNo(), resCode, axqUrlVo.getResMessage(), axqUrlVo.getUrl());
        if (StringUtils.isBlank(resCode) || !StringUtils.equals("60000000", resCode)) {
            throw new BaseException(Status.GET_SHORT_URL_ERROR);
        }
        return ApiResult.ofSuccess(axqUrlVo);
    }

    /**
     * 2.16.委托授权发起自动签署
     *
     * @return
     */
    @ApiOperation(value = "2.16.委托授权发起自动签署")
    @PostMapping("/axqPowerOfAttorney")
    @ApiOperationSupport(order = 75)
    public ApiResult axqPowerOfAttorney(@RequestBody AxqCommitRequest axqCommitRequest) {
        AxqUrlVo axqUrlVo = cfcaSignClient.axqPowerOfAttorney(axqCommitRequest);
        axqUrlVo.setUrl("www.test.test");
        Map r = new HashMap();
        return ApiResult.ofSuccess(assembleReturnMap(r, "url", axqUrlVo.getUrl()));
    }

    /**
     * 查询债权凭证
     *
     * @return
     */
    @ApiOperation(value = "查询债权凭证")
    @PostMapping("/selectCreditor")
    @ApiOperationSupport(order = 73)
    public String  selectCreditor(@RequestBody @Valid ContractNoRequest contractNoRequest, HttpServletRequest request) {
        String userid = jwtUtil.getUseridFromRequest(request);
        CompanyUser user = userDao.findByUserid(Long.valueOf(userid), true);
        contractNoRequest.setOperatorPhone(user.getLoginPhone());
        return userInfoService.selectCreditor(contractNoRequest);
    }
}
