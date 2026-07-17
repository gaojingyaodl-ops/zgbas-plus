package com.spt.bas.server.api;

import com.spt.bas.client.entity.WorkTarget;
import com.spt.bas.server.rocketmq.send.WorkTargetSendMessage;
import com.spt.bas.server.service.IWorkTargetService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = "/work/target")
public class WorkTargetApi extends BaseApi<WorkTarget> {

    @Autowired
    private IWorkTargetService workTargetService;
    @Autowired
    private WorkTargetSendMessage workTargetSendMessage;

    @Override
    public IDataService<WorkTarget> getService() {
        return workTargetService;
    }

    @PostMapping("/findByBranchCdAndTargetMonth")
    String findByBranchCdAndTargetMonth(@RequestBody WorkTarget query) {
        return workTargetService.findByBranchCdAndTargetMonth(query);
    }

    /**
     * 保存任务目标
     *
     * @param workTarget 任务目标
     * @throws ApplicationException
     */
    @PostMapping("/saveData")
    public void saveData(@RequestBody WorkTarget workTarget) throws ApplicationException {
        boolean isNew = Objects.isNull(workTarget.getId());
        workTargetService.save(workTarget);
        // 推送消息
        if (isNew) {
            workTargetSendMessage.addWorkTarget(workTarget);
        } else {
            workTargetSendMessage.updateWorkTarget(workTarget);
        }
    }

    /**
     * 删除任务目标
     *
     * @param id
     * @throws ApplicationException
     */
    @GetMapping("/deleteData")
    public void deleteData(@RequestParam(value = "id", required = false) Long id) throws ApplicationException {
        workTargetService.delete(id);
        // 推送消息
        workTargetSendMessage.deleteWorkTarget(id);
    }
}
