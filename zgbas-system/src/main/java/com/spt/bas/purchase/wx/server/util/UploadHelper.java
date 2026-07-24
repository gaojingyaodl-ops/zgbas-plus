package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.util.StrUtil;
import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileUploadBase64Request.Base64DataVo;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.payload.UploadBase64Request;
import com.spt.bas.purchase.wx.server.vo.UploadFileVo;
import com.spt.tools.core.prop.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 *  上传Helper
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-20 14:20
 */
@Component
public class UploadHelper {
    private static Logger logger = LoggerFactory.getLogger(UploadHelper.class);

    @Autowired
    private FileRemote fileRemote;

    @Autowired
    private IFileRecordClient fileRecordClient;

    /**
     * 上传
     * @param fileRequest
     * @param req
     */
    public FileRespVo uploadFile(FileUploadBase64Request fileRequest, HttpServletRequest req) {
        try {
            String[] allTypes = { ".jpg", ".gif", ".png", ".bmp",".pdf",".docx",".doc"  };
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("bas/");
            fileRequest.setServerName(req.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            //将上传的文件转为base64
            List<Base64DataVo> dataList = fileCoverBase64(req);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo=  fileRemote.uploadBase64(fileRequest);
            return fileRespVo;
        } catch (Exception e) {
            logger.error("uploadFile error!", e);
            throw new BaseException(Status.ERROR, "文件上传失败");
        }
    }

    /**
     * 上传base64文件
     * @param uploadBase64Request
     * @param request
     * @return
     */
    public UploadFileVo uploadBase64(UploadBase64Request uploadBase64Request, HttpServletRequest request) {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            FileUploadBase64Request fileRequest = new FileUploadBase64Request();
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("bas/");
            fileRequest.setServerName(request.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            //将上传的文件转为base64
            Base64DataVo base64DataVo = new Base64DataVo();
            base64DataVo.setFileName(uploadBase64Request.getFileName());
            base64DataVo.setBase64Data(uploadBase64Request.getBase64Data());
            List<Base64DataVo> dataList = new ArrayList<>();
            dataList.add(base64DataVo);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            if (fileRespVo == null) {
                throw new BaseException(Status.ERROR, "文件上传失败");
            }
            if (fileRespVo.getResult()) {
                if (!StrUtil.isEmpty(fileRespVo.getFileId())) {
                    String[] fileId = fileRespVo.getFileId().split(",");
                    try {
                        saveFileRecord(fileId[0], dataList);
                    } catch (Exception e) {
                        logger.error("上传附件,保存fileRecord失败", e);
                    }
                    return UploadFileVo.builder().fileId(fileId[0]).build();
                }
            }
            throw new BaseException(Status.ERROR, "文件上传失败");
        } catch (Exception e) {
            logger.error("uploadFile error!", e);
            throw new BaseException(Status.ERROR, "文件上传失败");
        }
    }

    /**
     * 附件上传，保存fileRecord
     *
     * @param fileId
     * @param dataList
     */
    private void saveFileRecord(String fileId,List<Base64DataVo> dataList) {
        FileRecord fileRecord = new FileRecord();
        if (StringUtils.isEmpty(fileId)) {
            return;
        }
        // 去除","
        if (fileId.indexOf(",") > 0) {
            fileId = fileId.split(",")[0];
        }
        fileRecord.setFileId(fileId);
        fileRecord.setFileName(dataList.get(0).getFileName());
        fileRecordClient.save(fileRecord);
    }

    /**
     * 图片转base64
     * @param request
     * @return
     */
    public List<Base64DataVo> fileCoverBase64(HttpServletRequest request){
        List<Base64DataVo> dataList =new ArrayList<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        for (Iterator<String> it = multipartRequest.getFileNames(); it.hasNext();) {
            String fileName = it.next();

            List<MultipartFile> lstFiles = multipartRequest.getFiles(fileName);
            for (MultipartFile file : lstFiles) {
                Base64DataVo dataVo = new Base64DataVo();
                try {
                    dataVo.setFileName(file.getOriginalFilename());
                    dataVo.setBase64Data(Base64Utility.base64Encode(file.getBytes()));
                    dataList.add(dataVo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }


}
