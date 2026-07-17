/**
 *
 */
package com.spt.bas.web.controller;

import com.hsoft.file.sdk.constant.FileConstant;
import com.hsoft.file.sdk.entity.SysFile;
import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.file.sdk.util.Base64Utility;
import com.hsoft.file.sdk.vo.FileRespVo;
import com.hsoft.file.sdk.vo.FileSearchVo;
import com.hsoft.file.sdk.vo.FileUploadBase64Request;
import com.hsoft.file.sdk.vo.FileUploadBase64Request.Base64DataVo;
import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/file")
public class FileController {
    private final Log logger = LogFactory.getLog(getClass());
    @Autowired
    private FileRemote fileRemote;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IFileRecordClient fileRecordClient;

    /**
     * 上传附件
     * @param fileRequest
     * @param req
     * @param resp
     * @throws Exception
     */
    @RequestMapping("/uploadFile")
    public void uploadFile(FileUploadBase64Request fileRequest, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("bas/");
            fileRequest.setServerName(req.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            //将上传的文件转为base64
            List<Base64DataVo> dataList = fileCoverBase64(req);
            fileRequest.setDataList(dataList);
			fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            try {
                saveFileRecord(fileRespVo, dataList);
            } catch (Exception e) {
                logger.error("上传附件,保存fileRecord失败", e);
            }

            RenderUtil.renderJson(JsonUtil.obj2Json(fileRespVo), resp);
        } catch (Exception e) {
            RenderUtil.renderFailure("上传失败", resp);
            logger.error("uploadFile error!", e);
        }

    }

    /**
     * 附件上传，保存fileRecord
     *
     * @param fileRespVo
     * @param dataList
     */
    private void saveFileRecord(FileRespVo fileRespVo, List<Base64DataVo> dataList) {
        FileRecord fileRecord = new FileRecord();
        String fileId = fileRespVo.getFileId();
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

    private List<Base64DataVo> fileCoverBase64(HttpServletRequest request) {
        List<Base64DataVo> dataList = new ArrayList<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        for (Iterator<String> it = multipartRequest.getFileNames(); it.hasNext(); ) {
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

    /**富文本编辑框 文件上传 */
    @RequestMapping("/uploadConfigFile")
    public void uploadConfigFile(Model model, FileUploadBase64Request fileRequest, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("config/");
            fileRequest.setServerName(req.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            List<Base64DataVo> dataList = fileCoverBase64(req);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            String fileId = fileRespVo.getFileId();
            String filePath = PropertiesUtil.getProperty(FileConstant.FILE_PATH_KEY);
            String url = filePath + "/view/show/" + fileId;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", "SUCCESS");
            map.put("url", url);
            map.put("title", fileId);
            map.put("original", "图片上传");
            RenderUtil.renderJson(map, resp);
        } catch (Exception e) {
            logger.error("uploadConfigFile error!", e);
        }

    }


    @RequestMapping(value = "/delete")
    public void delete(HttpServletResponse resp, Long fileId, String domId, Long bizId) {
        try {
            FileRespVo fileRespVo = fileRemote.delete(fileId);
            try {
                fileRecordClient.deleteByFileId(fileRespVo.getFileId());
                dealWithBusinessLogic(fileId, domId, bizId);
            } catch (Exception e) {
                logger.error("删除附件,删除fileRecord失败", e);
            }
            RenderUtil.renderSuccess(fileRespVo.getFileId(), resp);
        } catch (Exception e) {
            logger.error("delete error!", e);
        }
    }

    /**上传基础格式文件demo */
    @RequestMapping("/uploadbaseFile")
    public void uploadbaseFile(Model model, FileUploadBase64Request fileRequest, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("user");
            fileRequest.setServerName(req.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            fileRequest.setBizFieldName("img_file_id");
            fileRequest.setBizTableName("t_sys_user");
            fileRequest.setBizId(4L);
            //将上传的文件转为base64
            List<Base64DataVo> dataList = fileCoverBase64(req);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            String fileId = fileRespVo.getFileId();
            String filePath = PropertiesUtil.getProperty(FileConstant.FILE_PATH_KEY);
            String url = filePath + "/view/show/" + fileId;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", "SUCCESS");
            map.put("url", url);
            map.put("title", fileId);
            map.put("original", "图片上传");
            RenderUtil.renderJson(map, resp);
        } catch (Exception e) {
            logger.error("uploadConfigFile error!", e);
        }

    }

    /**上传base64格式文件 demo*/
    @RequestMapping("/uploadbase64File")
    public void uploadbase64File(Model model, FileUploadBase64Request fileRequest, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String[] allTypes = {".jpg", ".gif", ".png", ".bmp", ".pdf", ".docx", ".doc"};
            fileRequest.setAllowTypes(allTypes);
            fileRequest.setFilePath("user");
            fileRequest.setServerName(req.getServerName());
            fileRequest.setAppCode(PropertiesUtil.getProperty(FileConstant.FILE_BUCKET));
            fileRequest.setBizFieldName("img_file_id");
            fileRequest.setBizTableName("t_sys_user");
            fileRequest.setBizId(2l);
            List<Base64DataVo> dataList = new ArrayList<Base64DataVo>();
            Base64DataVo dataVo = new Base64DataVo();
            dataVo.setBase64Data("base64字符串");
            dataVo.setFileName("文件名");
            dataList.add(dataVo);
            fileRequest.setDataList(dataList);
            FileRespVo fileRespVo = fileRemote.uploadBase64(fileRequest);
            String fileId = fileRespVo.getFileId();
            String filePath = PropertiesUtil.getProperty(FileConstant.FILE_PATH_KEY);
            String url = filePath + "/view/show/" + fileId;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("state", "SUCCESS");
            map.put("url", url);
            map.put("title", fileId);
            map.put("original", "图片上传");
            RenderUtil.renderJson(map, resp);
        } catch (Exception e) {
            logger.error("uploadConfigFile error!", e);
        }

    }

    @RequestMapping("/loadFiles")
    public String loadFiles(Model model, HttpServletRequest req, FileSearchVo vo) {
        List<SysFile> list = fileRemote.loadFiles(vo);
        model.addAttribute("list", list);
        String canEditFile = req.getParameter("hasEditFile");
        String url = req.getParameter("url");
        String domId = req.getParameter("domId");
        boolean canEdit = BooleanUtils.toBoolean(canEditFile);
        model.addAttribute("hasEditFile", canEdit);
        model.addAttribute("url", url);
        model.addAttribute("domId", domId);
        model.addAttribute("bizId", vo.getBizId());
        return "file/file-show";
    }

    private void dealWithBusinessLogic(Long fileId, String domId, Long bizId) {
        if (Objects.nonNull(bizId) && bizId != 0L) {
            if (StringUtils.equals("debtCertificateFileId", domId)) {
                ctrContractClient.refreshFactorStatus(bizId);
            }
        }
    }
}
