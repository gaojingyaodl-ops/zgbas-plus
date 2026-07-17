package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.vo.ApplyCancel2Vo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/apply/cancel2")
public class ApplyCancel2Controller {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IPmApproveContentsClient approveContentsClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        PmApproveContents entity = getEntity(id);
        ApplyCancel2Vo applyCancel2Vo;
        if (!StringUtils.isEmpty(entity.getContents())) {
            applyCancel2Vo = JsonUtil.json2Object(ApplyCancel2Vo.class, entity.getContents());
        }else{
            applyCancel2Vo = new ApplyCancel2Vo();
        }
        String processCode = request.getParameter("processCode");
        String contractId = request.getParameter("contractId");
        // 新建
        if (id != null && id == 0L) {
            if (!StringUtils.isEmpty(contractId)) {
                List<CtrContract> contracts = ctrContractClient.findContractsByContractId(Long.parseLong(contractId));
                for (CtrContract contract : contracts) {
                    // 新建赋值
                    // 销售
                    applyCancel2Vo.setId(0L);
                    if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
                        applyCancel2Vo.setOurCompanyName(contract.getOurCompanyName());
                        applyCancel2Vo.setBuyCompanyName(contract.getCompanyName());
                        applyCancel2Vo.setBuyContractNo(contract.getContractNo());
                        applyCancel2Vo.setBuyMatchUserName(contract.getMatchUserName());
                    } else if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
                        applyCancel2Vo.setSellCompanyName(contract.getCompanyName());
                        applyCancel2Vo.setSellContractNo(contract.getContractNo());
                        applyCancel2Vo.setSellMatchUserName(contract.getMatchUserName());
                    }
                }
            }
        }
        model.addAttribute("processCode", processCode);
        model.addAttribute("entity", applyCancel2Vo);

        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        return "apply/cancel2-content";
    }

    @ModelAttribute("preload")
    public PmApproveContents getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                PmApproveContents entity1 = approveContentsClient.getEntity(id);
                return entity1;
            }else {
                PmApproveContents entity1 = new PmApproveContents();
                entity1.setApproveId(0L);
                return entity1;

            }
        }
        return null;
    }

    /**
     * 更改附件ID
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            approveContentsClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
}
