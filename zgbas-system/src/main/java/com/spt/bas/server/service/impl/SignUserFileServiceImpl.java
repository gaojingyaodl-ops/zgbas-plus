package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.SignFileUser;
import com.spt.bas.server.dao.sign.SignFileUserDao;
import com.spt.bas.server.service.ISignUserFileService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class SignUserFileServiceImpl extends BaseService<SignFileUser> implements ISignUserFileService {
    @Autowired
    private SignFileUserDao signFileUserDao;
    @Override
    public BaseDao<SignFileUser> getBaseDao() {
        return signFileUserDao;
    }

    @Override
    public List<SignFileUser> findSignFileUserBySignId(Long signId) {
        return signFileUserDao.findBySignId(signId);
    }

    @Transactional(readOnly = false)
    @Override
    public void saveDatas(List<SignFileUser> insertedRecords,
                          List<SignFileUser> updatedRecords, List<SignFileUser> deletedRecords,
                          Long signFileId) {
        if (CollectionUtils.isNotEmpty(insertedRecords)){
            for (SignFileUser dictData : insertedRecords) {
                saveData(dictData, signFileId);
            }
        }
        if (CollectionUtils.isNotEmpty(updatedRecords)){
            for (SignFileUser dictData : updatedRecords) {
                saveData(dictData, signFileId);
            }
        }
        if (CollectionUtils.isNotEmpty(deletedRecords)){
            signFileUserDao.deleteAll(deletedRecords);
        }
    }

    private void saveData(SignFileUser data, Long signFileId) {
        data.setSignFileId(signFileId);
        data.setUpdatedDate(new Date());
        data.setCreatedDate(new Date());
        signFileUserDao.save(data);
    }
}
