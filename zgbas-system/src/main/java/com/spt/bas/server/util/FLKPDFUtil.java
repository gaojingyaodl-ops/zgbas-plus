package com.spt.bas.server.util;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.core.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author MoonLight
 * @Date 2024/10/25 15:16
 * @Version 1.0
 */
@Slf4j
public class FLKPDFUtil {

    public static String generateFLKFileId(Map<String, String> paramMap, String templatePath, String fileName) {
        ByteArrayOutputStream outputStream = generateFLKContractFile(paramMap, templatePath);
        if (outputStream == null) {
            return "";
        }
        return fileUploadBase64(outputStream, fileName);
    }


    private static String fileUploadBase64(ByteArrayOutputStream outputStream, String fileName) {
        FileUploadBase64Request fileRequest = new FileUploadBase64Request();
        fileRequest.setFilePath(BasConstants.APP_CODE + "/");
        fileRequest.setServerName(BasConstants.APP_CODE);
        fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
        List<FileUploadBase64Request.Base64DataVo> dataList = new ArrayList<>();
        FileUploadBase64Request.Base64DataVo dataVo = new FileUploadBase64Request.Base64DataVo();
        dataVo.setFileName(fileName + ".pdf");
        dataVo.setBase64Data(Base64Utility.base64Encode(outputStream.toByteArray()));
        dataList.add(dataVo);
        fileRequest.setDataList(dataList);
        FileRemote fileRemote = SpringContextHolder.getBean(FileRemote.class);
        FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
        if (Objects.isNull(fileRespVo) || StringUtils.isBlank(fileRespVo.getFileId())) {
            log.error("FLKPDFUtil fileUploadBase64 error!");
        }
        return fileRespVo.getFileId();
    }

    private static ByteArrayOutputStream generateFLKContractFile(Map<String, String> paramMap, String templatePath) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = null;
        PdfReader reader = null;

        try {
            // 1. 读取PDF模板
            InputStream templateStream = FLKPDFUtil.class.getClassLoader().getResourceAsStream(templatePath);
            if (templateStream == null) {
                log.error("FLKPDFUtil templateStream is null!");
                return null;
            }
            reader = new PdfReader(templateStream);

            // 2. 创建新的PDF输出文件流
            stamper = new PdfStamper(reader, outputStream);

            // 3. 获取PDF中的表单字段
            AcroFields form = stamper.getAcroFields();

            // 4. 设置字体（确保中文字符显示正确）
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(baseFont);

            // 5. 填充表单字段
            paramMap.forEach((k, v) -> {
                try {
                    form.setField(k, v);
                } catch (Exception e) {
                    throw new RuntimeException("Error setting field: " + k, e);
                }
            });

            // 6. 将表单内容锁定，防止再次编辑
            stamper.setFormFlattening(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // 返回包含 PDF 文件内容的输出流
        return outputStream;
    }
}
