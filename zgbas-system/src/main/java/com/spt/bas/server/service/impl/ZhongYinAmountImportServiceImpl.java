package com.spt.bas.server.service.impl;

import com.google.common.base.Stopwatch;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.PiccSupplementInfo;
import com.spt.bas.client.entity.ZhongYinSupplementInfo;
import com.spt.bas.client.vo.DaDiExcelVo;
import com.spt.bas.client.vo.ImportExcelVo;
import com.spt.bas.client.vo.ZhongYinExcelVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.PiccSupplementInfoDao;
import com.spt.bas.server.dao.ZhongYinSupplementInfoDao;
import com.spt.bas.server.service.IBsCompanyCreditService;
import com.spt.bas.server.service.IDaDiAmountImportService;
import com.spt.bas.server.service.IZhongYinAmountImportService;
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
 * 中银额度导入处理
 */
@Slf4j
@Component
public class ZhongYinAmountImportServiceImpl extends ExelImportUtil implements IZhongYinAmountImportService {
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Autowired
    private IBsCompanyCreditService bsCompanyCreditService;
    @Autowired
    private ZhongYinSupplementInfoDao zhongYinSupplementInfoDao;

    /**
     * 中银额度初始化导入系统
     *
     * @param importExcelVo
     */
    @Override
    @ServerTransactional
    public List<String> initZhongYinData(ImportExcelVo importExcelVo) {
        List<String> zhongYinMessageList = new ArrayList<>();
        String fileId = importExcelVo.getFileId();
        Long userId = importExcelVo.getUserId();
        String userName = importExcelVo.getUserName();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            if (StringUtils.isBlank(fileId)) {
                log.error("fileId 不可为空!");
                zhongYinMessageList.add("fileId 不可为空!");
                return zhongYinMessageList;
            }
            fileId = fileId.replace(",", "");
            if (StringUtils.isBlank(fileId)) {
                log.error("无效的fileId!");
                zhongYinMessageList.add("无效的fileId!");
                return zhongYinMessageList;
            }
            InputStream inputStream = null;
            try {
                URL url = new URL(fileServerUrl + "/view/download/" + fileId);
                inputStream = url.openStream();
            } catch (Exception e) {
                log.error("openStream error", e);
                zhongYinMessageList.add("openStream error");
                return zhongYinMessageList;
            }
            List<ZhongYinExcelVo> excelInfo = getZhongYinExcelInfo(inputStream);

            log.info(JsonUtil.obj2Json(excelInfo));
            if (CollectionUtils.isEmpty(excelInfo)) {
                log.error("excelInfo is empty!");
                zhongYinMessageList.add("excelInfo is empty!");
                return zhongYinMessageList;
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
                List<ZhongYinExcelVo> list = excelInfo.subList(start, end);
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
            zhongYinMessageList.add("读取"+excelInfo.size()+"条企业数据;");
            zhongYinMessageList.add("导入成功"+syncSuccessNum+"家企业授信数据;");
            zhongYinMessageList.add("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)*1.0/1000+"秒;");
            return zhongYinMessageList;
        } catch (Exception e){
            log.error("导入中银额度失败:{}",e);
            zhongYinMessageList.add("导入中银额度失败,请联系管理员;");
            return zhongYinMessageList;
        }
    }

    private int saveInitData(List<ZhongYinExcelVo> list, Long userId, String userName) {
        int successInt = 0;
        for (ZhongYinExcelVo zhongYin : list) {
            boolean existCompany = updateZhongYinCreditAmount(zhongYin, userId, userName);
            if(existCompany){
                successInt = successInt+1;
            }
        }
        return successInt;
    }


    /**
     * 更新大地数据
     *
     * @param zhongYin
     */
    private boolean updateZhongYinCreditAmount(ZhongYinExcelVo zhongYin, Long userId, String userName) {
        if (Objects.nonNull(zhongYin) && StringUtils.isNotBlank(zhongYin.getCompanyName()) && StringUtils.isNotBlank(zhongYin.getZhongYinCreditAmount())) {
            String companyName = formatterCompanyName(zhongYin.getCompanyName());
            List<BsCompany> companyList = bsCompanyDao.queryCompanyName(companyName, BasConstants.ZG_ENTERPRISE_ID);
            if (CollectionUtils.isNotEmpty(companyList)) {
                BsCompany company = companyList.get(0);
                Long companyId = company.getId();
                // 大地额度
                BigDecimal zhongYinCreditAmount = new BigDecimal(zhongYin.getZhongYinCreditAmount());
                // 授信类别
                String newCreditCategory = getNewCreditCategory(company.getCreditCategory(), BasConstants.CREDIT_TYPE_2);
                company.setCreditCategory(newCreditCategory);
                company.setZhongYinApplyStatus(BasConstants.ZHONG_YIN_APPLY_STATUS_2);
                company.setZhongYinApproveDate(zhongYin.getZhongYinApproveDate());
                bsCompanyDao.save(company);

                if(companyId != null) {
                    ZhongYinSupplementInfo zhongYinSupplementInfo = zhongYinSupplementInfoDao.findByCompanyId(companyId);
                    if(Objects.isNull(zhongYinSupplementInfo)) {
                        zhongYinSupplementInfo = new ZhongYinSupplementInfo();
                        zhongYinSupplementInfo.setCompanyId(companyId);
                    }
                    zhongYinSupplementInfo.setApproveResult(zhongYin.getZhongYinCreditAmount());
                    zhongYinSupplementInfoDao.save(zhongYinSupplementInfo);
                }
                
                // 授信额度表-中银授信修改
                BsCompanyCredit companyCredit = bsCompanyCreditService.findByCompanyIdAndCreditTypeAndEnableFlg(companyId, BasConstants.CREDIT_TYPE_2, true);
                try {
                    if (Objects.nonNull(companyCredit)) {
                        companyCredit.setCreditAmount(zhongYinCreditAmount);
                        BigDecimal riskAmount = companyCredit.getRiskAmount();
                        if (riskAmount == null) {
                            companyCredit.setRiskAmount(zhongYinCreditAmount);
                        }
                    } else {
                        companyCredit = new BsCompanyCredit();
                        companyCredit.setCompanyId(companyId);
                        companyCredit.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
                        companyCredit.setCreditType(BasConstants.CREDIT_TYPE_2);
                        companyCredit.setCreditAmount(zhongYinCreditAmount);
                        companyCredit.setRiskAmount(zhongYinCreditAmount);
                        companyCredit.setUsedCreditAmount(BigDecimal.ZERO);
                        companyCredit.setTemporaryAmount(BigDecimal.ZERO);
                        companyCredit.setEnableFlg(true);
                        companyCredit.setCreatedUserId(userId);
                        companyCredit.setCreatedUserName(userName);
                    }
                    bsCompanyCreditService.save(companyCredit);
                } catch (ApplicationException e) {
                    log.error("授信额度表-中银授信修改失败!", companyName);
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
