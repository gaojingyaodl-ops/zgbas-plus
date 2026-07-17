package com.spt.bas.server.service.impl;

import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ImportExcelVo;
import com.spt.bas.client.vo.PiccExcelVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyInsuranceDao;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.ClaimBuyerDao;
import com.spt.bas.server.dao.PiccSupplementInfoDao;
import com.spt.bas.server.service.IApplyInsuranceService;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IClaimBuyerService;
import com.spt.bas.server.service.IPiccDataSyncService;
import com.spt.bas.server.util.ExelImportUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 人保限额批复数据导入处理
 *
 * @Author: gaojy
 * @create 2022/4/18 14:23
 * @version: 1.0
 * @description:
 */
@Slf4j
@Component
public class PiccDataSyncServiceImpl extends ExelImportUtil implements IPiccDataSyncService {
    @Value("${file.server.url}")
    private String fileServerUrl;

    private static final String SENDER = "NBZG";

    @Autowired
    private IApplyInsuranceService applyInsuranceService;
    @Autowired
    private ApplyInsuranceDao applyInsuranceDao;
    @Autowired
    private IClaimBuyerService claimBuyerService;
    @Autowired
    private ClaimBuyerDao claimBuyerDao;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private PiccSupplementInfoDao piccSupplementInfoDao;
    @Autowired
    private IBsCompanyCreditService bsCompanyCreditService;

