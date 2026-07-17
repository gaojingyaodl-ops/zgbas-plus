package com.spt.bas.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.hsoft.file.sdk.entity.SysFile;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.vo.FileSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.entity.ApplyConfirmReceiptDcsx;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.dao.ApplyConfirmReceiptDao;
import com.spt.bas.server.dao.ApplyConfirmReceiptDcsxDao;
import com.spt.bas.server.dao.ApplyInvoiceDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author MoonLight
 * @Date 2023/8/17 14:53
 * @Version 1.0
 */
@Component
public class CtrContractDownloadService extends BaseService<CtrContract> {
    private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Resource
    private FileRemote fileRemote;
    @Resource
    private CtrContractDao ctrContractDao;
    @Resource
    private ICtrContractService ctrContractService;
    @Resource
    private IApplyDcsxService applyDcsxService;
    @Resource
    private IApproveWaitDealService approveWaitDealService;
    @Resource
    private ApplyConfirmReceiptDao applyConfirmReceiptDao;
    @Resource
    private ApplyConfirmReceiptDcsxDao applyConfirmReceiptDcsxDao;
    @Resource
    private ApplyInvoiceDao applyInvoiceDao;

    @Value("${zip.file.directory}")
    private String zipFileDirectory;
    @Override
    public BaseDao<CtrContract> getBaseDao() {
        return ctrContractDao;
    }

