package com.spt.quartz.task;

import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.service.ISignFileService;
import com.spt.sign.client.entity.SignContract;
import com.spt.sign.client.remote.ISignContractClient;
import com.spt.sign.client.remote.ISignInfoClient;
import com.spt.tools.core.exception.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.slf4j.Logger;
import java.util.List;
import java.util.Objects;

/**
 * Phase 6 (06-02) — ported from {@code com.spt.bas.server.task.SignTask}.
 * Bean name {@code "signTask"} aligns with {@code sys_job.invoke_target}
 * short name {@code signTask.doUploadContractSigned}.
 */
@Component("signTask")
public class SignTask {
    private Logger logger = LoggerFactory.getLogger(SignTask.class);
    @Autowired
    private ISignContractClient signContractClient;

    @Autowired
    private  ISignFileService iSignFileService;

    /**
     * 上传合同签署
     */
    /**
     * 定时任务，更新逾期印章外借状态
     */
    public void doUploadContractSigned() {
        // 查询近5分钟更新未签署的签收单 执行签署合同操作后 返回电子合同列表 （注意：返回的signContractList 合同已执行DownloadContractNo操作）
        List<SignContract> signContractList = signContractClient.getUploadContractSignedUpdate();
        if (CollectionUtils.isEmpty(signContractList)) {
            logger.info(">>>暂未查询到自定义上传合同完成签署!");
            return;
        }
        signContractList.forEach(signContract -> {
            String downloadPath = signContract.getDownloadPath();
            String contractNo = signContract.getContractNo();
            String contractStatus = signContract.getContractState();
            SignFile byCfcaContractNo = iSignFileService.findByCfcaContractNo(contractNo);
            if (Objects.nonNull(byCfcaContractNo) && StringUtils.isNotBlank(downloadPath)) {
                byCfcaContractNo.setFileId(downloadPath);
                byCfcaContractNo.setSignStatus(contractStatus);
                try {
                    iSignFileService.save(byCfcaContractNo);
                } catch (ApplicationException e) {
                    logger.error("SignTask.doUploadContractSigned save error", e);
                }
                logger.info("contractNo:{} downloadPath:{}", contractNo, downloadPath);
            }
        });
    }

}
