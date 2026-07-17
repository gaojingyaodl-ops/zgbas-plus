package com.spt.bas.web.controller.bs;

import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.client.remote.IFileTypeClient;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  附件操作controller
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-02 15:46
 */
@Controller
@RequestMapping(value = "/bs/file")
public class BsFileController {
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;
    @Autowired
    private IFileRecordClient fileRecordClient;
    @Autowired
    private IFileTypeClient fileTypeClient;

    /**
     * 查询审批单附件选项
     * @param processCode
     * @param request
     * @param response
     */
    @RequestMapping(value = "listFileType/{processCode}")
    public void listFileType(@PathVariable("processCode") String processCode, HttpServletRequest request,
                              HttpServletResponse response) {
        List<FileProcessRel> list = fileProcessRelClient.findList(processCode);
        RenderUtil.renderJson(list, response);
    }

}
