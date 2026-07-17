/**
 *
 */
package com.spt.bas.web.controller.pm;

import com.spt.bas.client.remote.IPmProcessAutoStepClient;
import com.spt.bas.client.remote.IPmProcessConditionClient;
import com.spt.bas.client.remote.IPmProcessNodeClient;
import com.spt.bas.client.remote.IPmProcessStepClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcessAutoStep;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 额外条件节点配置
 *
 */
@Controller
@RequestMapping(value = "/pm/processAutoStep")
public class PmProcessAutoStepController extends SingleCrudControll<PmProcessAutoStep, BaseVo> {
    @Autowired
    private IPmProcessAutoStepClient pmProcessAutoStepClient;
    @Autowired
    private IPmProcessNodeClient pmProcessNodeClient;
    @Autowired
    private IPmProcessConditionClient pmProcessConditionClient;
    @Autowired
    private IPmProcessStepClient pmProcessStepClient;
    @Override
    public BaseClient<PmProcessAutoStep> getService() {
        return pmProcessAutoStepClient;
    }

    @RequestMapping(value = "findAutoStep/{id}", method = RequestMethod.GET)
    public String findAutoStep(@PathVariable("id") Long id, Model model) {
        model.addAttribute("processId", id);

        List<PmProcessCondition> conditionList = pmProcessConditionClient.findConditionsByProcessId(id);
        model.addAttribute("conditionListJson", JsonUtil.obj2Json(conditionList));

        List<PmProcessNode> processNodeList = pmProcessNodeClient.findNodeList(ShiroUtil.getEnterpriseId());
        model.addAttribute("processNodeListJson", JsonUtil.obj2Json(processNodeList));
        return "pm/processAutoStep";
    }

    @RequestMapping(value = "findProcessStep", method = RequestMethod.POST)
    public void findProcessStep(Long conditionId, HttpServletResponse response) {
        if (Objects.nonNull(conditionId)) {
            List<PmProcessStep> processStepList = pmProcessStepClient.findStepByConditionId(conditionId);
            processStepList.forEach(step -> {
                PmProcessNode node = pmProcessNodeClient.getEntity(step.getNodeId());
                if (Objects.nonNull(node)) {
                    step.setStepName(node.getNodeName());
                }
            });
            RenderUtil.renderJson(processStepList, response);
        }
    }
}
