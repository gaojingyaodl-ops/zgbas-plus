package com.spt.bas.web.controller.evaluate;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.EvaluateItem;
import com.spt.bas.client.remote.IEvaluateItemClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/evaluate/item")
public class EvaluateItemController extends PageController<EvaluateItem, BaseVo> {
    @Autowired
    private IEvaluateItemClient evaluateItemClient;
    @Override
    public BaseClient<EvaluateItem> getService() {
        return evaluateItemClient;
    }
    @RequestMapping(value = "content")
    public String content(Model model) {
        
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        model.addAttribute("evaluateDeptJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_DEPT)));
                

        return "evaluate/evaluate-item";
    }
    
    /**
     * 查询所有信息
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findAllEvaluateItemLoading")
    public void findAllEvaluateItemLoading(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        searchVo.setSort("dispOrderNo");
        searchVo.setOrder("ASC");
        PageDown<EvaluateItem> page = evaluateItemClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }
    /**
     * 跳转到详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        Long shiroid = ShiroUtil.getEnterpriseId();
        model.addAttribute("evaluateGroupJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_GROUP)));
        model.addAttribute("evaluateMetricsJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_METRICS)));
        model.addAttribute("evaluateDeptJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.EVALUATE_DEPT)));
        EvaluateItem entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new EvaluateItem();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "evaluate/evaluate-item-detail";

    }
    @ModelAttribute("preload")
    public EvaluateItem getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                EvaluateItem entity = new EvaluateItem();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }
   

    /**
     * 添加方法
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void save(EvaluateItem entity,HttpServletRequest request, HttpServletResponse response) {
//        BsConfig bsConfig = new BsConfig();
//        bsConfig.setEnableFlg(Boolean.valueOf(request.getParameter("enableFlg")));//是否有效
//        bsConfig.setContractModel(request.getParameter("contractModel"));//赊销模式
//        bsConfig.setFundSource(request.getParameter("fundSource"));//资金来源
//        bsConfig.setRemark(request.getParameter("remark"));//备注
//        bsConfig.setOurCompanyName(request.getParameter("ourCompanyName"));//我方抬头
//        bsConfig.setSxCompany(request.getParameter("sxCompany"));//代采赊销单位
//        bsConfig.setBalance(StringUtils.isNotBlank(request.getParameter("balance")) ? new BigDecimal(request.getParameter("balance")) : BigDecimal.ZERO);
//        bsConfig.setEnterpriseId(ShiroUtil.getEnterpriseId());//企业id
//        bsConfig.setMatchUserId(String.valueOf(ShiroUtil.getCurrentUserId())); //业务员ID
//        bsConfig.setMatchUserName(ShiroUtil.getCurrentUserName());//业务员姓名
        try{
            evaluateItemClient.save(entity);
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
    }
    /**
     * 修改方法
     * @param request
     * @param response
     */
    @RequestMapping("update")
    public void update(EvaluateItem entity,HttpServletRequest request, HttpServletResponse response){
        try{
            evaluateItemClient.save(entity);
            RenderUtil.renderSuccess("修改成功", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderSuccess("修改失败", response);
        }
    }
    /**
     * 删除方法
     * @param id
     * @param request
     * @param response
     * @return
     */
    @Override
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            evaluateItemClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }
}