    /**
     * 下载合同双签附件ZIP
     *
     * @param searchVo
     * @return
     */
    public DownLoadContractRespVo downloadContractFileZip(ContractSearchVo searchVo) {
        Date queryDate = new Date();
        DownLoadContractRespVo respVo = new DownLoadContractRespVo();
        Stopwatch started = Stopwatch.createStarted();
        logger.info("=====> Begin downloadContractFileZip <=====");
        try {
            String zipFileName = "contractFile"+ RandomUtil.randomNumbers(5) + ".zip";
            List<DownLoadContractVo> resultList = this.downloadContract(searchVo);
            if (resultList.size() > 500) {
                resultList = resultList.subList(0, 500);
            }
            respVo.setZipFile(createZipFile(resultList, zipFileName, zipFileDirectory));
            String path = searchVo.getRequestUrl() + "/download/" + zipFileName;
            approveWaitDealService.addZipFileDeal(searchVo, DateOperator.formatDate(queryDate, true), path);
        } catch (Exception e) {
            logger.error("downloadContractFileZip error", e);
        }
        logger.info("=====> End downloadContractFileZip 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
        return respVo;
    }
    /**
     * 下载合同双签附件合并PDF
     *
     * @return
     */
    public DownLoadContractRespVo downloadContractFileMergePdf(List<CtrContractFileDownloadVo> fileDownloadVoList) {

        DownLoadContractRespVo respVo = new DownLoadContractRespVo();
        asyncDownloadContractFileMergePdf(fileDownloadVoList);

        return respVo;
    }

    public DownLoadContractRespVo downloadDcsxContractFileMergePdf(List<CtrContractFileDownloadVo> fileDownloadVoList) {

        DownLoadContractRespVo respVo = new DownLoadContractRespVo();
        asyncDownloadDcsxContractFileMergePdf(fileDownloadVoList);

        return respVo;
    }
    public void asyncDownloadContractFileMergePdf(List<CtrContractFileDownloadVo> fileDownloadVoList){
        SCHEDULED_POOL.schedule(() -> {
            Date queryDate = new Date();
            Stopwatch started = Stopwatch.createStarted();
            logger.info("=====> Begin downloadContractFileMergePdf <=====");
            try {
                String pdfFileName = "contractFile"+ RandomUtil.randomNumbers(5) + ".pdf";
                List<DownLoadContractVo> resultList = this.downloadContract(fileDownloadVoList);
                if (resultList.size() > 500) {
                    resultList = resultList.subList(0, 500);
                }


                mergePdfAndImages(resultList, pdfFileName, zipFileDirectory);
                CtrContractFileDownloadVo fileDownloadVo = fileDownloadVoList.get(0);

                String path = fileDownloadVo.getRequestUrl() + "/download/" + pdfFileName;
                approveWaitDealService.addZipFileDeal(fileDownloadVo.getEnterpriseId(), fileDownloadVo.getUserId(), DateOperator.formatDate(queryDate, true), path);
            } catch (Exception e) {
                logger.error("downloadContractFileZip error", e);
            }
            logger.info("=====> End downloadContractFileMergePdf 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
        }, 1, TimeUnit.SECONDS);
    }

    public void asyncDownloadDcsxContractFileMergePdf(List<CtrContractFileDownloadVo> fileDownloadVoList){
        SCHEDULED_POOL.schedule(() -> {
            Date queryDate = new Date();
            Stopwatch started = Stopwatch.createStarted();
            logger.info("=====> Begin downloadDcsxContractFileMergePdf <=====");
            try {
                String pdfFileName = "contractFile"+ RandomUtil.randomNumbers(5) + ".pdf";
                List<DownLoadContractVo> resultList = this.downloadDcsxContract(fileDownloadVoList);
                if (resultList.size() > 500) {
                    resultList = resultList.subList(0, 500);
                }


                mergePdfAndImages(resultList, pdfFileName, zipFileDirectory);
                CtrContractFileDownloadVo fileDownloadVo = fileDownloadVoList.get(0);

                String path = fileDownloadVo.getRequestUrl() + "/download/" + pdfFileName;
                approveWaitDealService.addZipFileInvoiceDcsx(fileDownloadVo.getEnterpriseId(), fileDownloadVo.getUserId(), DateOperator.formatDate(queryDate, true), path);
            } catch (Exception e) {
                logger.error("downloadContractFileZip error", e);
            }
            logger.info("=====> End downloadDcsxContractFileMergePdf 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * 下载合同双签附件
     *
     * @param searchVo
     * @return
     */
    public List<DownLoadContractVo> downloadContract(ContractSearchVo searchVo) throws Exception {
        searchVo.setOutLineFlg(true);
        searchVo.setRows(50);
        List<DownLoadContractVo> resultList = new ArrayList<>();
        if (Boolean.TRUE.equals(searchVo.getDcsxSearchFlg())) {
            Page<DcsxShowVo> pageContract = applyDcsxService.findPageContract(searchVo);
            while (pageContract != null && CollectionUtils.isNotEmpty(pageContract.getContent()) && resultList.size() < 500) {
                downloadContractFile(pageContract.getContent(), null, resultList, searchVo);
                if (pageContract.hasNext()) {
                    searchVo.setPage(searchVo.getPage() + 1);
                    pageContract = applyDcsxService.findPageContract(searchVo);
                } else {
                    pageContract = null;
                }
            }
        } else {
            Page<ContractShowVo> pageContract = ctrContractService.findPageContract(searchVo);
            while (pageContract != null && CollectionUtils.isNotEmpty(pageContract.getContent()) && resultList.size() < 500) {
                downloadContractFile(null, pageContract.getContent(), resultList, searchVo);
                if (pageContract.hasNext()) {
                    searchVo.setPage(searchVo.getPage() + 1);
                    pageContract = ctrContractService.findPageContract(searchVo);
                } else {
                    pageContract = null;
                }
            }
        }
        return resultList;
    }

    /**
     * 下载合同双签附件
     *
     * @return
     */
    public List<DownLoadContractVo> downloadContract(List<CtrContractFileDownloadVo> fileDownloadVoList) throws Exception {
        List<DownLoadContractVo> resultList = new ArrayList<>();
        downloadContractFile(fileDownloadVoList, resultList);
        return resultList;
    }

    /**
     * 下载中游合同双签附件
     *
     * @return
     */
    public List<DownLoadContractVo> downloadDcsxContract(List<CtrContractFileDownloadVo> fileDownloadVoList) throws Exception {
        List<DownLoadContractVo> resultList = new ArrayList<>();
        downloadDcsxContractFile(fileDownloadVoList, resultList);
        return resultList;
    }

    /**
     * 下载合同双签附件
     *
     * @param contentList
     * @param resultList
     * @return
     */
    private void downloadContractFile(List<DcsxShowVo> dcsxList, List<ContractShowVo> contentList, List<DownLoadContractVo> resultList, ContractSearchVo searchVo) throws Exception {
        List<DownLoadContractVo> downloadList = new ArrayList<>();
        String downLoadType = searchVo.getDownLoadType();
        if (CollectionUtils.isNotEmpty(dcsxList)) {
            List<Long> contractIdList = dcsxList.stream().map(DcsxShowVo::getId).collect(Collectors.toList());
            List<ApplyConfirmReceiptDcsx> confirmList = applyConfirmReceiptDcsxDao.findByContractIdList(contractIdList);
            Map<Long, List<ApplyConfirmReceiptDcsx>> confirmMap = confirmList.stream().collect(Collectors.groupingBy(ApplyConfirmReceiptDcsx::getContractId));
            Map<String, List<CtrInvoiceDataVo>> invoiceMap = new HashMap<>();
            if (StringUtils.equals("2", downLoadType)) {
                List<String> contractNoList = dcsxList.stream().map(DcsxShowVo::getContractNo).collect(Collectors.toList());
                List<CtrInvoiceDataVo> invoiceFile = applyInvoiceDao.findInvoiceFile(contractNoList);
                if (CollectionUtils.isNotEmpty(invoiceFile)){
                    invoiceMap = invoiceFile.stream().collect(Collectors.groupingBy(CtrInvoiceDataVo::getContractNo));
                }
            }
            for (DcsxShowVo dcsx : dcsxList) {
                // 合同双签附件
                downloadList.add(new DownLoadContractVo(getContractDownLoadFileNameDcsx(dcsx.getContractNo(), dcsx.getCompanyName()), getContractDownLoadFileIdDcsx(dcsx)));
                // 代采赊销开票
                if (StringUtils.equals("2", downLoadType) && invoiceMap.containsKey(dcsx.getContractNo())){
                    String invoiceFileIds = invoiceMap.get(dcsx.getContractNo()).stream()
                            .map(CtrInvoiceDataVo::getFileId)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(BasConstants.COMMA));
                    buildDownLoad(downloadList, "_发票_", invoiceFileIds, dcsx.getContractNo(), dcsx.getCompanyName());
                } else {
                    // 确认收货附件
                    if (confirmMap.containsKey(dcsx.getId())){
                        String confirmFileIds = confirmMap.get(dcsx.getId()).stream()
                                .map(ApplyConfirmReceiptDcsx::getFileId)
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(BasConstants.COMMA));
                        buildDownLoad(downloadList, "_收货确认_", confirmFileIds, dcsx.getContractNo(), dcsx.getCompanyName());
                    }
                }
            }
        } else if (CollectionUtils.isNotEmpty(contentList)){
            List<Long> contractIdList = contentList.stream().map(ContractShowVo::getId).collect(Collectors.toList());
            List<ApplyConfirmReceipt> confirmList = applyConfirmReceiptDao.findByContractIdList(contractIdList);
            Map<Long, List<ApplyConfirmReceipt>> confirmMap = confirmList.stream().collect(Collectors.groupingBy(ApplyConfirmReceipt::getContractId));
            for (ContractShowVo content : contentList) {
                // 合同双签附件
                downloadList.add(new DownLoadContractVo(getContractDownLoadFileName(content.getContractNo(), content.getCompanyName()), getContractDownLoadFileId(content)));

                // 确认收货附件
                if (confirmMap.containsKey(content.getId())){
                    String confirmFileIds = confirmMap.get(content.getId()).stream()
                            .map(ApplyConfirmReceipt::getFileId)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.joining(BasConstants.COMMA));
                    buildDownLoad(downloadList, "_收货确认_", confirmFileIds, content.getContractNo(), content.getCompanyName());
                }
            }
        }

        // 获取可用的处理器核心数
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = downloadList.size();
        float bathSize = 25F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<DownLoadContractVo> syncList = downloadList.subList(start, end);
            execu.submit(() -> {
                processDownLoad(syncList, resultList);
                return "downloadContractFile OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            logger.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    /**
     * 下载合同双签附件
     *
     * @param resultList
     * @return
     */
    private void downloadContractFile(List<CtrContractFileDownloadVo> fileDownloadVoList, List<DownLoadContractVo> resultList) throws Exception {
        List<DownLoadContractVo> downloadList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileDownloadVoList)){
            for (CtrContractFileDownloadVo content : fileDownloadVoList) {
                // 合同双签附件
                downloadList.add(new DownLoadContractVo(getContractDownLoadFileName(content.getContractNo(), content.getCompanyName(),RandomUtil.randomNumbers(5)), getContractDownLoadFileId(content)));
            }
        }

        // 获取可用的处理器核心数
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = downloadList.size();
        float bathSize = 25F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<DownLoadContractVo> syncList = downloadList.subList(start, end);
            execu.submit(() -> {
                processDownLoad(syncList, resultList);
                return "downloadContractFile OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            logger.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    /**
     * 下载合同双签附件
     *
     * @param resultList
     * @return
     */
    private void downloadDcsxContractFile(List<CtrContractFileDownloadVo> fileDownloadVoList, List<DownLoadContractVo> resultList) throws Exception {
        List<DownLoadContractVo> downloadList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fileDownloadVoList)){
            for (CtrContractFileDownloadVo content : fileDownloadVoList) {
                // 合同双签附件
                downloadList.add(new DownLoadContractVo(getContractDownLoadFileName(content.getContractNo(), content.getCompanyName(),RandomUtil.randomNumbers(5)), getDcsxContractDownLoadFileId(content)));
            }
        }

        // 获取可用的处理器核心数
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        ExecutorCompletionService<String> execu = new ExecutorCompletionService<>(executorService);
        int taskSize = downloadList.size();
        float bathSize = 25F;
        int bath = (int) Math.ceil((taskSize / bathSize));
        for (int i = 0; i < bath; i++) {
            int start = (int) (bathSize * i);
            int end = (int) (start + bathSize);
            end = Math.min(end, taskSize);
            List<DownLoadContractVo> syncList = downloadList.subList(start, end);
            execu.submit(() -> {
                processDownLoad(syncList, resultList);
                return "downloadContractFile OK";
            });
        }
        for (int i = 0; i < bath; i++) {
            Future<String> future = execu.take();
            logger.info("result:{},{}", i, future.get());
        }
        executorService.shutdown();
    }

    private void buildDownLoad(List<DownLoadContractVo> downloadList, String fileType, String targetFileIdStr, String contractNo, String companyName){
        if (StringUtils.isNotBlank(targetFileIdStr)){
            List<String> targetIdList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(targetFileIdStr);
            for (int i = 0; i < targetIdList.size(); i++) {
                downloadList.add(new DownLoadContractVo(getContractDownLoadFileName(contractNo, companyName) + fileType + i, getContractDownLoadFileId(targetIdList.get(i))));
            }
        }
    }

    private void processDownLoad(List<DownLoadContractVo> downloadList, List<DownLoadContractVo> resultList) {
        Stopwatch started = Stopwatch.createStarted();
        String fileIds = downloadList.stream().map(DownLoadContractVo::getFileId).filter(Objects::nonNull).map(Objects::toString).collect(Collectors.joining(BasConstants.COMMA));
        FileSearchVo fileSearchVo = new FileSearchVo();
        fileSearchVo.setFileIds(fileIds);
        List<SysFile> sysFiles = fileRemote.loadFiles(fileSearchVo);
        if (CollectionUtils.isEmpty(sysFiles)) {
            return;
        }
        Map<Long, SysFile> fileMap = sysFiles.stream().collect(Collectors.toMap(SysFile::getId, s -> s, (a, b) -> b));
        if (fileMap.isEmpty()) {
            return;
        }
        downloadFiles(downloadList, resultList, fileMap);
        logger.info("=====> 下载双签附件, 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
    }


    /**
     * 根据文件名获取文件类型
     *
     * @param sysFile
     * @return
     */
    private String getFileType(SysFile sysFile) {
        String fileType = ".pdf";
        String contentType = sysFile.getContentType();
        if (StringUtils.isNotBlank(contentType)) {
            List<String> result = Splitter.on(BasConstants.OBL).omitEmptyStrings().splitToList(contentType);
            fileType = result.size() > 1 ? "." + result.get(result.size() - 1) : fileType;
        }
        return fileType;
    }

    private String getContractDownLoadFileName(String contractNo, String companyName) {
        return getPrefixContractNo(contractNo) + BasConstants.UNDER + getAbbreviation(companyName);
    }
    private String getContractDownLoadFileName(String contractNo, String companyName, String randomNum) {
        return getPrefixContractNo(contractNo) + BasConstants.UNDER + getAbbreviation(companyName) + randomNum;
    }

    private String getContractDownLoadFileNameDcsx(String contractNo, String companyName) {
        return getPrefixContractNo(contractNo) + BasConstants.UNDER + getAbbreviation(companyName);
    }

    private String getPrefixContractNo(String contractNo){
        if (StringUtils.isBlank(contractNo)){
            return contractNo;
        }
        if (contractNo.contains("SPTB") || contractNo.contains("KCB") || contractNo.contains("XYB")){
            return contractNo.replaceAll("\\D","") + BasConstants.UNDER + "采购";
        }
        if (contractNo.contains("SPTS") || contractNo.contains("KCS") || contractNo.contains("XYS")){
            return contractNo.replaceAll("\\D","") + BasConstants.UNDER + "销售";
        }
        if (contractNo.contains("SPTX") || contractNo.contains("KCX") || contractNo.contains("XYX")){
            return contractNo.replaceAll("\\D","") + BasConstants.UNDER + "中游";
        }
        return contractNo;
    }

    /**
     * 不同业务，不同合同，最终确认一个附件ID
     *
     * @param contract
     * @return
     */
    private Long getContractDownLoadFileId(ContractShowVo contract) {
        String downLoadFileId = "";
        String contractType = contract.getContractType();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, contract.getBusinessType())) {
            downLoadFileId = StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contractType) ? contract.getSellContentFileId() : contract.getBuyContentFileId();
        } else {
            downLoadFileId = contract.getFileId();
        }
        if (StringUtils.isNotBlank(downLoadFileId)) {
            List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(downLoadFileId);
            downLoadFileId = CollectionUtils.isNotEmpty(fileList) ? fileList.get(fileList.size() - 1) : "";
        }
        return getContractDownLoadFileId(downLoadFileId);
    }
    /**
     * 不同业务，不同合同，最终确认一个附件ID
     *
     * @param contract
     * @return
     */
    private Long getContractDownLoadFileId(CtrContractFileDownloadVo contract) {
        String downLoadFileId = "";
        String contractType = contract.getContractType();
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, contract.getBusinessType())) {
            downLoadFileId = StringUtils.equals(BasConstants.CONTRACT_TYPE_S, contractType) ? contract.getSellContentFileId() : contract.getBuyContentFileId();
        } else {
            downLoadFileId = contract.getFileId();
        }
        if (StringUtils.isNotBlank(downLoadFileId)) {
            List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(downLoadFileId);
            downLoadFileId = CollectionUtils.isNotEmpty(fileList) ? fileList.get(fileList.size() - 1) : "";
        }
        return getContractDownLoadFileId(downLoadFileId);
    }

    /**
     * 获取中游合同附件ID
     * @param contract
     * @return
     */
    private Long getDcsxContractDownLoadFileId(CtrContractFileDownloadVo contract) {
        String downLoadFileId = contract.getDcsxContractFileId();
        if (StringUtils.isNotBlank(downLoadFileId)) {
            List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(downLoadFileId);
            downLoadFileId = CollectionUtils.isNotEmpty(fileList) ? fileList.get(fileList.size() - 1) : "";
        }
        return getContractDownLoadFileId(downLoadFileId);
    }

    private Long getContractDownLoadFileId(String fileId) {
        return (StringUtils.isNoneBlank(fileId) && NumberUtil.isNumber(fileId)) ? Long.valueOf(fileId) : null;
    }

    /**
     * 不同业务，不同合同，最终确认一个附件ID
     *
     * @param contract
     * @return
     */
    private Long getContractDownLoadFileIdDcsx(DcsxShowVo contract) {
        String downLoadFileId = contract.getFileId();
        if (StringUtils.isNotBlank(downLoadFileId)) {
            List<String> fileList = Splitter.on(BasConstants.OBL).omitEmptyStrings().splitToList(downLoadFileId);
            downLoadFileId = CollectionUtils.isNotEmpty(fileList) ? fileList.get(fileList.size() - 1) : "";
        }
        return (StringUtils.isNoneBlank(downLoadFileId) && NumberUtil.isNumber(downLoadFileId)) ? Long.valueOf(downLoadFileId) : null;
    }
    public void mergePdfAndImages(List<DownLoadContractVo> resultList, String zipFileName, String zipFileDirectory) throws IOException {
        PDDocument outputDoc = new PDDocument();
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        
        for (DownLoadContractVo downLoadContractVo : resultList) {
            String fileType = downLoadContractVo.getFileType().toLowerCase();

            try {
                if (fileType.endsWith(".pdf")) {
                    // 合并 PDF 文件
                    try (PDDocument pdfDoc = PDDocument.load(downLoadContractVo.getTargetFile())) {
                        pdfMerger.appendDocument(outputDoc, pdfDoc); // 将 PDF 合并到 outputDoc 中
                    }
                } else if (fileType.endsWith(".jpg") || fileType.endsWith(".jpeg") || fileType.endsWith(".png")) {
                    // 将图片添加到 PDF
                    PDPage page = new PDPage();
                    outputDoc.addPage(page);
                    PDImageXObject image = PDImageXObject.createFromFile(downLoadContractVo.getTargetFile().getAbsolutePath(), outputDoc);

                    // 获取页面和图片的尺寸
                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();
                    float imageWidth = image.getWidth();
                    float imageHeight = image.getHeight();

                    // 计算缩放比例
                    float scaleX = pageWidth / imageWidth;
                    float scaleY = pageHeight / imageHeight;

                    float scale = Math.min(scaleX, scaleY); // 获取缩放比例

                    // 绘制尺寸决定
                    float drawWidth = imageWidth;
                    float drawHeight = imageHeight;

                    // 如果图片尺寸超过页面，则按比例缩放
                    if (scale < 1.0f) {
                        drawWidth = imageWidth * scale;
                        drawHeight = imageHeight * scale;
                    }

                    PDPageContentStream contentStream = new PDPageContentStream(outputDoc, page);

                    // 计算绘制位置，使图片居中
                    float xOffset = (pageWidth - drawWidth) / 2;
                    float yOffset = (pageHeight - drawHeight) / 2;

                    // 设置图片占满整个页面
                    contentStream.drawImage(image, xOffset, yOffset, drawWidth, drawHeight);
                    contentStream.close();
                }
            } catch (Exception e) {
                logger.error("=====> 合并附件合并失败, 文件路径:{} <=====", downLoadContractVo.getTargetFile().getAbsolutePath());
                logger.error("=====> 合并附件合并失败：{} <=====", e);
            }
            
        }

        // 保存合并后的 PDF 文档
        outputDoc.save(zipFileDirectory+zipFileName);
        outputDoc.close();
    }

    /**
     * 创建压缩文件ZIP
     *
     * @param resultList
     * @param zipFileName
     * @return
     * @throws IOException
     */
    public static File createZipFile(List<DownLoadContractVo> resultList, String zipFileName, String zipFileDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        // 创建目录，如果不存在
        File directory = new File(zipFileDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File zipFile = new File(directory, zipFileName);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (DownLoadContractVo vo : resultList) {
                File targetFile = vo.getTargetFile();
                FileInputStream fileInputStream = new FileInputStream(targetFile);

                // 添加一个 ZIP 条目
                ZipEntry zipEntry = new ZipEntry(targetFile.getName());
                zipOutputStream.putNextEntry(zipEntry);

                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }

                fileInputStream.close();
            }
        }
        return zipFile;
    }

