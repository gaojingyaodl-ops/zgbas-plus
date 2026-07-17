package com.spt.bas.web.controller.evaluate;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateUser;
import com.spt.bas.client.remote.IEvaluateUserClient;
import com.spt.bas.client.vo.EvaluateSearchVo;
import com.spt.bas.client.vo.EvaluateStartVo;
import com.spt.bas.report.client.entity.RptEvaluateTotalSearch;
import com.spt.bas.report.client.remote.IRptEvaluateTotalClient;
import com.spt.bas.report.client.vo.RptEvaluateTotalVo;
import com.spt.bas.web.util.StringUtils;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/evaluate/total")
public class EvaluateTotalController{
    @Autowired
    private IRptEvaluateTotalClient evaluateTotalClient;
    @Autowired
    private IEvaluateUserClient evaluateUserClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content")
    public String content(Model model) {
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        model.addAttribute("evaluateDeptJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_DEPT)));
        return "evaluate/evaluate-total";   
    }
    /**
     * 查询所有信息
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findAllEvaluateTotalLoading")
    public void findAllEvaluateItemLoading(RptEvaluateTotalSearch searchVo, HttpServletRequest request, HttpServletResponse response) {
        PageDown<RptEvaluateTotalVo> page = evaluateTotalClient.findPageEvaluateTotal(searchVo);
        JsonEasyUI.renderJson(response, page);
    }
    /**
     * 跳转到详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "startShow/{id}", method = RequestMethod.GET)
    public String startShow(@PathVariable("id") Long id, Model model) {
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        model.addAttribute("evaluateDeptJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_DEPT)));
        EvaluateUser entity;
        model.addAttribute("id", id);
        entity = new EvaluateUser();
        entity.setId(0L);
        model.addAttribute("entity", entity);
        EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(false, true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(nodes.getChildren()));
        return "evaluate/evaluate-start";

    }
    
    /**
     * 添加方法
     * @param request
     * @param response
     */
    @RequestMapping("/startEvaluate")
    public void startEvaluate(EvaluateStartVo vo, HttpServletRequest request, HttpServletResponse response) {
        try{
            if(StringUtils.isBlank(vo.getEvaluateMonth())){
                RenderUtil.renderFailure("考评月份不能为空", response);
                return;
            }
            String[] deptIds = request.getParameterValues("deptId");
            if(deptIds == null || deptIds.length <= 0){
                RenderUtil.renderFailure("考评部门不能为空", response);
                return;
            }
            EvaluateSearchVo searchVo = new EvaluateSearchVo();
            searchVo.setEvaluateMonth(vo.getEvaluateMonth());
            List<Long> deptIdList = new ArrayList<>();
            for (String str:deptIds) {
                Long deptId = Long.valueOf(str.replaceAll("dept",""));
                SysDeptSdk dept = webParamUtils.getDeptById(deptId);

                // 查询当前部门当前月份是否已存在记录 
                searchVo.setDeptId(deptId);
                List<EvaluateUser> evaluateUserList = evaluateUserClient.findAllByEvaluateMonthAndDeptId(searchVo);
                if(evaluateUserList != null && evaluateUserList.size() > 0) {
                    RenderUtil.renderFailure("部门：["+dept.getDeptName()+"]当月已存在考评任务", response);
                    return;
                }
                deptIdList.add(deptId);
            }
            vo.setDeptIds(deptIdList);
            evaluateUserClient.startEvaluate(vo);
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderFailure("发起考评失败", response);
        }
    }
}
