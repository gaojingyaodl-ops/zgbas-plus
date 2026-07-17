package com.spt.bas.server.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.hsoft.file.sdk.entity.SysFile;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.vo.FileSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.service.IApproveWaitDealService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class BsCompanyDownloadService extends BaseService<BsCompany> {
    @Autowired
    private BsCompanyDao bsCompanyDao;
    @Resource
    private IApproveWaitDealService approveWaitDealService;
    @Resource
    private FileRemote fileRemote;
    @Value("${file.server.url}")
    private String fileServerUrl;
    @Value("${zip.file.directory}")
    private String zipFileDirectory;
    @Autowired
    private IBsCompanyService bsCompanyService;
    
    @Override
    public BaseDao<BsCompany> getBaseDao() {
        return bsCompanyDao;
    }

    /**
     * 下载访厂报告附件ZIP
     *
     * @param searchVo
     * @return
     */
    public DownLoadContractRespVo downloadAccessReportFileZip(BsCompanySearchVo searchVo) {
        Date queryDate = new Date();
        DownLoadContractRespVo respVo = new DownLoadContractRespVo();
        Stopwatch started = Stopwatch.createStarted();
        logger.info("=====> Begin downloadContractFileZip <=====");
        try {
            String zipFileName = "accessReportFile"+ RandomUtil.randomNumbers(5) + ".zip";
            List<DownLoadContractVo> resultList = this.downloadCompany(searchVo);
            if (resultList.size() > 500) {
                resultList = resultList.subList(0, 500);
            }
            respVo.setZipFile(createZipFile(resultList, zipFileName, zipFileDirectory));
            String path = searchVo.getRequestUrl() + "/download/" + zipFileName;
            approveWaitDealService.addCompanyZipFileDeal(searchVo, DateOperator.formatDate(queryDate, true), path);
        } catch (Exception e) {
            logger.error("downloadContractFileZip error", e);
        }
        logger.info("=====> End downloadContractFileZip 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
        return respVo;
    }
    /**
     * 下载访厂报告附件
     *
     * @param searchVo
     * @return
     */
    public List<DownLoadContractVo> downloadCompany(BsCompanySearchVo searchVo) throws ExecutionException, InterruptedException, ApplicationException {
        searchVo.setRows(500);
        List<DownLoadContractVo> resultList = new ArrayList<>();
        Page<BsCompanyVo> pageCompany = findPageCompnayVo(searchVo);
        if(pageCompany != null && pageCompany.getContent() != null && pageCompany.getContent().size() > 0) {
            downloadCompanyFile(pageCompany.getContent(), resultList);
//            while (pageCompany != null && CollectionUtils.isNotEmpty(pageCompany.getContent()) && resultList.size() < 500) {
//                if (pageCompany.hasNext()) {
//                    searchVo.setPage(searchVo.getPage() + 1);
//                    pageCompany = findPageCompnayVo(searchVo);
//                } else {
//                    pageCompany = null;
//                }
//            }
        }
        return resultList;
    }

    /**
     * 下载企业访厂报告附件
     *
     * @param resultList
     * @return
     */
    private void downloadCompanyFile(List<BsCompanyVo> companyList, List<DownLoadContractVo> resultList) throws InterruptedException, ExecutionException {
        List<DownLoadContractVo> downloadList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(companyList)){
            for (BsCompanyVo bsCompanyVo : companyList) {
                String accessReportId = bsCompanyVo.getAccessReportId();
                if(StringUtils.isNotBlank(accessReportId)) {
                    DownLoadContractVo vo = new DownLoadContractVo(getCompanyDownLoadFileName(bsCompanyVo), getCompanyDownLoadFileId(bsCompanyVo));
                    downloadList.add(vo);
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

    private List<DownLoadContractVo> processDownLoad(List<DownLoadContractVo> downloadList, List<DownLoadContractVo> resultList) {
        Stopwatch started = Stopwatch.createStarted();
        String fileIds = downloadList.stream().map(DownLoadContractVo::getFileId).filter(Objects::nonNull).map(Objects::toString).collect(Collectors.joining(BasConstants.COMMA));
        FileSearchVo fileSearchVo = new FileSearchVo();
        fileSearchVo.setFileIds(fileIds);
        List<SysFile> sysFiles = fileRemote.loadFiles(fileSearchVo);
        if (CollectionUtils.isEmpty(sysFiles)) {
            return resultList;
        }
        Map<Long, SysFile> fileMap = sysFiles.stream().collect(Collectors.toMap(SysFile::getId, s -> s, (a, b) -> b));
        if (fileMap.isEmpty()) {
            return resultList;
        }
        List<DownLoadContractVo> downLoadContractVos = downloadFiles(downloadList, resultList, fileMap);
        logger.info("=====> 下载访厂报告附件, 耗时:{}s <=====", started.elapsed(TimeUnit.SECONDS));
        return downLoadContractVos;
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
                String originalFileName = sysFile.getOriginalFilename();
                if (Objects.nonNull(sysFile) && !(StringUtils.isNotBlank(originalFileName) && originalFileName.contains("出库单"))) {
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
            fileType = result.size() > 1 ? "." + result.get(1) : fileType;
        }
        return fileType;
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

    private String getCompanyDownLoadFileName(BsCompanyVo company) {
        return company.getCompanyName() + BasConstants.UNDER + "访厂报告" + BasConstants.UNDER + RandomUtil.randomNumbers(5);
    }

    /**
     * 不同业务，不同合同，最终确认一个附件ID
     *
     * @return
     */
    private Long getCompanyDownLoadFileId(BsCompanyVo company) {
        String downLoadFileId = company.getAccessReportId();
        if (StringUtils.isNotBlank(downLoadFileId)) {
            List<String> fileList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(downLoadFileId);
            downLoadFileId = CollectionUtils.isNotEmpty(fileList) ? fileList.get(fileList.size() - 1) : "";
        }
        return getCompanyDownLoadFileId(downLoadFileId);
    }

    private Long getCompanyDownLoadFileId(String fileId) {
        return (StringUtils.isNoneBlank(fileId) && NumberUtil.isNumber(fileId)) ? Long.valueOf(fileId) : null;
    }

    /**
     * 创建压缩文件ZIP
     *
     * @param resultList
     * @param zipFileName
     * @return
     * @throws IOException
     */
    public File createZipFile(List<DownLoadContractVo> resultList, String zipFileName, String zipFileDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        // 创建目录，如果不存在
        File directory = new File(zipFileDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File zipFile = new File(directory, zipFileName);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (DownLoadContractVo vo : resultList) {
                try {
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
                } catch (Exception e) {
                    logger.error("写入文件异常",e);
                }
                
            }
        }
        return zipFile;
    }
    public Page<BsCompanyVo> findPageCompnayVo(BsCompanySearchVo queryVo) throws ApplicationException {
        // 展示所有用户
        Page<BsCompany> page = bsCompanyService.findPageCompnay(queryVo);
        List<BsCompanyVo> voList = new ArrayList<>();
        for (BsCompany company : page.getContent()) {
            BsCompanyVo vo = new BsCompanyVo();
            BeanUtils.copyProperties(company, vo);
            voList.add(vo);
        }
        PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
        Page<BsCompanyVo> pageVo = new PageImpl<>(voList, pageRequest, page.getTotalElements());
        return pageVo;
    }

    public void exportCreditInfo0Excel(CompanyCreditExportVo companyCreditExportVo) {
        logger.info("人保授信导出开始==");
        List<CompanyCreditInfo0Vo> content = companyCreditExportVo.getCompanyCreditInfo0VoList();
        BsCompanySearchVo searchVo = companyCreditExportVo.getBsCompanySearchVo();
        Date queryDate = new Date();
        String companmydirNmae = "companyCredit0Files" + RandomUtil.randomNumbers(5);
        // 创建临时目录用于缓存文件
        File tempDir = new File(System.getProperty("java.io.tmpdir"), companmydirNmae);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        int batchSize = 20;
        int totalBatches = (int) Math.ceil((double) content.size() / batchSize);
        // 创建一个ByteArrayOutputStream来存储zip内容
        try {
            int numThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
            // 遍历文件，分批下载并处理
            for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
                int start = batchIndex * batchSize;
                int end = Math.min(start + batchSize, content.size());
                List<CompanyCreditInfo0Vo> batchContent = content.subList(start, end);
                // 每次处理一个批次的文件
                for (CompanyCreditInfo0Vo creditInfo : batchContent) {
                    String fileId = creditInfo.getFileId();
                    if (fileId != null && !fileId.isEmpty()) {
                        String[] fileIds = fileId.split(",");
                        String firstFileId = fileIds[0].trim();  // 获取第一个文件ID并去除空格
                        String fileUrl = fileServerUrl + "/view/download/" + firstFileId;
                        completableFutureList.add(CompletableFuture.runAsync(() -> {
                            byte[] fileBytes = downloadFile(fileUrl);  // 下载文件
                            if (fileBytes != null) {
                                try {
                                    FileSearchVo fileSearchVo = new FileSearchVo();
                                    fileSearchVo.setFileIds(firstFileId);
                                    List<SysFile> sysFiles = fileRemote.loadFiles(fileSearchVo);
                                    if (CollectionUtils.isNotEmpty(sysFiles)) {
                                        SysFile sysFile = sysFiles.get(0);
                                        String extension = FilenameUtils.getExtension(sysFile.getFilename());
                                        // 将文件保存到临时目录
                                        File tempFile = new File(tempDir, creditInfo.getCompanyName().trim() + "." + extension);
                                        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                                            fileOutputStream.write(fileBytes);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, executorService));
                    }
                }

                // 等待当前批次文件下载完成后再进行压缩
                CompletableFuture<Void> waitFuture = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));
                waitFuture.join();
                // 清空 completableFutureList 以防止内存泄漏
                completableFutureList.clear();
            }

            // 生成Excel文件并将其添加到zip包
            byte[] excelBytes = generateExcel(content);  // 生成Excel文件
            // 将文件保存到临时目录
            File tempFile = new File(tempDir, "人保客户.xlsx");
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                fileOutputStream.write(excelBytes);
                logger.info("人保授信导出xlxs成功==");
            } catch (IOException e) {
                logger.error("人保授信导出Excel写入失败", e);
            }
            // 完成压缩文件的写入
            String zipFileName = "人保客户" + RandomUtil.randomNumbers(5) + ".zip";
            File zipFile = new File(zipFileDirectory, zipFileName);
            zipDirectory(tempDir, zipFile);
            String path = searchVo.getRequestUrl() + "/download/" + zipFileName;
            approveWaitDealService.addCompanyCreditFileDeal(searchVo, DateOperator.formatDate(queryDate, true), path);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("人保授信导出异常", e.getMessage());
        }
        logger.info("人保授信导出结束==");
    }
    /**
     * 压缩整个目录
     */
    private void zipDirectory(File sourceDir, File zipFile) {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            File[] files = sourceDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zipOut.write(buffer, 0, len);
                        }
                        zipOut.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            logger.error("人保授信导出异常 ZIP 文件创建失败", e);
        }
    }
    // 下载文件方法
    public byte[] downloadFile(String fileUrl) {
        try (InputStream inputStream = new URL(fileUrl).openStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // 读取流并写入byteArrayOutputStream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 生成Excel文件
    public byte[] generateExcel(List<CompanyCreditInfo0Vo> content) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("人保授信");
        String[] headers = {"公司名称", "法人","成立日期", "注册资本", "公司地址", "人保额度", "最近合作日期"};
        Row headerRow = sheet.createRow(0);
        // 创建表头
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        // 填充数据
        for (int i = 0; i < content.size(); i++) {
            CompanyCreditInfo0Vo creditInfo = content.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(creditInfo.getCompanyName()==null?"":creditInfo.getCompanyName());
            row.createCell(1).setCellValue(creditInfo.getLegalRepresent()==null?"":creditInfo.getLegalRepresent());
            row.createCell(2).setCellValue(creditInfo.getStartDate()==null?"":creditInfo.getStartDate());
            row.createCell(3).setCellValue(creditInfo.getRegisterCapital()==null?"":creditInfo.getRegisterCapital());
            row.createCell(4).setCellValue(creditInfo.getAddress()==null?"":creditInfo.getAddress());
            row.createCell(5).setCellValue(creditInfo.getCreditAmount()==null?"":creditInfo.getCreditAmount().toString());
            row.createCell(6).setCellValue(creditInfo.getContractTime()==null?"":creditInfo.getContractTime());
        }
        // 设置表格默认列宽度为 15 个字节
        sheet.setDefaultColumnWidth(15);
        // 将Excel内容写入到ByteArrayOutputStream
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();  // 返回Excel文件的字节数组
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