    /**
     * 根据文件URL，下载为临时文件
     *
     * @param downloadList
     * @param resultList
     * @param fileMap
     */
    private List<DownLoadContractVo> downloadFiles(List<DownLoadContractVo> downloadList, List<DownLoadContractVo> resultList, Map<Long, SysFile> fileMap) {
        for (DownLoadContractVo vo : downloadList) {
            Stopwatch started = Stopwatch.createStarted();
            try {
                SysFile sysFile = fileMap.get(vo.getFileId());
                if (Objects.nonNull(sysFile) && !(StringUtils.isNotBlank(sysFile.getOriginalFilename()) && sysFile.getOriginalFilename().contains("出库单"))) {
                    vo.setFileType(getFileType(sysFile));
                    URL fileUrl = new URL(fileServerUrl + "/view/download/" + vo.getFileId());
                    // 创建临时文件，但不包含随机数
                    File tempFile = createCustomTempFile(vo.getFileName(), vo.getFileType());
                    // 下载文件并保存到临时文件
                    FileUtils.copyURLToFile(fileUrl, tempFile);
                    vo.setTargetFile(tempFile);
                    logger.info("创建附件：{}，大小：{}， 耗时：{} ms", vo.getFileName(), tempFile.length(), started.elapsed(TimeUnit.MILLISECONDS));
                    resultList.add(vo);
                    if (resultList.size() >= 500) {
                        return resultList;
                    }
                }
            } catch (Exception e) {
                logger.error("downloadFiles error", e);
            }
        }
        return resultList;
    }


