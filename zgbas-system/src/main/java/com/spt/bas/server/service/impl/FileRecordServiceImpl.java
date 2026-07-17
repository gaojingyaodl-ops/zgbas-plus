package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.FileRecord;
import com.spt.bas.server.dao.FileRecordDao;
import com.spt.bas.server.service.IFileRecordService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *     附件操作
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-02 14:11
 */
@Component
@Transactional(readOnly = true)
public class FileRecordServiceImpl extends BaseService<FileRecord> implements IFileRecordService {
    @Autowired
    private FileRecordDao fileRecordDao;

    @Override
    public BaseDao<FileRecord> getBaseDao() {
        return fileRecordDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFileId(String fileId) {
        fileRecordDao.deleteByFileId(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileRecord> findByFileIds(List<String> fileIds) {
        return fileRecordDao.findByFileIds(fileIds);
    }

    @Override
    public FileRecord findByFileId(String fileId) {
        return fileRecordDao.findByFileId(fileId);
    }

}
