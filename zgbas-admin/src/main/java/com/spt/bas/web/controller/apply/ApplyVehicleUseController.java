package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.VehicleUse;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.remote.IVehicleUseClient;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.VehicleUseVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
/**
 * 车辆使用申请单
 */
@Controller
@RequestMapping(value = "/apply/vehicleUse")
public class ApplyVehicleUseController extends PageController<VehicleUse,BaseVo> {
    @Autowired
    private IVehicleUseClient vehicleUseClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Resource
    private WebParamUtils webParamUtils;


    @Override
    public BaseClient<VehicleUse> getService() {
        return vehicleUseClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        // 申请人
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
        // 车牌号
        model.addAttribute("plateNumberJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DICT_APPLY_VEHICLE_USE)));
        //所在地点
        model.addAttribute("vehicleLocations",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DICT_APPLY_VEHICLE_LOCATION)));
        // 车辆使用状态
        return "vehicle/vehicle_use";
    }

    @RequestMapping(value = "/content/{id}" , method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        VehicleUse entity = getEntity(id, processCode);

        // 车牌号
        model.addAttribute("plateNumberJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DICT_APPLY_VEHICLE_USE)));
        //所在地点
        model.addAttribute("vehicleLocations",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.DICT_APPLY_VEHICLE_LOCATION)));
        //当前登录用户id
        model.addAttribute("applyUserIdJson",JsonUtil.obj2Json(ShiroUtil.getCurrentUserId()));

        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/vehicle-use";
    }

    @ModelAttribute("preload")
    public VehicleUse getEntity(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "processCode", required = false) String processCode) {
        VehicleUse enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (VehicleUse) ProcessControlUtil.getEntity(id, processCode);
                if (enObject != null) {
                    enObject.setId(id);
                }
                return enObject;
            } else {
                VehicleUse vehicleUse = new VehicleUse();
                vehicleUse.setApplyDate(new Date());
                vehicleUse.setDepartDate(new Date());
                vehicleUse.setApplyUserName(ShiroUtil.getCurrentUserName());
                vehicleUse.setApplyUserId(ShiroUtil.getCurrentUserId());
                vehicleUse.setId(0L);
                return vehicleUse;
            }
        }
        return enObject;
    }

    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }

    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            pmApproveContentsClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
    @RequestMapping(value = "findVehiclePage")
    public void findVehiclePage(VehicleUseVo vehicleUseVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(vehicleUseVo, request);
        Page<VehicleUse> page = vehicleUseClient.findVehiclePage(vehicleUseVo);
        JsonEasyUI.renderJson(response, page);
    }
}
