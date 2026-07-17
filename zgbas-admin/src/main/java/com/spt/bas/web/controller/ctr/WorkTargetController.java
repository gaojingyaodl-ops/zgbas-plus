package com.spt.bas.web.controller.ctr;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.client.remote.IWorkTargetClient;
import com.spt.bas.client.vo.ContractSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.DateUtils;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/work/target")
public class WorkTargetController extends SingleCrudControll<WorkTarget, BaseVo> {

    @Autowired
    private IWorkTargetClient workTargetClient;

    @Override
    public BaseClient<WorkTarget> getService() {
        return workTargetClient;
    }

    @RequestMapping(value = "workTarget")
    public String index(Model model) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        model.addAttribute("nowTargetMonth", now.format(pattern));
        return "workTarget/workTargetPage";
    }

    @RequestMapping(value = "/workTargetList")
    public void workTargetList(ContractSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        searchVo.setSort("targetType");
        initSearch(searchVo, request);
        logger.info("searchVo : " + JsonUtil.obj2Json(searchVo));
        Map<String, Object> footer = new HashMap<>();
        Page<WorkTarget> page = workTargetClient.findPage(searchVo);
        formatWorkTarget(page);
        JsonEasyUI.renderJson(response, page, null, footer);
    }

    /**
     * 转化数据字典
     *
     * @param page 每页数据
     */
    private void formatWorkTarget(Page<WorkTarget> page) {
        List<BsDictData> targetList = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.TARGET_TYPE);
        Map<String, String> targetMap = targetList.stream().collect(Collectors.toMap(BsDictData::getDictCd, BsDictData::getDictName, (a, b) -> b));
        List<WorkTarget> content = page.getContent();
        if (CollectionUtils.isNotEmpty(content)) {
            for (WorkTarget workTarget : content) {
                workTarget.setTargetTypeStr(targetMap.getOrDefault(workTarget.getTargetType(), ""));
            }
        }
    }

    @GetMapping("/workTargetAdd/{id}")
    public String addOrEdit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("branchList", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.BRANCH_CD)));
        model.addAttribute("targetType", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.TARGET_TYPE)));
        if (id != 0) {
            WorkTarget entity = getService().getEntity(id);
            BigDecimal targetTotalAmount = entity.getTargetTotalAmount();
            if(targetTotalAmount != null) {
                BigDecimal divide = targetTotalAmount.divide(new BigDecimal(10000),2, RoundingMode.HALF_UP);
                entity.setTargetTotalAmount(divide);
            }
            model.addAttribute("entity", entity);
        } else {
            model.addAttribute("entity", new WorkTarget());
        }
        return "workTarget/work-target-add";
    }

    /**
     * 新增、修改方法
     *
     * @param workTarget 实体
     * @param response   返回
     */
    @PostMapping("/saveWorkTarget")
    public void save(WorkTarget workTarget, HttpServletResponse response) {
        String validParam = validParam(workTarget);

        if (StringUtils.isNotEmpty(validParam)) {
            RenderUtil.renderFailure(validParam, response);
            return;
        }
        if (Objects.isNull(workTarget.getId())) {
            String isHave = checkMonthIsHaveTarget(workTarget);
            if (StringUtils.isNotEmpty(isHave)) {
                RenderUtil.renderFailure(isHave, response);
                return;
            }
        }
        BigDecimal targetTotalAmount = workTarget.getTargetTotalAmount();
        if(targetTotalAmount != null) {
            BigDecimal multiply = targetTotalAmount.multiply(new BigDecimal(10000));
            workTarget.setTargetTotalAmount(multiply);
        }
        workTarget.setCreatedDate(DateUtils.getNowDate());
        workTarget.setUpdatedDate(DateUtils.getNowDate());
        workTargetClient.saveData(workTarget);
        RenderUtil.renderSuccess("保存成功！", response);
    }

    public String checkMonthIsHaveTarget(WorkTarget workTarget) {
        WorkTarget query = new WorkTarget();
        query.setTargetType(workTarget.getTargetType());
        query.setBranchCd(workTarget.getBranchCd());
        query.setTargetMonth(workTarget.getTargetMonth());
        return workTargetClient.findByBranchCdAndTargetMonth(query);
    }

    /**
     * 参数校验
     *
     * @param workTarget 实体
     */
    private String validParam(WorkTarget workTarget) {
        if (StringUtils.isEmpty(workTarget.getTargetMonth())) {
            return "目标月份不能为空！";
        }
        if (StringUtils.isEmpty(workTarget.getBranchCd())) {
            return "所属地区cd不能为空！";
        }
        if (StringUtils.isEmpty(workTarget.getBranchName())) {
            return "所属地区不能为空！";
        }
        if (StringUtils.isBlank(workTarget.getTargetType())) {
            return "总价类型不能为空！";
        }
        return null;
    }


    /**
     * 删除方法
     *
     * @param id       id
     * @param response 返回结果
     */
    @GetMapping("/delete/{id}")
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) {
        if (Objects.isNull(id)) {
            logger.error("任务目标删除--id为空！");
            RenderUtil.renderFailure("删除失败！请联系管理员", response);
            return;
        }
        workTargetClient.deleteData(id);
        RenderUtil.renderSuccess("保存成功！", response);
    }
}
