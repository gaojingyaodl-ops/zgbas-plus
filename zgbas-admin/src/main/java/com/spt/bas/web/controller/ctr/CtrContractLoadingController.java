package com.spt.bas.web.controller.ctr;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractLoading;
import com.spt.bas.client.entity.CtrContractLoadingDetail;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.ICtrContractLoadingClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.XssExcelExp;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.vo.PmApproveSearchVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.BatchSaveVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 提货单
 *
 * @Author: gaojy
 * @create 2022/3/16 9:35
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping(value = "/ctr/loading")
public class CtrContractLoadingController extends PageController<CtrContractLoading, BaseVo> {

    @Autowired
    private ICtrContractLoadingClient ctrContractLoadingClient;
    @Autowired
    private ICtrContractClient ctrContractClient;

    @Value("${file.show.url}")
    private String fileShowUrl;

    @Override
    public BaseClient<CtrContractLoading> getService() {
        return ctrContractLoadingClient;
    }

    /**
     * 提货单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "")
    public String findFactory(Model model) {
        return "ctr/contractLoadingList";
    }

    /**
     * 配送单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "delivery")
    public String delivery(Model model) {
        return "ctr/contractDeliveryList";
    }

    /**
     * 创建提货单记录
     */
    @RequestMapping(value = "createLoading", method = RequestMethod.POST)
    public void createLoading(CtrContractLoading load, HttpServletRequest request, HttpServletResponse response) {
        try {
            String type = request.getParameter("type");
            logger.info("用户:{},创建提货/配送单:{}", ShiroUtil.getCurrentUserName(), JsonUtil.obj2Json(load));
            if (StringUtils.isBlank(load.getBillType()) || StringUtils.isBlank(load.getOurCompanyName())) {
                RenderUtil.renderFailure("提交信息有误，请刷新页面重新填写提交!", response);
                return;
            }
            BatchSaveVo<CtrContractLoadingDetail> loadingDetails = getLoadingDetails(request);
            if ((Objects.isNull(load.getId()) || load.getId() == 0L)
                    && CollectionUtils.isEmpty(loadingDetails.getInsertedRecords())
                    && CollectionUtils.isEmpty(loadingDetails.getUpdatedRecords())
                    && CollectionUtils.isEmpty(loadingDetails.getDeletedRecords())) {
                RenderUtil.renderFailure("提交信息有误，请刷新页面重新填写提交!", response);
                return;
            }
            load.setEnableFlg(true);
            load.setEnterpriseId(ShiroUtil.getEnterpriseId());
            CtrContractLoading loading = ctrContractLoadingClient.save(load);
            ctrContractLoadingClient.saveLoadingDetails(loadingDetails, loading.getId(), StringUtils.equals("copy", type));
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("createLoading:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    private BatchSaveVo<CtrContractLoadingDetail> getLoadingDetails(HttpServletRequest request) {
        BatchSaveVo<CtrContractLoadingDetail> batchSaveVo = new BatchSaveVo<>();
        try {
            List<CtrContractLoadingDetail> lstDeleted = JsonEasyUI.getDeletedRecords(CtrContractLoadingDetail.class, request);
            List<CtrContractLoadingDetail> lstInsert = JsonEasyUI.getInsertRecords(CtrContractLoadingDetail.class, request);
            List<CtrContractLoadingDetail> lstUpdated = JsonEasyUI.getUpdatedRecords(CtrContractLoadingDetail.class, request);
            batchSaveVo.setDeletedRecords(lstDeleted);
            batchSaveVo.setInsertedRecords(lstInsert);
            batchSaveVo.setUpdatedRecords(lstUpdated);
            logger.info("用户:{},操作数据:{}", ShiroUtil.getCurrentUserName(), JsonUtil.obj2Json(batchSaveVo));
            logger.info("JsonEasyUIData:{}", JsonUtil.obj2Json(request.getParameter(JsonEasyUI.JSON_NAME)));
        } catch (Exception e) {
            logger.error("getLoadingDetails save error!", e);
        }
        return batchSaveVo;
    }

    @RequestMapping(value = "deleteLoadingDetail/{id}")
    public String deleteLoadingDetail(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            ctrContractLoadingClient.deleteLoadingDetail(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception var5) {
            this.logger.error("delete record error!", var5);
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 克隆提货单
     */
    @RequestMapping(value = "copyBill/{loadId}", method = RequestMethod.GET)
    public void copyBill(@PathVariable("loadId") Long loadId, HttpServletResponse response) {
        try {
            CtrContractLoading entity = ctrContractLoadingClient.getEntity(loadId);
            if (Objects.nonNull(entity)) {
                entity.setId(0L);
                entity.setShortUrl(null);
                entity.setFileId(null);
                entity.setSignDate(null);
                CtrContractLoading loading = ctrContractLoadingClient.save(entity);
                BatchSaveVo<CtrContractLoadingDetail> batchSaveVo = new BatchSaveVo<>();
                batchSaveVo.setInsertedRecords(entity.getLoadingDetails());
                ctrContractLoadingClient.saveLoadingDetails(batchSaveVo, loading.getId(), true);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("copyLoading:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    /**
     * 生成电子签合同
     */
    @RequestMapping(value = "axqLoadingBill/{loadId}", method = RequestMethod.GET)
    public void axqLoadingBill(@PathVariable("loadId") Long loadId, HttpServletResponse response) {
        try {
            CtrContractLoading loading = ctrContractLoadingClient.axqLoadingBill(loadId);
            if (StringUtils.isBlank(loading.getShortUrl())) {
                RenderUtil.renderFailure("生成提货单电子合同失败", response);
            } else {
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error("axqLoadingBill:", e);
            String msg = "";
            if (e.getCause() != null) {
                JSONObject jsonObject = JSONObject.parseObject(e.getCause().getMessage());
                msg = jsonObject.getString("message");
            }
            RenderUtil.renderFailure(msg, response);
        }
    }

    /**
     * 刷新提货单状态
     */
    @RequestMapping(value = "refreshLoadingBillStatus/{loadId}", method = RequestMethod.GET)
    public void refreshLoadingBillStatus(@PathVariable("loadId") Long loadId, HttpServletResponse response) {
        try {
            ctrContractLoadingClient.refreshLoadingBillStatus(loadId);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("refreshLoadingBillStatus:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "findSealUsageApproveList")
    public void findSealUsageApproveList(HttpServletRequest request, HttpServletResponse response) {
        List<PmApprove> resultList = new ArrayList<>();
        String searchKey = request.getParameter("q");
        if (StringUtils.isNotBlank(searchKey)) {
            PmApproveSearchVo searchVo = new PmApproveSearchVo(searchKey, ShiroUtil.getEnterpriseId());
            resultList = ctrContractLoadingClient.findSealUsageApprove(searchVo);
        }
        RenderUtil.renderJson(resultList, response);
    }

    /**
     * 导出Excel提货单
     *
     * @param response
     * @param request
     * @throws ApplicationException
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> paramMap = new HashMap<>();
        Long loadingId = new Long(request.getParameter("loadingId"));
        CtrContractLoading entity = ctrContractLoadingClient.getEntity(loadingId);
        if (Objects.nonNull(entity)) {
            paramMap.put("ourCompanyName", entity.getOurCompanyName());
            paramMap.put("companyName", entity.getCompanyName());
            String contractNo = entity.getContractNo();
            paramMap.put("contractNo", contractNo);
            paramMap.put("productName", entity.getProductName());
            paramMap.put("factoryName", entity.getFactoryName());
            paramMap.put("brandNumber", entity.getBrandNumber());
            paramMap.put("dealNumber", NumberUtil.formatNumber(entity.getDealNumber(), "#.####"));
            paramMap.put("numberUnit", entity.getNumberUnit());
            paramMap.put("plateNumber", entity.getPlateNumber());
            paramMap.put("driverName", entity.getDriverName());
            paramMap.put("driverCardNo", entity.getDriverCardNo());
            paramMap.put("driverPhone", entity.getDriverPhone());
            paramMap.put("loadingDate", DateUtil.format(entity.getLoadingDate(), DatePattern.CHINESE_DATE_PATTERN));
            paramMap.put("remark", entity.getRemark());
            XssExcelExp.excelLoading("/excel/loadBill_v3.0.xlsx", paramMap, entity.getLoadingDetails(), response, "提货单(" + entity.getContractNo() + ").xlsx");
        }
    }

    /**
     * 导出Excel配送单
     *
     * @param response
     * @param request
     * @throws ApplicationException
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping("exportDeliveryExcel")
    public void exportDeliveryExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> paramMap = new HashMap<>();
        Long loadingId = new Long(request.getParameter("loadingId"));
        CtrContractLoading entity = ctrContractLoadingClient.getEntity(loadingId);
        if (Objects.nonNull(entity)) {
            paramMap.put("ourCompanyName", entity.getOurCompanyName());
            paramMap.put("companyName", entity.getCompanyName());
            String contractNo = entity.getContractNo();
            paramMap.put("contractNo", contractNo);
            paramMap.put("productName", entity.getProductName());
            paramMap.put("factoryName", entity.getFactoryName());
            paramMap.put("brandNumber", entity.getBrandNumber());
            paramMap.put("dealNumber", NumberUtil.formatNumber(entity.getDealNumber(), "#.###"));
            paramMap.put("numberUnit", entity.getNumberUnit());
            paramMap.put("deliveryNo", entity.getDeliveryNo());
            paramMap.put("contactName", entity.getContactName());
            paramMap.put("contactPhone", entity.getContactPhone());
            paramMap.put("contactAddress", entity.getContactAddress());
            paramMap.put("loadingDate", DateUtil.format(entity.getLoadingDate(), DatePattern.CHINESE_DATE_PATTERN));
            paramMap.put("remark", entity.getRemark());
            XssExcelExp.excelDelivery("/excel/deliveryBill_v2.0.xlsx", paramMap, entity.getLoadingDetails(), response, "配送单(" + entity.getContractNo() + ").xlsx");
        }
    }

    /**
     * 导出Excel签收单
     *
     * @param response
     * @param request
     * @throws ApplicationException
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping("exportReceiptExcel")
    public void exportReceiptExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        Map<String, String> paramMap = new HashMap<>();
        Long loadingId = new Long(request.getParameter("loadingId"));
        CtrContractLoading entity = ctrContractLoadingClient.getEntity(loadingId);
        CtrContract contract = ctrContractClient.getEntity(entity.getContractId());
        if (Objects.nonNull(entity)) {
            paramMap.put("ourCompanyName", entity.getOurCompanyName());
            paramMap.put("companyName", entity.getCompanyName());
            String contractNo = entity.getContractNo();
            paramMap.put("contractNo", parseDefaultParam(contractNo));
            paramMap.put("productName", parseDefaultParam(entity.getProductName()));
            paramMap.put("factoryName", parseDefaultParam(entity.getFactoryName()));
            paramMap.put("brandNumber", parseDefaultParam(entity.getBrandNumber()));
            paramMap.put("dealNumber", parseDefaultParam(NumberUtil.formatNumber(entity.getDealNumber(), "#.###")));
            paramMap.put("numberUnit", parseDefaultParam(entity.getNumberUnit()));
            paramMap.put("plateNumber", parseDefaultParam(entity.getPlateNumber()));
            paramMap.put("driverName", parseDefaultParam(entity.getDriverName()));
            paramMap.put("driverCardNo", parseDefaultParam(entity.getDriverCardNo()));
            paramMap.put("loading", parseDefaultParam(DateUtil.format(entity.getLoadingDate(), DatePattern.CHINESE_DATE_PATTERN)));
            paramMap.put("remark", parseDefaultParam(entity.getRemark()));
            paramMap.put("matchUserName", parseDefaultParam("-"));
            paramMap.put("matchUserPhone", parseDefaultParam("-"));
            String contractAddrss = entity.getContactAddress();
            if (Objects.nonNull(contract)) {
                contractAddrss = String.format("%s%s", contract.getDeliveryAddr(), contract.getContactAddr());
            }
            paramMap.put("contactAddress", parseDefaultParam(contractAddrss));
            XssExcelExp.excelReceive("/excel/receiptBill_v2.0.xlsx", paramMap, entity.getLoadingDetails(), response, "签收单(" + entity.getContractNo() + ").xlsx");
        }
    }

    /**
     * 查询所有信息
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findAllContractLoading")
    public void findAllContractLoading(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<CtrContractLoading> page = ctrContractLoadingClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }


    @RequestMapping(value = "loadingDetails/{id}")
    public void listData(@PathVariable("id") Long id, HttpServletResponse response) {
        List<CtrContractLoadingDetail> detailList = new ArrayList<>();
        if (id != null && id > 0) {
            CtrContractLoading entity = ctrContractLoadingClient.getEntity(id);
            if (Objects.nonNull(entity) && CollectionUtils.isNotEmpty(entity.getLoadingDetails())) {
                detailList = entity.getLoadingDetails();
            }
        }
        JsonEasyUI.renderListJson(response, detailList);
    }

    /**
     * 获取合同附件下载地址
     *
     * @param fileId
     * @return
     */
    @RequestMapping(value = "getByFileId", method = RequestMethod.POST)
    @ResponseBody
    public String getByFileId(@RequestParam("fileId") Long fileId) {
        return fileShowUrl + "/view/show/" + fileId;
    }

    private String parseDefaultParam(String param) {
        return StringUtils.isNotBlank(param) ? param : "-";
    }
}
