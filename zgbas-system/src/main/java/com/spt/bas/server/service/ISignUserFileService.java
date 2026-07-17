package com.spt.bas.server.service;

import com.spt.bas.client.entity.SignFileUser;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface ISignUserFileService extends IBaseService<SignFileUser> {
    List<SignFileUser> findSignFileUserBySignId(Long signId);
    public void saveDatas(List<SignFileUser> insertedRecords,
                          List<SignFileUser> updatedRecords, List<SignFileUser> deletedRecords,
                          Long signFileId);
}
