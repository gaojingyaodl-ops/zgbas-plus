package com.spt.quartz.task;

import com.google.common.base.Splitter;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.service.IApplyDcsxService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.service.IPmApproveService;
import com.spt.tools.core.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author MoonLight
 * @Date 2023/10/13 17:04
 * @Version 1.0
 *
 * <p>Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.AutoSealPdfTask}.
 * Bean name {@code "autoSealPdfTask"} aligns with {@code sys_job.invoke_target}
 * short names {@code autoSealPdfTask.generateSealPDFSignDCSX('${approveNo,contractNo}')}
 * and {@code autoSealPdfTask.successSignContractByKeyword('${approveNo,contractNo}')}.
 */
@Slf4j
@Component("autoSealPdfTask")
public class AutoSealPdfTask {
    @Resource
    private IAutoSealPdfSignFilter autoSealPdfSignFilter;

    @Resource
    private IPmApproveService pmApproveService;

    @Resource
    private IApplyDcsxService applyDcsxService;

    @Resource
    private IPmApproveContentsClient approveContentsClient;

    /**
     * 代采赊销盖章申请，附件生成异常补偿任务
     */
    public void generateSealPDFSignDCSX(String param) throws ApplicationException {
        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("参数不可为空, 格式approveNo,contractNo");
        }
        List<String> paramList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(param);
        if (paramList.size() < 2) {
            throw new RuntimeException("参数格式错误, 正确格式approveNo,contractNo");
        }
        PmApprove approve = pmApproveService.findByApproveNo(paramList.get(0));
        ApplyCtrDCSX entity = applyDcsxService.findByContractNo(paramList.get(1));
        autoSealPdfSignFilter.generateSealPDFSignDCSXV2(approve, entity);
        log.info("代采赊销盖章申请，附件生成异常补偿任务执行成功!");
    }

    /**
     * 代采赊销盖章审批完成后自动执行签署逻辑补偿任务
     */
    @ServerTransactional
    public void successSignContractByKeyword(String param) throws ApplicationException {
        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("参数不可为空, 格式approveNo,contractNo");
        }
        List<String> paramList = Splitter.on(BasConstants.COMMA).omitEmptyStrings().splitToList(param);
        if (paramList.size() < 2) {
            throw new RuntimeException("参数格式错误, 正确格式approveNo,contractNo");
        }
        PmApprove approve = pmApproveService.findByApproveNo(paramList.get(0));
        ApplyCtrDCSX entity = applyDcsxService.findByContractNo(paramList.get(1));
        PmApproveContents approveContents = approveContentsClient.findByApproveId(approve.getId());
        String resultId = autoSealPdfSignFilter.successSignContractByKeyword(approveContents.getCfcaContractNo());

        if (StringUtils.isNotBlank(resultId)) {
            approveContents.setFileId(resultId);
            approveContentsClient.save(approveContents);

            entity.setFileId(resultId);
            applyDcsxService.save(entity);
        }
        log.info("代采赊销盖章审批完成后自动执行签署逻辑补偿任务执行成功!");
    }
}