    /**
     * 限额批复数据初始化导入系统
     *
     * @param importExcelVo
     */
    @Override
    @ServerTransactional
    public List<String> initPiccData(ImportExcelVo importExcelVo) {
        List<String> piccMessageList = new ArrayList<>();
        String fileId = importExcelVo.getFileId();
        Long userId = importExcelVo.getUserId();
        String userName = importExcelVo.getUserName();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            if (StringUtils.isBlank(fileId)) {
                log.error("fileId 不可为空!");
                piccMessageList.add("fileId 不可为空!");
                return piccMessageList;
            }
            fileId = fileId.replace(",", "");
            if (StringUtils.isBlank(fileId)) {
                log.error("无效的fileId!");
                piccMessageList.add("无效的fileId!");
                return piccMessageList;
            }
            InputStream inputStream = null;
            try {
                URL url = new URL(fileServerUrl + "/view/download/" + fileId);
                inputStream = url.openStream();
            } catch (Exception e) {
                log.error("openStream error", e);
                piccMessageList.add("openStream error");
                return piccMessageList;
            }
            List<PiccExcelVo> excelInfo = getPiccExcelInfo(inputStream);
            log.info(JsonUtil.obj2Json(excelInfo));
            if (CollectionUtils.isEmpty(excelInfo)) {
                log.error("excelInfo is empty!");
                piccMessageList.add("excelInfo is empty!");
                return piccMessageList;
            }
//            List<ApplyInsurance> insuranceList = applyInsuranceService.findAll();
//            List<ClaimBuyer> claimBuyerList = claimBuyerService.findAll();

            ExecutorService executorService = Executors.newCachedThreadPool();
            ExecutorCompletionService<Integer> execu = new ExecutorCompletionService<>(executorService);
            int taskSize = excelInfo.size();
            float bathSize = 25F;
            int bath = (int) Math.ceil((taskSize / bathSize));
            int syncSuccessNum = 0;
            for (int i = 0; i < bath; i++) {
                int start = (int) (bathSize * i);
                int end = (int) (start + bathSize);
                end = Math.min(end, taskSize);
                List<PiccExcelVo> list = excelInfo.subList(start, end);
                execu.submit(() -> saveInitData(list, userId, userName));
            }
            for (int i = 0; i < bath; i++) {
                Future<Integer> future = execu.take();
                log.info("result:{} OK,syncSuccessNum:{}", i, future.get());
                syncSuccessNum += future.get();
            }
            executorService.shutdown();
            
            // 将不是本次导入的修改为自主授信
            bsCompanyDao.updatePiccFlgByPiccThisUpdateFlg(false);
            // 将本次导入标识重置
            bsCompanyDao.updatePiccThisUpdateFlg();
            log.info("initPiccDate 读取到数据{}条", excelInfo.size());
            log.info("initPiccData 同步成功:{}条", syncSuccessNum);
            log.info("initPiccData success耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            piccMessageList.add("读取"+excelInfo.size()+"条企业数据;");
            piccMessageList.add("同步成功"+syncSuccessNum+"家企业授信数据;");
            piccMessageList.add("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)*1.0/1000+"秒;");
//            return "读取"+excelInfo.size()+"条企业数据，同步成功"+syncSuccessNum+"家企业授信数据，耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)/1000+"秒;";
            return piccMessageList;
        } catch (Exception e){
            log.error("导入人保授信数据失败:{}",e);
            piccMessageList.add("导入人保授信数据失败,请联系管理员;");
            return piccMessageList;
        }
    }

    private int saveInitData(List<PiccExcelVo> list, Long userId, String userName) {
//        List<ApplyInsurance> insuranceUpdateList = new ArrayList<>();
//        List<ClaimBuyer> claimBuyerUpdateList = new ArrayList<>();
        List<Long> companyIdList = new ArrayList<>();
        list.forEach(picc -> {
            initInsurance(picc, companyIdList, userId, userName);
//            boolean existCompany = initInsurance(picc, insuranceList, insuranceUpdateList, companyIdList);
//            if (Boolean.TRUE.equals(existCompany)) {
//                initClaimBuyer(picc, claimBuyerList, claimBuyerUpdateList);
//            }
        });
//        applyInsuranceDao.saveAll(insuranceUpdateList);
//        claimBuyerDao.saveAll(claimBuyerUpdateList);
//        return insuranceUpdateList.size();
        return companyIdList.size();
    }

    /**
     * 初始化限额批复买方信息报文数据
     *
     * @param picc
     * @param claimBuyerList
     * @param claimBuyerUpdateList
     */
    private void initClaimBuyer(PiccExcelVo picc, List<ClaimBuyer> claimBuyerList, List<ClaimBuyer> claimBuyerUpdateList) {
        if (Objects.nonNull(picc) && StringUtils.isNotBlank(picc.getPiccCode()) && StringUtils.isNotBlank(picc.getCorpSerialNo())) {
            ClaimBuyer claimBuyer = claimBuyerList.stream().filter(c -> StringUtils.equals(picc.getCorpSerialNo(), c.getCorpSerialNo())).findFirst().orElse(new ClaimBuyer());
            claimBuyer.setCorpSerialNo(picc.getCorpSerialNo());
            claimBuyer.setNoticeSerialNo(picc.getCorpSerialNo());
            claimBuyer.setSender("PICC");
            claimBuyer.setReceiver(SENDER);
            claimBuyer.setBuyerNo(picc.getPiccCode());
            claimBuyer.setVersion("1.0");
            claimBuyer.setMessageStatus("0");
            claimBuyerUpdateList.add(claimBuyer);
        }
    }

    /**
     * 初始化保险资料审批数据
     *
     * @param picc
     * @param companyIdList
     */
    private boolean initInsurance(PiccExcelVo picc, List<Long> companyIdList, Long userId, String userName) {
        if (Objects.nonNull(picc) && StringUtils.isNotBlank(picc.getPiccCode()) && StringUtils.isNotBlank(picc.getCorpSerialNo())) {
            String companyName = formatterCompanyName(picc.getCompanyName());
            List<BsCompany> companyList = bsCompanyDao.queryCompanyName(companyName, BasConstants.ZG_ENTERPRISE_ID);
            if (CollectionUtils.isNotEmpty(companyList)) {
                BsCompany company = companyList.get(0);
                Long companyId = company.getId();
//                ApplyInsurance insurance = insuranceList.stream().filter(i -> StringUtils.equals(picc.getCorpSerialNo(), i.getCorpSerialNo())).findFirst().orElse(new ApplyInsurance());
//                insurance.setCorpSerialNo(picc.getCorpSerialNo());
//                insurance.setBussinessNo(picc.getBussinessNo());
//                insurance.setCompanyName(picc.getCompanyName());
//                insurance.setRiskCompName(picc.getCompanyName());
//                insurance.setInsuredPiccCode(picc.getPiccCode());
//                insurance.setCompanyId(companyId);
//                insurance.setRiskCompAddress(picc.getRiskCompAddress());
//                insurance.setAppliAmount(picc.getAppliAmount());
//                insurance.setPaidTerm(picc.getPaidTerm());
//                insurance.setBussStartDate(picc.getBussStartDate());
//                insurance.setBussEndDate(picc.getBussEndDate());
//                insurance.setApplyUserId(0L);
//                insurance.setApplyUserName("sysInit");
//                insurance.setEnableFlg(true);
//                insurance.setSender(SENDER);
//                insurance.setReceiver("PICC");
//                insurance.setMessageType("GOMS_QuotaApply");
//                insurance.setMessageStatus("0");
//                insurance.setVersion("1.0");
//                insurance.setApplyStatus("2");
//                insurance.setStatus(BasConstants.APPROVE_STATUS_D);
//                insurance.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
//                insurance.setLimitFlag("0");
//                insuranceUpdateList.add(insurance);
                companyIdList.add(companyId);
                
                if(companyId != null) {
                    PiccSupplementInfo piccSupplementInfo = piccSupplementInfoDao.findByCompanyId(companyId);
                    if(Objects.isNull(piccSupplementInfo)) {
                        piccSupplementInfo = new PiccSupplementInfo();
                        piccSupplementInfo.setCompanyId(companyId);
                    }
                    piccSupplementInfo.setApproveResult(picc.getApprovedQuota());
                    piccSupplementInfoDao.save(piccSupplementInfo);
                }

                // 人保批复额度
                BigDecimal approvedQuota = new BigDecimal(picc.getApprovedQuota());
                String compensationRatioStr = picc.getCompensationRatio();
                BigDecimal compensationRatio = null;
                if (StringUtils.isNotBlank(compensationRatioStr)) {
                    compensationRatio = new BigDecimal(picc.getCompensationRatio());
                }
                Integer paidTerm = Integer.valueOf(picc.getPaidTerm());
                Date piccApproveDate = picc.getPiccApproveDate();
                Date bussStartDate = picc.getBussStartDate();
                Date bussEndDate = picc.getBussEndDate();
                String piccCode = picc.getPiccCode();
                BigDecimal piccHaveusedAmount = BigDecimal.ZERO;
                BigDecimal piccUseAbleaMount = BigDecimal.ZERO;
                if (StringUtils.isNotEmpty((picc.getPiccHaveusedAmount()))) {
                    piccHaveusedAmount = new BigDecimal(picc.getPiccHaveusedAmount());
                }
                if (StringUtils.isNotEmpty((picc.getPiccUseAbleaMount()))) {
                    piccUseAbleaMount = new BigDecimal(picc.getPiccUseAbleaMount());
                }
                // 授信类别
                String newCreditCategory = getNewCreditCategory(company.getCreditCategory(), BasConstants.CREDIT_TYPE_0);
                if (approvedQuota.compareTo(BigDecimal.ZERO) > 0) {
                    // 人保批复额度大于0 设置人保标识
                    // 企业更新人保数据
                    bsCompanyDao.updatePiccDataNew(companyId, approvedQuota, paidTerm, "4", bussStartDate,
                            bussEndDate, compensationRatio, true, BasConstants.PICC_APPLY_STATUS_2, 
                            true,piccCode, newCreditCategory, piccHaveusedAmount, piccUseAbleaMount, piccApproveDate);
                } else {
                    // 人保授信额度为0的修改为黑名单 (导入表单中的数据)
                    bsCompanyDao.updatePiccApplyStatusAndPiccFlgToBlack(companyId, approvedQuota, paidTerm, bussStartDate,
                            bussEndDate, compensationRatio,true, BasConstants.PICC_APPLY_STATUS_2, 
                            true,piccCode, newCreditCategory, piccHaveusedAmount, piccUseAbleaMount, piccApproveDate);
                    // 如果人保批复为0，则将自主授信也设置为0
                    BsCompanyCredit companyCredit = bsCompanyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(companyId, BasConstants.CREDIT_TYPE_9, true);
                    try {
                        // 修改自主额度
                        if (Objects.nonNull(companyCredit)) {
                            BigDecimal riskAmount = companyCredit.getRiskAmount();
                            companyCredit.setCreditAmount(BigDecimal.ZERO);
                            if (riskAmount == null) {
                                companyCredit.setRiskAmount(BigDecimal.ZERO);
                            }
                            companyCredit.setEffectiveDate(bussStartDate);
                            companyCredit.setExpiryDate(bussEndDate);
                            companyCredit.setCompensationRatio(compensationRatio);
                            companyCredit.setCreatedUserId(userId);
                            companyCredit.setCreatedUserName(userName);
                        } else {
                            // 新增
                            companyCredit = new BsCompanyCredit();
                            companyCredit.setCompanyId(companyId);
                            companyCredit.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                            companyCredit.setCreditType(BasConstants.CREDIT_TYPE_9);
                            companyCredit.setCreditAmount(BigDecimal.ZERO);
                            companyCredit.setRiskAmount(BigDecimal.ZERO);
                            companyCredit.setUsedCreditAmount(BigDecimal.ZERO);
                            companyCredit.setEffectiveDate(bussStartDate);
                            companyCredit.setExpiryDate(bussEndDate);
                            companyCredit.setCompensationRatio(compensationRatio);
                            companyCredit.setTemporaryAmount(BigDecimal.ZERO);
                            companyCredit.setEnableFlg(true);
                            companyCredit.setCreatedUserId(userId);
                            companyCredit.setCreatedUserName(userName);
                        }
                        bsCompanyCreditService.save(companyCredit);
                    } catch (ApplicationException e) {
                        log.error("授信额度表-自主授信修改失败!", companyName);
                        throw new RuntimeException(e);
                    }
                }

                // 授信额度表-人保授信修改
                BsCompanyCredit companyCredit = bsCompanyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(companyId, BasConstants.CREDIT_TYPE_0, true);
                try {
                    if (Objects.nonNull(companyCredit)) {
                        BigDecimal riskAmount = companyCredit.getRiskAmount();
                        companyCredit.setCreditAmount(approvedQuota);
                        if (riskAmount == null) {
                            companyCredit.setRiskAmount(approvedQuota);   
                        }
                        companyCredit.setEffectiveDate(bussStartDate);
                        companyCredit.setExpiryDate(bussEndDate);
                        companyCredit.setCompensationRatio(compensationRatio);
                        companyCredit.setCreatedUserId(userId);
                        companyCredit.setCreatedUserName(userName);
                    } else {
                        // 新增
                        companyCredit = new BsCompanyCredit();
                        companyCredit.setCompanyId(companyId);
                        companyCredit.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                        companyCredit.setCreditType(BasConstants.CREDIT_TYPE_0);
                        companyCredit.setCreditAmount(approvedQuota);
                        companyCredit.setRiskAmount(approvedQuota);
                        companyCredit.setUsedCreditAmount(BigDecimal.ZERO);
                        companyCredit.setEffectiveDate(bussStartDate);
                        companyCredit.setExpiryDate(bussEndDate);
                        companyCredit.setCompensationRatio(compensationRatio);
                        companyCredit.setTemporaryAmount(BigDecimal.ZERO);
                        companyCredit.setEnableFlg(true);
                        companyCredit.setCreatedUserId(userId);
                        companyCredit.setCreatedUserName(userName);
                    }
                    bsCompanyCreditService.save(companyCredit);
                } catch (ApplicationException e) {
                    log.error("授信额度表-人保授信修改失败!", companyName);
                    throw new RuntimeException(e);
                }

                return true;
            } else {
                log.error("initInsurance {}-企业不存在!", companyName);
            }
        }
        return false;
    }

    public String getNewCreditCategory(String creditCategory, String newCategory) {
        if (StringUtils.isNotBlank(creditCategory)) {
            // 将字符串分割为字符串列表
            List<String> categories = Arrays.asList(creditCategory.split(","));

            // 添加新的字符串类别，确保不重复
            if (!categories.contains(newCategory)) {
                categories = categories.stream().collect(Collectors.toList()); // 复制列表以防止不变性问题
                categories.add(newCategory);
            }

            // 对列表进行字典顺序排序
            categories = categories.stream().sorted().collect(Collectors.toList());

            // 将列表重新拼接成逗号分割的字符串
            return String.join(",", categories);
        } else {
            return BasConstants.CREDIT_TYPE_0;
        }

    }

    /**
     * 格式化企业名称数据
     * 英文括号替换为中文括号
     * @param companyName
     * @return
     */
    private String formatterCompanyName(String companyName){
        if (companyName.contains("(")){
            companyName = companyName.replaceAll("\\(","（").replaceAll("\\)","）");
        }
        return companyName.trim();
    }
}
