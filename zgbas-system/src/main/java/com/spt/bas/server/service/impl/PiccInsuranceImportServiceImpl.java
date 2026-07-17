package com.spt.bas.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ContractPiccInsuranceVo;
import com.spt.bas.client.vo.PiccExcelVo;
import com.spt.bas.client.vo.PiccInsuranceExcelVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDcsxDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.InsuranceAmountFlowDao;
import com.spt.bas.server.service.IBsCompanyDcsxService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.IInsuranceAmountFlowService;
import com.spt.bas.server.service.IPiccInsuranceImportService;
import com.spt.bas.server.util.ExelImportUtil;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 大地额度导入处理
 */
@Slf4j
@Component
public class PiccInsuranceImportServiceImpl extends ExelImportUtil implements IPiccInsuranceImportService {
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Autowired
    private CtrContractDao contractDao;
    @Autowired
    private ICtrContractService contractService;
    @Autowired
    private InsuranceAmountFlowDao insuranceAmountFlowDao;
    @Autowired
    private IInsuranceAmountFlowService insuranceAmountFlowService;
    @Autowired
    private BsCompanyDcsxDao companyDcsxDao;
    @Autowired
    private IBsCompanyDcsxService companyDcsxService;


    /**
     * 人保保费流水初始化导入系统
     *
     * @param fileId
     */
    @Override
    public List<String> initPiccInsuranceData(String fileId) {
        List<String> piccMessageList = new ArrayList<>();
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
            List<PiccInsuranceExcelVo> excelInfoList = getPiccInsuranceExcelInfo(inputStream);

            log.info(JsonUtil.obj2Json(excelInfoList));
            if (CollectionUtils.isEmpty(excelInfoList)) {
                log.error("excelInfo is empty!");
                piccMessageList.add("excelInfo is empty!");
                return piccMessageList;
            }

            excelInfoList.sort(Comparator.comparing(PiccInsuranceExcelVo::getEntryDate));


            int syncSuccessNum = updateContractInsurance(excelInfoList);

            log.info("initDaDiDate 读取到数据{}条", excelInfoList.size());
            log.info("initDaDiDate 导入成功:{}条", syncSuccessNum);
            log.info("initDaDiDate success耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            piccMessageList.add("读取" + excelInfoList.size() + "条人保保费流水;");
            piccMessageList.add("导入成功" + syncSuccessNum + "条人保保费流水;");
            piccMessageList.add("耗时" + stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1.0 / 1000 + "秒;");
            return piccMessageList;
        } catch (Exception e) {
            log.error("导入人保保费流水失败:{}", e);
            piccMessageList.add("导入人保保费流水失败,请联系管理员;");
            return piccMessageList;
        }
    }

