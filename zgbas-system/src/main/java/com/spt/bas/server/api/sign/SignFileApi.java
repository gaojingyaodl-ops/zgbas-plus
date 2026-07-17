package com.spt.bas.server.api.sign;

import com.spt.bas.client.entity.SignFile;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.client.vo.sign.SignFileSearchVo;
import com.spt.bas.server.dao.sign.SignFileDao;
import com.spt.bas.server.service.ISignFileService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sign/file")
public class SignFileApi extends BaseApi<SignFile> {
    @Autowired
    private ISignFileService signFileService;
    @Autowired
    private SignFileDao signFileDao;
    @Override
    public IDataService<SignFile> getService() {
        return signFileService;
    }

    @PostMapping("findPageSignFile")
    public Page<SignFile> findPageSignFile(@RequestBody SignFileSearchVo searchVo){
        return signFileService.findPageSignFile(searchVo);
    }

    @PostMapping("findByCfcaContractNo")
    public  SignFile findByCfcaContractNo(@RequestBody String cfcaContractNo){
        return signFileService.findByCfcaContractNo(cfcaContractNo);
    }

    @PostMapping(value = "/generateSignature")
    public SignFile generateSignature(@RequestBody Long signId) throws ApplicationException{
        return signFileService.generateSignature(signId, 2);
    }

    @PostMapping(value = "/refreshSignFile")
    public SignFile refreshSignFile(@RequestBody String cfcaContractNo) throws ApplicationException {
        return signFileService.refreshSignFile(cfcaContractNo);
    }

    @PostMapping("updateFileId")
    public void updateFileId(@RequestBody FileIdUpdateVo vo) {
        signFileDao.updateFileId(vo.getId(), vo.getFileId());
    }

    @PostMapping("findByAllLimit")
    public SignFile findByAllLimit() {
        return signFileService.findByAllLimit();
    };

}
