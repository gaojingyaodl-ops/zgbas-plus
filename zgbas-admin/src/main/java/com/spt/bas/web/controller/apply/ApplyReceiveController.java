package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.remote.IApplyReceiveClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/apply/receive")
public class ApplyReceiveController extends PageController<ApplyReceive, BaseVo> {
    @Autowired
    private IApplyReceiveClient applyReceiveClient;
    @Autowired
    private ICtrContractClient contractClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private ICtrContractClient ctrContractClient;

    @Override
    public BaseClient<ApplyReceive> getService() {
        return applyReceiveClient;
    }


    /**
     * 审批模板内容
     */
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) throws WebApplicationException {
        String contractId = request.getParameter("contractId");
        ApplyReceive entity = getEntity(id);
        model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        List<BsDictData> lstPayType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BsDictConstants.DICT_TYPE_RECEIVETYPE);
        String receiveTypeJson;
        if (lstPayType.isEmpty()) {
            //收款类型
            receiveTypeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.RECEIVE_TYPE));
        } else {
            receiveTypeJson = JsonUtil.obj2Json(lstPayType);
        }

        model.addAttribute("receiveTypeJson", receiveTypeJson);
        //收款方式
        model.addAttribute("receiveModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_MODE_APPLYRECEIVE)));
        // 贴息费用承担方
        model.addAttribute("discountTargetJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_DISCOUNT_TARGET)));
        model.addAttribute("entity", entity);
        //我方抬头
        model.addAttribute("ourCompanyJson",
                JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        Boolean isFromTP = false;
        if (StringUtils.isNotBlank(contractId) && NumberUtil.isNumber(contractId)) {
            CtrContract contract = ctrContractClient.getEntity(Long.valueOf(contractId));
            if (Objects.nonNull(contract) && StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                isFromTP = true;
            }
        } else {
            if (entity.getContractId() != null) {
                CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
                if (Objects.nonNull(contract) && StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                    isFromTP = true;
                }
            } else {
                List<ApplyReceive> receiveDetailList = entity.getReceiveDetailList();
                if (CollectionUtils.isNotEmpty(receiveDetailList)) {
                    Long firstContractId = receiveDetailList.stream()
                            .map(ApplyReceive::getContractId)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                    if (firstContractId != null) {
                        CtrContract contract = ctrContractClient.getEntity(firstContractId);
                        if (Objects.nonNull(contract) && StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, contract.getBusinessType())) {
                            isFromTP = true;
                        }
                    }
                }
            }
        }
        if (isFromTP) {
            return "apply/receive-content-tp";
        } else {
            return "apply/receive-content2";
        }
    }

    @ModelAttribute("preload")
    public ApplyReceive getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                ApplyReceive entity = getService().getEntity(id);
                List<ApplyReceive> receiveDetailList = entity.getReceiveDetailList();
                if (CollectionUtils.isNotEmpty(receiveDetailList)) {
                    entity.setReceiveDetailListStr(JsonUtil.obj2Json(entity.getReceiveDetailList()));
                    Long[] contractIds = receiveDetailList.stream().filter(r -> Objects.nonNull(r.getContractId())).mapToLong(ApplyReceive::getContractId).mapToObj(Long::valueOf).toArray(Long[]::new);
                    List<CtrContract> contractList = contractClient.findContractByIds(contractIds);
                    if (CollectionUtils.isNotEmpty(contractList)) {
                        entity.setContractDetailList(contractList);
                        entity.setContractDetailListStr(JsonUtil.obj2Json(contractList));
                    }
                }
                return entity;
            } else {
                ApplyReceive entity = new ApplyReceive();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                entity.setReceiveDate(new Date());

                List<ApplyReceive> receiveDetailList = new ArrayList<>();
                receiveDetailList.add(new ApplyReceive());
                entity.setReceiveDetailList(receiveDetailList);
                return entity;
            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "queryDiscountContractList", method = RequestMethod.POST)
    public List<ApplyReceive> queryDiscountContractList(@RequestBody CtrContract contract) {
        if (StringUtils.isNotBlank(contract.getOurCompanyName()) && StringUtils.isNotBlank(contract.getCompanyName())) {
            return contractClient.findDiscountContractList(contract);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "queryTpDiscountContractList", method = RequestMethod.POST)
    public List<ApplyReceive> queryTpDiscountContractList(@RequestBody CtrContract contract) {
        if (StringUtils.isNotBlank(contract.getOurCompanyName()) && StringUtils.isNotBlank(contract.getCompanyName())) {
            return contractClient.findTpDiscountContractList(contract);
        }
        return null;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            applyReceiveClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "listVo")
    public void listVo(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        Page<ApplyReceive> page = findPage(searchVo, request, response);
        Map<String, Object> searchParams = searchVo.getSearchParams();
        searchParams.put("EQS_status", BasConstants.APPROVE_STATUS_D);
        searchVo.setSearchParams(searchParams);
        ApplyReceive sum = applyReceiveClient.findPageSum(searchVo);
        Map<String, Object> footer = new HashMap<>();
        footer.put("receiveDate", "合计");
        footer.put("receiveAmount", sum.getReceiveAmount());
        JsonEasyUI.renderJson(response, page, footer);
    }

    @RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
    public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Map<String, Object> map = searchVo.getSearchParams();
        map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        Page<ApplyCancelDetail> page = applyReceiveClient.findPageDetail(searchVo);

        JsonEasyUI.renderJson(response, page);
    }
}
