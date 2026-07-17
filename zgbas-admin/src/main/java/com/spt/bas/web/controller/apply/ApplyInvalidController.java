package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInvalid;
import com.spt.bas.client.remote.IApplyInvalidClient;
import com.spt.bas.client.vo.ApplyInvalidApproveVo;
import com.spt.bas.client.vo.ApplyInvalidDetailVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 作废申请
 */
@Controller
@RequestMapping(value = "/apply/invalid")
public class ApplyInvalidController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private IApplyInvalidClient applyInvalidClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyInvalid entity = getEntity(id);
        model.addAttribute("entity", entity);
        // 质量标准
        model.addAttribute("qualityStandardJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 交货方式
        model.addAttribute("deliveryTypeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
        //包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
        model.addAttribute("invalidTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_INVALID_TYPE)));
        return "apply/apply-invalid";
    }

    @ModelAttribute("preload")
    public ApplyInvalid getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyInvalid entity = new ApplyInvalid();
        if (id != null && id > 0) {
            return applyInvalidClient.getEntity(id);
        }
        entity.setId(0L);
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        return entity;
    }

    /**
     * 更改附件ID
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyInvalidClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    /**
     * 查询作废明细
     *
     * @param contractTailNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryInvalidDetail", method = RequestMethod.POST)
    public ApplyInvalidDetailVo queryInvalidDetail(@RequestParam("contractTailNo") String contractTailNo) {
        if (StringUtils.isNotBlank(contractTailNo)) {
            ApplyInvalid invalid = new ApplyInvalid();
            invalid.setContractTailNo(contractTailNo);
            invalid.setEnterpriseId(ShiroUtil.getEnterpriseId());
            return applyInvalidClient.queryInvalidDetail(invalid);
        }
        return null;
    }

    /**
     * 查询可作废的审批单列表
     *
     * @param budgetApproveId
     * @param invalidType
     * @param response
     */
    @RequestMapping({"findCanInvalidList"})
    public void findCanInvalidList(@RequestParam(value = "budgetApproveId", required = false) Long budgetApproveId,
                                   @RequestParam(value = "invalidType", required = false) String invalidType,
                                   @RequestParam(value = "invalidApproveIds", required = false) String invalidApproveIds,
                                   @RequestParam(value = "contractTailNo", required = false) String contractTailNo, HttpServletResponse response) {
        ApplyInvalid invalid = new ApplyInvalid();
        invalid.setBudgetApproveId(budgetApproveId);
        invalid.setInvalidType(invalidType);
        invalid.setInvalidApproveIds(invalidApproveIds);
        invalid.setEnterpriseId(ShiroUtil.getEnterpriseId());
        invalid.setContractTailNo(contractTailNo);
        List<ApplyInvalidApproveVo> resultList = applyInvalidClient.queryInvalidApproveList(invalid);
        JsonEasyUI.renderListJson(response, resultList);
    }
}
