package com.spt.bas.server.api.sign;

import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.server.service.ISignUserFileService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import com.spt.tools.data.vo.BatchSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "signUser/file")
public class SignFileUserApi extends BaseApi<SignFileUser> {

    @Autowired
    private ISignUserFileService signFileUserService;

    @Override
    public IDataService<SignFileUser> getService() {
        return signFileUserService;
    }

    @PostMapping(value = "findSignFileUserBySignId")
    public List<SignFileUser> findConditionsByProcessId(@RequestBody Long SignId){
        return signFileUserService.findSignFileUserBySignId(SignId);
    }
    @PostMapping(value = "saveDatas")
    public void saveDatas(@RequestBody BatchSaveVo<SignFileUser> batchSaveVo, @RequestParam("signFileId") Long signFileId){
        signFileUserService.saveDatas(batchSaveVo.getInsertedRecords(),batchSaveVo.getUpdatedRecords(),batchSaveVo.getDeletedRecords(),signFileId);
    }
}
