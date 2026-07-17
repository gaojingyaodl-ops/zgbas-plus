package com.spt.bas.web.controller.sign;

import com.alibaba.fastjson.JSONObject;
import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.client.remote.IFileRecordClient;
import com.spt.bas.client.remote.ISignFileClient;
import com.spt.bas.client.remote.ISignFileUserClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
@RequestMapping("documentsUser/sign")
public class SignUserController extends SingleCrudControll<SignFileUser, BaseVo> {

    @Autowired
    private ISignFileUserClient signFileUserClient;

    @Autowired ISignFileClient signFileClient;

    @Autowired
    private IFileRecordClient fileRecordClient;

    @Override
    public BaseClient<SignFileUser> getService() {
        return signFileUserClient;
    }

    /**
     * 生成电子签合同
     */
    @RequestMapping(value = "generateSignature/{signFileId}", method = RequestMethod.GET)
    public void generateSignature(@PathVariable("signFileId") Long signFileId, HttpServletResponse response) {
        try {
            SignFile entity = signFileClient.getEntity(signFileId);
            if (StringUtils.isBlank(entity.getFileId())){
                RenderUtil.renderFailure("签署附件缺失，请上传PDF格式的附件", response);
                return;
            }
            SignFile signFile = signFileClient.generateSignature(signFileId);
            if (StringUtils.isBlank(signFile.getCfcaContractNo())){
                RenderUtil.renderFailure("生成电子签合同失败", response);
            }else{
                RenderUtil.renderSuccess("success", response);
            }
        } catch (Exception e) {
            logger.error("generateSignature:", e);
            String msg = "";
            if (e.getCause() != null) {
                JSONObject jsonObject = JSONObject.parseObject(e.getCause().getMessage());
                msg = jsonObject.getString("message");
            }
            RenderUtil.renderFailure(msg, response);
        }
    }

    /**
     * 刷新电子签状态
     */
    @RequestMapping(value = "refreshSignFile/{cfcaContractNo}", method = RequestMethod.GET)
    public void refreshSignFile(@PathVariable("cfcaContractNo") String cfcaContractNo, HttpServletResponse response) {
        try {
            signFileClient.refreshSignFile(cfcaContractNo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
//            logger.error("refreshSignFile:", e);
            String errorMeg = "";
            Throwable cause = e.getCause();
            if (Objects.nonNull(cause)) {
                String message = cause.getMessage();
                ErrorResp errorResp = JsonUtil.json2Object(ErrorResp.class, message);
                errorMeg = errorResp.getMessage();
            }
            RenderUtil.renderFailure("提示：" + errorMeg, response);
        }
    }

    /**
     * 作废
     */
    @RequestMapping(value = "invalidSignFile/{signFileId}", method = RequestMethod.GET)
    public void invalidSignFile(@PathVariable("signFileId") Long signFileId, HttpServletResponse response) {
        try {
            if (Objects.isNull(signFileId) || signFileId == 0L){
                return;
            }
            SignFile entity = signFileClient.getEntity(signFileId);
            entity.setEnableFlg(false);
            signFileClient.save(entity);
            logger.info("{}作废文件签署数据，signFileId:{}，fileName:{}", ShiroUtil.getCurrentUserName(), signFileId, entity.getFileName());
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("invalidSignFile:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }
}
