package com.spt.bas.server.service.impl;

import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.vo.DaDiExcelVo;
import com.spt.bas.client.vo.ImportExcelVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IDaDiAmountImportService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 大地额度导入处理
 */
@Slf4j
@Component
public class DaDiAmountImportServiceImpl extends ExelImportUtil implements IDaDiAmountImportService {
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private IBsCompanyCreditService bsCompanyCreditService;

    /**
     * 大地额度初始化导入系统
     *
     * @param importExcelVo
     */
    @Override
    @ServerTransactional
    public List<String> initDaDiData(ImportExcelVo importExcelVo) {
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
            List<DaDiExcelVo> excelInfo = getDaDiExcelInfo(inputStream);

            log.info(JsonUtil.obj2Json(excelInfo));
            if (CollectionUtils.isEmpty(excelInfo)) {
                log.error("excelInfo is empty!");
                piccMessageList.add("excelInfo is empty!");
                return piccMessageList;
            }

            ExecutorService executorService = Executors.newCachedThreadPool();
            ExecutorCompletionService<Integer> execu = new ExecutorCompletionService<>(executorService);
            int taskSize = excelInfo.size();
            float bathSize = 25F;
            int syncSuccessNum = 0;
            int bath = (int) Math.ceil((taskSize / bathSize));
            for (int i = 0; i < bath; i++) {
                int start = (int) (bathSize * i);
                int end = (int) (start + bathSize);
                end = Math.min(end, taskSize);
                List<DaDiExcelVo> list = excelInfo.subList(start, end);
                execu.submit(() -> saveInitData(list, userId, userName));
            }
            for (int i = 0; i < bath; i++) {
                Future<Integer> future = execu.take();
                log.info("result:{} OK,syncSuccessNum:{}", i, future.get());
                syncSuccessNum += future.get();
            }
            executorService.shutdown();

            log.info("initDaDiDate 读取到数据{}条", excelInfo.size());
            log.info("initDaDiDate 导入成功:{}条", syncSuccessNum);
            log.info("initDaDiDate success耗时:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            piccMessageList.add("读取"+excelInfo.size()+"条企业数据;");
            piccMessageList.add("导入成功"+syncSuccessNum+"家企业授信数据;");
            piccMessageList.add("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)*1.0/1000+"秒;");
            return piccMessageList;
        } catch (Exception e){
            log.error("导入大地额度失败:{}",e);
            piccMessageList.add("导入大地额度失败,请联系管理员;");
            return piccMessageList;
        }
    }

    private int saveInitData(List<DaDiExcelVo> list, Long userId, String userName) {
        int successInt = 0;
        for (DaDiExcelVo daDi : list) {
            boolean existCompany = updateDaDiCreditAmount(daDi, userId, userName);
            if(existCompany){
                successInt = successInt+1;
            }
        }
        return successInt;
    }


    /**
     * 更新大地数据
     *
     * @param daDi
     */
    private boolean updateDaDiCreditAmount(DaDiExcelVo daDi, Long userId, String userName) {
        if (Objects.nonNull(daDi) && StringUtils.isNotBlank(daDi.getCompanyName()) && StringUtils.isNotBlank(daDi.getDaDiCreditAmount())) {
            String companyName = formatterCompanyName(daDi.getCompanyName());
            List<BsCompany> companyList = bsCompanyDao.queryCompanyName(companyName, BasConstants.ZG_ENTERPRISE_ID);
            if (CollectionUtils.isNotEmpty(companyList)) {
                BsCompany company = companyList.get(0);
                Long companyId = company.getId();
                // 大地额度
                BigDecimal daDiCreditAmount = new BigDecimal(daDi.getDaDiCreditAmount());
                // 授信类别
                String newCreditCategory = getNewCreditCategory(company.getCreditCategory(), BasConstants.CREDIT_TYPE_1);
                bsCompanyDao.updateDaDiAmount(companyId,daDiCreditAmount, newCreditCategory);
                // 授信额度表-大地授信修改
                BsCompanyCredit companyCredit = bsCompanyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(companyId, BasConstants.CREDIT_TYPE_1, true);
                try {
                    if (Objects.nonNull(companyCredit)) {
                        companyCredit.setCreditAmount(daDiCreditAmount);
                        BigDecimal riskAmount = companyCredit.getRiskAmount();
                        if (riskAmount == null) {
                            companyCredit.setRiskAmount(daDiCreditAmount);
                        }
                    } else {
                        companyCredit = new BsCompanyCredit();
                        companyCredit.setCompanyId(companyId);
                        companyCredit.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                        companyCredit.setCreditType(BasConstants.CREDIT_TYPE_1);
                        companyCredit.setCreditAmount(daDiCreditAmount);
                        companyCredit.setRiskAmount(daDiCreditAmount);
                        companyCredit.setUsedCreditAmount(BigDecimal.ZERO);
                        companyCredit.setTemporaryAmount(BigDecimal.ZERO);
                        companyCredit.setEnableFlg(true);
                        companyCredit.setCreatedUserId(userId);
                        companyCredit.setCreatedUserName(userName);
                    }
                    bsCompanyCreditService.save(companyCredit);
                } catch (ApplicationException e) {
                    log.error("授信额度表-大地授信修改失败!", companyName);
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
            return BasConstants.CREDIT_TYPE_1;
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
