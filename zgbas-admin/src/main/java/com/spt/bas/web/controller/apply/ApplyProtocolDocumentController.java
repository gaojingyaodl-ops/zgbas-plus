package com.spt.bas.web.controller.apply;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.protocol.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 协议文件
 *
 * @Author MoonLight
 * @Date 2024/5/21 16:23
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "/apply/protocolDocument")
public class ApplyProtocolDocumentController extends PageController<ApplyProtocolDocument, BaseVo> {
    @Resource
    private IApplyProtocolDocumentClient protocolDocumentClient;
    @Value("${file.show.url}")
    private String fileShowUrl;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBasBrandClient brandClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IApplyCtrDcsxClinent ctrDcsxClinent;
    @Autowired
    private ICtrProductClient ctrProductClient;
    @Autowired
    private IApplyReceiveClient applyReceiveClient;
    @Autowired
    private IBsCompanyOurClient bsCompanyOurClient;
    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;

    @Override
    public BaseClient<ApplyProtocolDocument> getService() {
        return protocolDocumentClient;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            protocolDocumentClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyProtocolDocument entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, entity.getStatus()) ||
                StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
            model.addAttribute("disabled", true);
        } else {
            model.addAttribute("disabled", false);
        }
        if (entity.getId() != 0) {
            String content = entity.getContent();
            ApplyProtocolDocVo ckhVo = JSONObject.parseObject(content, ApplyProtocolDocVo.class);
            
            if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, entity.getDocType())) {
                List<ApplyProtocolDocCkhDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocCkhDetailVo.class);
                model.addAttribute("detailSize", detailList.size());
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, entity.getDocType())) {
                List<ApplyProtocolDocZnjDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocZnjDetailVo.class);
                model.addAttribute("detailSize", detailList.size());
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, entity.getDocType())) {
                List<ReminderPaymentAgreement> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ReminderPaymentAgreement.class);
                model.addAttribute("detailSize", detailList.size());
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_DZ, entity.getDocType())) {
//                String remark = ckhVo.getRemark();
//                if (StringUtils.isBlank(remark)) {
//                    remark = "如贵司对上述账目内容有任何异议，请在收到本对账单之日起3个工作日内与我司对账人联系核对。逾期未提出异议，视为贵司确认对账单内容无误。感谢您的配合！";
//                }
//                ckhVo.setRemark(remark);
            }
            model.addAttribute("content", ckhVo);

        } else {
            model.addAttribute("content", new ApplyProtocolDocVo());
            model.addAttribute("detailSize", 1);
        }

        //业务类型
        model.addAttribute("productTypeJson",
                JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
        List<BasBrand> lstBrand = brandClient.findAll();
        model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
        model.addAttribute("defaultDate", new Date());
        model.addAttribute("docTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_DOC_TYPE)));
        // 企业抬头
        model.addAttribute("ourCompanyNameJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        // 交货方式 DICT_DELIVERYTYPETEXT
        model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_DELIVERYTYPETEXT)));
        model.addAttribute("defaultFlgJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DEFAULTFLG)));// 是否

        return "apply/applyProtocolDoc";
    }

    @RequestMapping(value = "addProtocolDocCkhDetail")
    public String addProtocolDocCkhDetail(Model model, String docType, Integer curNumber, Long id) {
        ApplyProtocolDocument entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        if (StringUtils.equals(BasConstants.APPROVE_STATUS_A, entity.getStatus()) ||
                StringUtils.equals(BasConstants.APPROVE_STATUS_D, entity.getStatus())) {
            model.addAttribute("disabled", true);
        } else {
            model.addAttribute("disabled", false);
        }
        if (entity.getId() != 0) {
            String content = entity.getContent();
            ApplyProtocolDocVo ckhVo = JSONObject.parseObject(content, ApplyProtocolDocVo.class);
            model.addAttribute("content", ckhVo);
            Integer randomNumber = 0;
            if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, docType)) {
                List<ApplyProtocolDocCkhDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocCkhDetailVo.class);
                for (ApplyProtocolDocCkhDetailVo detail : detailList) {
                    detail.setRandomNumber(randomNumber);
                    randomNumber++;
                }
                model.addAttribute("detailList", detailList);
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, docType)) {
                List<ApplyProtocolDocZnjDetailVo> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ApplyProtocolDocZnjDetailVo.class);
                for (ApplyProtocolDocZnjDetailVo detail : detailList) {
                    detail.setRandomNumber(randomNumber);
                    randomNumber++;
                }
                model.addAttribute("detailList", detailList);
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, docType)) {
                List<ReminderPaymentAgreement> detailList = JSONArray.parseArray(ckhVo.getDetailList(), ReminderPaymentAgreement.class);
                for (ReminderPaymentAgreement detail : detailList) {
                    detail.setRandomNumber(randomNumber);
                    randomNumber++;
                }
                model.addAttribute("detailList", detailList);
            }

        } else {
            model.addAttribute("content", new ApplyProtocolDocVo());
            if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, docType)) {
                List<ApplyProtocolDocCkhDetailVo> detailList = new ArrayList<>();
                ApplyProtocolDocCkhDetailVo detail = new ApplyProtocolDocCkhDetailVo();
                detail.setRandomNumber(curNumber);
                detailList.add(detail);
                model.addAttribute("detailList", detailList);
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, docType)) {
                List<ApplyProtocolDocZnjDetailVo> detailList = new ArrayList<>();
                ApplyProtocolDocZnjDetailVo detail = new ApplyProtocolDocZnjDetailVo();
                detail.setRandomNumber(curNumber);
                detailList.add(detail);
                model.addAttribute("detailList", detailList);
            } else if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, docType)) {
                List<ReminderPaymentAgreement> detailList = new ArrayList<>();
                ReminderPaymentAgreement detail = new ReminderPaymentAgreement();
                detail.setRandomNumber(curNumber);
                detailList.add(detail);
                model.addAttribute("detailList", detailList);
            }

        }
        String url = "apply/import-ckh";
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RL, docType)) {
            url = "apply/import-ckh";
        }
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_LN, docType)) {
            url = "apply/import-znj";
        }
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_CP, docType)) {
            url = "apply/import-rescission";
            RescissionAgreement rescissionAgreement = StringUtils.isNotBlank(entity.getContent())
                    ? JsonUtil.json2Object(RescissionAgreement.class, entity.getContent())
                    : new RescissionAgreement();
            model.addAttribute("content", rescissionAgreement);
        }
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RA, docType)) {
            url = "apply/import-repayment";
            RepaymentAgreement repaymentAgreement = StringUtils.isNotBlank(entity.getContent())
                    ? JSONUtil.toBean(entity.getContent(), RepaymentAgreement.class)
                    : new RepaymentAgreement();
            repaymentAgreement.setRepaymentDetailList(JSONUtil.toList(repaymentAgreement.getRepaymentDetailListStr(),
                    RepaymentAgreement.RepaymentAgreementDetail.class));
            model.addAttribute("content", repaymentAgreement);
        }

        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_SP, docType)) {
            url = "apply/import-supAgreement";
            SupplementaryAgreement supplementaryAgreement = StringUtils.isNotBlank(entity.getContent())
                    ? JsonUtil.json2Object(SupplementaryAgreement.class, entity.getContent())
                    : new SupplementaryAgreement();
            model.addAttribute("content", supplementaryAgreement);
        }

        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_RP, docType)) {
            url = "apply/import-reminderPayment";
        }
        if (StringUtils.equals(BasConstants.DICT_DOC_TYPE_DZ, docType)) {
            url = "apply/import-dzd";
            DzdAgreement dzdAgreement = StringUtils.isNotBlank(entity.getContent())
                    ? JSONUtil.toBean(entity.getContent(), DzdAgreement.class)
                    : new DzdAgreement();
            dzdAgreement.setDzdDetailList(JSONUtil.toList(dzdAgreement.getDzdDetailListStr(),
                    DzdAgreement.DzdAgreementDetail.class));
            model.addAttribute("content", dzdAgreement);
        }
        return url;
    }

    @ModelAttribute("preload")
    public ApplyProtocolDocument getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyProtocolDocument entity = new ApplyProtocolDocument();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        if (id != null && id != 0L) {
            entity = getService().getEntity(id);
        }
        return entity;
    }

    @ResponseBody
    @RequestMapping(value = "findDetailByContractNo", method = RequestMethod.POST)
    public Object findDetailByContractNo(@RequestParam("contractNo") String contractNo, @RequestParam("endDate") String endDate) {
        
        if (org.apache.commons.lang3.StringUtils.isNotBlank(contractNo)) {
            ProtocolDocumentContractVo protocolDocumentContractVo = new ProtocolDocumentContractVo();
            CtrContract contract = ctrContractClient.findByContractNoV2(contractNo);
            if (Objects.nonNull(contract)) {
                BeanUtils.copyProperties(contract, protocolDocumentContractVo);
                List<CtrProduct> productList = ctrProductClient.findByOutCtrContractId(contract.getId());
                if (CollectionUtils.isNotEmpty(productList)) {
                    CtrProduct ctrProduct = productList.get(0);
                    protocolDocumentContractVo.setProductCd(ctrProduct.getProductCd());
                    protocolDocumentContractVo.setProductName(ctrProduct.getProductName());
                    protocolDocumentContractVo.setBrandNumber(ctrProduct.getBrandNumber());
                }
                BigDecimal totalAmount = contract.getTotalAmount();
                BigDecimal dealedAmount = contract.getDealedAmount();
                if (dealedAmount == null) {
                    dealedAmount = BigDecimal.ZERO;
                }
                protocolDocumentContractVo.setUnPayOverdueAmount(totalAmount.subtract(dealedAmount));
                List<ApplyReceive> receiveList = applyReceiveClient.findListByContractIdAndStatus(contract.getId(), BasConstants.APPROVE_STATUS_D);
                Date appointPayFullTime = contract.getAppointPayFullTime();
                BigDecimal breachRate = contract.getBreachRate();
                
                Date newDate = new Date();
                if (StringUtils.isNotBlank(endDate)) {
                    newDate = DateUtil.parseDate(endDate);
                }
                StringBuilder realPayDateStrBuilder = new StringBuilder();
                StringBuilder overdueLateFeesBuilder = new StringBuilder();
                BigDecimal overdueLateFeeSum = BigDecimal.ZERO;

                ApplyCtrDCSX dcsxContract = ctrDcsxClinent.findByDCSXApproveId(contract.getApproveId());

                String ourCompanyName = contract.getOurCompanyName();
                if (Objects.nonNull(dcsxContract)) {
                    if (StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contract.getContractType())) {
                        String ourBankName = dcsxContract.getOurBankName();
                        String ourBankAccount = dcsxContract.getOurBankAccount();
                        if(ourBankName.isEmpty()||ourBankName.equals("-")||ourBankAccount.isEmpty()||ourBankAccount.equals("-")){
                            BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
                            companyOurSearchVo.setCompanyName(ourCompanyName);
                            BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
                            if (Objects.nonNull(companyOur)) {
                                protocolDocumentContractVo.setBankName(companyOur.getCompanyBankName());
                                protocolDocumentContractVo.setBankAccount(companyOur.getCompanyCardId());

                            } else {
                                BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
                                if (byCompanyName != null) {
                                    protocolDocumentContractVo.setBankName(byCompanyName.getCompanyBankName());
                                    protocolDocumentContractVo.setBankAccount(byCompanyName.getCompanyCardId());
                                }
                            }
                        } else {
                            protocolDocumentContractVo.setBankName(dcsxContract.getOurBankName());
                            protocolDocumentContractVo.setBankAccount(dcsxContract.getOurBankAccount());
                        }
                    } else {
                        protocolDocumentContractVo.setBankName(dcsxContract.getBankName());
                        protocolDocumentContractVo.setBankAccount(dcsxContract.getBankAccount());
                    }
                } else {
                    BsCompanyOurSearchVo companyOurSearchVo = new BsCompanyOurSearchVo();
                    companyOurSearchVo.setCompanyName(ourCompanyName);
                    BsCompanyOur companyOur = bsCompanyOurClient.getCompanyOurDetail(companyOurSearchVo);
                    if (Objects.nonNull(companyOur)) {
                        protocolDocumentContractVo.setBankName(companyOur.getCompanyBankName());
                        protocolDocumentContractVo.setBankAccount(companyOur.getCompanyCardId());

                    } else {
                        BsCompanyDcsx byCompanyName = bsCompanyDcsxClient.findByCompanyName(ourCompanyName);
                        if (byCompanyName != null) {
                            protocolDocumentContractVo.setBankName(byCompanyName.getCompanyBankName());
                            protocolDocumentContractVo.setBankAccount(byCompanyName.getCompanyCardId());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(receiveList)) {
                    int num = 0;
                    for (ApplyReceive applyReceive : receiveList) {
                        num++;
                        Date receiveDate = applyReceive.getReceiveDate();
                        BigDecimal receiveAmount = applyReceive.getReceiveAmount();
                        realPayDateStrBuilder.append(DateUtils.parseDateToStr("yyyy-MM-dd", receiveDate) + "支付" + receiveAmount.stripTrailingZeros().toPlainString() + "元");
                        if (num < receiveList.size()) {
                            realPayDateStrBuilder.append("\r\n");
                        }
                        // 逾期滞纳金
                        if (appointPayFullTime != null && breachRate != null) {
                            // 你的逻辑代码
                            LocalDate appointPayFullTimeLocalDate = appointPayFullTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate receiveLocalDate = receiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (receiveLocalDate.isAfter(appointPayFullTimeLocalDate)) {
                                long days = ChronoUnit.DAYS.between(appointPayFullTimeLocalDate, receiveLocalDate);
                                BigDecimal overdueLateFee = receiveAmount.multiply(breachRate).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_HALF_UP);
                                overdueLateFeesBuilder.append(receiveAmount.stripTrailingZeros().toPlainString() + "*" + breachRate.stripTrailingZeros().toPlainString() + "*" + days + "天=" + overdueLateFee.stripTrailingZeros().toPlainString() + "元");
                                overdueLateFeesBuilder.append("\r\n");
                                overdueLateFeeSum = overdueLateFeeSum.add(overdueLateFee);
                            }
                        }
                    }

                    if (dealedAmount.compareTo(totalAmount) < 0) {
                        // 未付全款
                        BigDecimal subtract = totalAmount.subtract(dealedAmount);

                        if (appointPayFullTime != null && breachRate != null) {
                            LocalDate appointPayFullTimeLocalDate = appointPayFullTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate receiveLocalDate = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (receiveLocalDate.isAfter(appointPayFullTimeLocalDate)) {
                                // 计算两个日期之间的天数差异
                                long days = ChronoUnit.DAYS.between(appointPayFullTimeLocalDate, receiveLocalDate);
                                BigDecimal overdueLateFee = subtract.multiply(breachRate).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_HALF_UP);
                                overdueLateFeesBuilder.append(subtract.stripTrailingZeros().toPlainString() + "*" + breachRate.stripTrailingZeros().toPlainString() + "*" + days + "天=" + overdueLateFee.stripTrailingZeros().toPlainString() + "元");
                                overdueLateFeesBuilder.append("\r\n");
                                overdueLateFeeSum = overdueLateFeeSum.add(overdueLateFee);
                            }

                        }
                    }


                    protocolDocumentContractVo.setRealPayDateStr(realPayDateStrBuilder.toString());
                    if (overdueLateFeesBuilder != null && overdueLateFeesBuilder.length() > 0) {
                        overdueLateFeesBuilder.append("\r\n" + "此笔订单合计：" + overdueLateFeeSum.stripTrailingZeros().toPlainString() + "元");
                        protocolDocumentContractVo.setOverdueLateFees(overdueLateFeesBuilder.toString());
                        protocolDocumentContractVo.setOverdueLateFeeSum(overdueLateFeeSum);
                    }
                } else {
                    protocolDocumentContractVo.setRealPayDateStr("支付0元");
                    if (appointPayFullTime != null && breachRate != null) {
                        LocalDate appointPayFullTimeLocalDate = appointPayFullTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate receiveLocalDate = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (receiveLocalDate.isAfter(appointPayFullTimeLocalDate)) {
                            // 计算两个日期之间的天数差异
                            long days = ChronoUnit.DAYS.between(appointPayFullTimeLocalDate, receiveLocalDate);
                            BigDecimal overdueLateFee = totalAmount.multiply(breachRate).multiply(new BigDecimal(days)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            overdueLateFeesBuilder.append(totalAmount.stripTrailingZeros().toPlainString() + "*" + breachRate.stripTrailingZeros().toPlainString() + "*" + days + "天=" + overdueLateFee.stripTrailingZeros().toPlainString() + "元");
                            overdueLateFeesBuilder.append("\r\n");
                            overdueLateFeesBuilder.append("\r\n" + "此笔订单合计：" + overdueLateFee.stripTrailingZeros().toPlainString() + "元");
                            protocolDocumentContractVo.setOverdueLateFees(overdueLateFeesBuilder.toString());
                            protocolDocumentContractVo.setOverdueLateFeeSum(overdueLateFee);
                        }

                    }
                }
                return protocolDocumentContractVo;
            }
//            else {
//                ApplyCtrDCSX ctrDCSX = ctrDcsxClinent.findByContractNo(contractNo);
//                BeanUtils.copyProperties(ctrDCSX,protocolDocumentContractVo);
//                return protocolDocumentContractVo;
//            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "findDzdAgreement", method = RequestMethod.POST)
    public DzdAgreement findDzdAgreement(@RequestBody ProtocolDocumentSearchVo searchVo) {

        DzdAgreement dzdAgreement = new DzdAgreement();

        String dzdCompanyName = searchVo.getDzdCompanyName();
        String ourCompanyName = searchVo.getOurCompanyName();
        Date dzDateBegin = searchVo.getDzDateBegin();
        Date dzDateEnd = searchVo.getDzDateEnd();
        
        if (StringUtils.isBlank(dzdCompanyName)) {
            return dzdAgreement;
        }
        if (StringUtils.isBlank(ourCompanyName)) {
            return dzdAgreement;
        }
        if (Objects.isNull(dzDateBegin)) {
            return dzdAgreement;
        }
        if (Objects.isNull(dzDateEnd)) {
            return dzdAgreement;
        }
        
        return ctrContractClient.getDzdAgreement(searchVo);
    }

    // 根据协议类型，查找相同合同号的数量，用于生成协议编号
    @RequestMapping(value = "findProtocolDocumentByContent", method = RequestMethod.GET)
    public void findProtocolDocumentByContent(@RequestParam("contractNo") String contractNo, @RequestParam("docType") String docType, HttpServletResponse response) {
        try {
            PageSearchVo pageSearchVo = new PageSearchVo();
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("LIKES_content", contractNo);
            searchParams.put("EQS_docType", docType);
            pageSearchVo.setSearchParams(searchParams);
            PageDown<ApplyProtocolDocument> page = getService().findPage(pageSearchVo);
            JsonEasyUI.renderJson(response, page);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
}