    /**
     * 在指定目录创建一个临时文件不包含随机数
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createCustomTempFile(String fileName, String fileType) throws IOException {
        // 获取系统临时文件目录
        File tempDir = new File(System.getProperty("java.io.tmpdir"));

        // 构建完整的文件路径
        String filePath = tempDir.getAbsolutePath() + File.separator + fileName + fileType;

        // 创建文件
        File tempFile = new File(filePath);
        tempFile.createNewFile();

        return tempFile;
    }

    /**
     * 延时指定时间后，主动删除临时文件
     *
     * @param file
     * @param delay
     * @param timeUnit
     */
    private void scheduleFileDeletion(File file, long delay, TimeUnit timeUnit) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                Files.deleteIfExists(file.toPath());
                logger.info("Temporary file deleted: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, delay, timeUnit);

        executor.shutdown();
    }

    /**
     * 获取公司简称
     *
     * @param fullName
     * @return
     */
    public static String getAbbreviation(String fullName) {
        // 正则表达式匹配规则
        String regex = "(.*?)(有限公司|集团|供应链管理有限公司|贸易有限公司|科技有限公司|塑料包装制品厂|供应链管理有限公司|实业有限公司|有限责任公司|股份有限公司|科技有限公司|进出口有限公司)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fullName);

        if (matcher.matches()) {
            String abbreviation = matcher.group(1);
            return abbreviation.trim();
        } else {
            return fullName;
        }
    }
}