    @Transactional
    public int updateContractInsurance(List<PiccInsuranceExcelVo> list) throws InterruptedException, ExecutionException {
        insuranceAmountFlowDao.deleteAll();
        List<ContractPiccInsuranceVo> allContractInsurance = contractDao.findContractInsurance();
        Map<String, ContractPiccInsuranceVo> insuranceVoMap = allContractInsurance.stream().collect(Collectors.toMap(ContractPiccInsuranceVo::getContractNo, insuranceVo -> insuranceVo));

        List<BsCompanyDcsx> companyDcsxAll = companyDcsxService.findAll();
        Map<String, BsCompanyDcsx> companyDcsxMap = companyDcsxAll.stream().collect(Collectors.toMap(BsCompanyDcsx::getCompanyName, companyDcsx -> companyDcsx));

        int successInt = 0;
        List<ContractPiccInsuranceVo> updateContractInsuranceList = new ArrayList<>();
        Map<String, BsCompanyDcsx> newCompanyDcsxMap = new HashMap<>();
        List<InsuranceAmountFlow> insuranceAmountFlowList = new ArrayList<>();
        for (PiccInsuranceExcelVo piccInsurance : list) {
            String contractNo = piccInsurance.getContractNo();
            String insuranceRateStr = piccInsurance.getInsuranceRate();
            String insuranceAmountStr = piccInsurance.getInsuranceAmount();

            if (StringUtils.isNotBlank(contractNo) && StringUtils.isNotBlank(insuranceRateStr) && StringUtils.isNotBlank(insuranceAmountStr)) {
                BigDecimal insuranceRate = new BigDecimal(insuranceRateStr);
                BigDecimal insuranceAmount = new BigDecimal(insuranceAmountStr);
                ContractPiccInsuranceVo oldContractPiccInsuranceVo = insuranceVoMap.get(contractNo);

                ContractPiccInsuranceVo contractPiccInsuranceVo = new ContractPiccInsuranceVo();
                if (Objects.nonNull(oldContractPiccInsuranceVo)) {
                    contractPiccInsuranceVo.setInsuranceRate(insuranceRate);
                    contractPiccInsuranceVo.setInsuranceAmount(insuranceAmount);
                    contractPiccInsuranceVo.setInsuranceFlag(true);
                    contractPiccInsuranceVo.setId(oldContractPiccInsuranceVo.getId());
                    updateContractInsuranceList.add(contractPiccInsuranceVo);

                    BsCompanyDcsx companyDcsx = newCompanyDcsxMap.get(oldContractPiccInsuranceVo.getOurCompanyName());
                    if (Objects.isNull(companyDcsx)) {
                        companyDcsx = companyDcsxMap.get(oldContractPiccInsuranceVo.getOurCompanyName());
                    }
                    if (Objects.nonNull(companyDcsx)) {

                        BigDecimal initialAmount = companyDcsx.getInsuranceAmount();
                        if (initialAmount == null) {
                            initialAmount = BigDecimal.ZERO;
                        }

                        // 期末余额（期初余额+合同保费）,期初余额是全部出库的时候减过合同保费的数据，作废要加上合同保费
                        BigDecimal ultimateAmount = initialAmount.subtract(insuranceAmount);
                        InsuranceAmountFlow insuranceAmountFlow = new InsuranceAmountFlow();
                        insuranceAmountFlow.setFundCompanyId(companyDcsx.getId());
                        insuranceAmountFlow.setContractId(oldContractPiccInsuranceVo.getId());
                        insuranceAmountFlow.setFlowType(BasConstants.DICT_TYPE_INSURANCE_AMFL_S);
                        // 流水金额
                        insuranceAmountFlow.setFlowAmount(insuranceAmount.negate());
                        insuranceAmountFlow.setInitialAmount(initialAmount);
                        insuranceAmountFlow.setUltimateAmount(ultimateAmount);
                        String subject = oldContractPiccInsuranceVo.getContractNo() + "," + oldContractPiccInsuranceVo.getTotalAmount() + "元";
                        if (StringUtils.isNotBlank(piccInsurance.getCreditCycle()) && StringUtils.compare(piccInsurance.getCreditCycle(), "0") > 0) {
                            subject += "," + piccInsurance.getCreditCycle() + "天";
                        }
                        insuranceAmountFlow.setSubject(subject);
                        insuranceAmountFlow.setLinkApproveId(oldContractPiccInsuranceVo.getApproveId());
                        Date date = new Date();
                        String entryDate = piccInsurance.getEntryDate();
                        if (StringUtils.isNotBlank(entryDate)) {
                            try {
                                date = DateUtils.parseDate(entryDate, "YYYY-MM-dd");
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        insuranceAmountFlow.setCreatedDate(date);
                        insuranceAmountFlow.setUpdatedDate(date);
                        insuranceAmountFlowList.add(insuranceAmountFlow);

                        // 修改资金方保费余额
                        companyDcsx.setInsuranceAmount(ultimateAmount);
                        newCompanyDcsxMap.put(oldContractPiccInsuranceVo.getOurCompanyName(), companyDcsx);

                        successInt++;
                    }
                }
            }
        }

        if (CollUtil.isNotEmpty(updateContractInsuranceList)) {
            ExecutorService executorService = Executors.newFixedThreadPool(30);
            ExecutorCompletionService<Integer> execu = new ExecutorCompletionService<>(executorService);
            int taskSize = updateContractInsuranceList.size();
            float bathSize = 25F;
            int bath = (int) Math.ceil((taskSize / bathSize));
            int syncSuccessNum = 0;
            for (int i = 0; i < bath; i++) {
                int start = (int) (bathSize * i);
                int end = (int) (start + bathSize);
                end = Math.min(end, taskSize);
                List<ContractPiccInsuranceVo> subList = updateContractInsuranceList.subList(start, end);
                execu.submit(() -> updateContractInsuranceInfo(subList));
            }
            for (int i = 0; i < bath; i++) {
                Future<Integer> future = execu.take();
                log.info("result:{} OK,syncSuccessNum:{}", i, future.get());
                syncSuccessNum += future.get();
            }
            executorService.shutdown();
        }
        if (CollUtil.isNotEmpty(newCompanyDcsxMap)) {
            List<BsCompanyDcsx> companyDcsxList = newCompanyDcsxMap.values()
                    .stream()
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(companyDcsxList)) {
                companyDcsxDao.saveAll(companyDcsxList);
            }
        }
        if (CollUtil.isNotEmpty(insuranceAmountFlowList)) {
            insuranceAmountFlowDao.saveAll(insuranceAmountFlowList);
        }

        return successInt;
    }

    private int updateContractInsuranceInfo(List<ContractPiccInsuranceVo> list) {
        list.forEach(contractInsurance -> {
            contractDao.updateInsuranceInfo(contractInsurance.getId(), contractInsurance.getInsuranceRate(), contractInsurance.getInsuranceAmount(), true);
        });
        return list.size();
    }


//    public int saveInitData(List<PiccInsuranceExcelVo> list) {
//        insuranceAmountFlowDao.deleteAll();
//        int successInt = 0;
//        for (PiccInsuranceExcelVo piccInsurance : list) {
//            boolean existCompany = updateContractInsurance(piccInsurance);
//            if(existCompany){
//                successInt = successInt+1;
//            }
//        }
//        return successInt;
//    }


    /**
     * 更新合同保费
     *
     * @param piccInsurance
     */
    @Transactional
    public boolean updateContractInsurance1(PiccInsuranceExcelVo piccInsurance) {
        if (Objects.nonNull(piccInsurance) && StringUtils.isNotBlank(piccInsurance.getContractNo()) &&
                StringUtils.isNotBlank(piccInsurance.getInsuranceRate()) && StringUtils.isNotBlank(piccInsurance.getInsuranceAmount())) {

            String contractNo = piccInsurance.getContractNo();
            BigDecimal insuranceRate = new BigDecimal(piccInsurance.getInsuranceRate());
            BigDecimal insuranceAmount = new BigDecimal(piccInsurance.getInsuranceAmount());
            CtrContract contract = contractDao.findByContractNo(contractNo);
            if (Objects.nonNull(contract)) {

                contract.setInsuranceRate(insuranceRate);
                contract.setInsuranceAmount(insuranceAmount);
                contract.setInsuranceFlag(true);
                contractDao.save(contract);

                String ourCompanyName = contract.getOurCompanyName();
                BsCompanyDcsx companyDcsx = companyDcsxDao.findByCompanyName(ourCompanyName);


                if (Objects.nonNull(companyDcsx)) {

                    BigDecimal initialAmount = companyDcsx.getInsuranceAmount();
                    if (initialAmount == null) {
                        initialAmount = BigDecimal.ZERO;
                    }

                    // 期末余额（期初余额+合同保费）,期初余额是全部出库的时候减过合同保费的数据，作废要加上合同保费
                    BigDecimal ultimateAmount = initialAmount.subtract(insuranceAmount);
                    InsuranceAmountFlow insuranceAmountFlow = new InsuranceAmountFlow();
                    insuranceAmountFlow.setFundCompanyId(companyDcsx.getId());
                    insuranceAmountFlow.setContractId(contract.getId());
                    insuranceAmountFlow.setFlowType(BasConstants.DICT_TYPE_INSURANCE_AMFL_S);
                    // 流水金额
                    insuranceAmountFlow.setFlowAmount(insuranceAmount.negate());
                    insuranceAmountFlow.setInitialAmount(initialAmount);
                    insuranceAmountFlow.setUltimateAmount(ultimateAmount);
                    String subject = contract.getContractNo() + "," + contract.getTotalAmount() + "元";
                    if (StringUtils.isNotBlank(piccInsurance.getCreditCycle()) && StringUtils.compare(piccInsurance.getCreditCycle(), "0") > 0) {
                        subject += "," + piccInsurance.getCreditCycle() + "天";
                    }
                    insuranceAmountFlow.setSubject(subject);
                    insuranceAmountFlow.setLinkApproveId(contract.getApproveId());
                    Date date = new Date();
                    String entryDate = piccInsurance.getEntryDate();
                    if (StringUtils.isNotBlank(entryDate)) {
                        try {
                            date = DateUtils.parseDate(entryDate, "YYYY-MM-dd");
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    insuranceAmountFlow.setCreatedDate(date);
                    insuranceAmountFlow.setUpdatedDate(date);
                    insuranceAmountFlowDao.save(insuranceAmountFlow);
                    // 修改资金方保费余额
                    companyDcsx.setInsuranceAmount(ultimateAmount);
                    companyDcsxDao.save(companyDcsx);
                }

                return true;
            } else {
                log.error("initInsurance {}-合同不存在!", contractNo);
            }
        }
        log.error("导入保费流水失败：{}", piccInsurance.getContractNo());
        return false;
    }

    /**
     * 格式化企业名称数据
     * 英文括号替换为中文括号
     *
     * @param companyName
     * @return
     */
    private String formatterCompanyName(String companyName) {
        if (companyName.contains("(")) {
            companyName = companyName.replaceAll("\\(", "（").replaceAll("\\)", "）");
        }
        return companyName.trim();
    }
}
