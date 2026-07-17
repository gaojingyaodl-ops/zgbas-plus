package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.FileType;
import com.spt.bas.server.dao.FileTypeDao;
import com.spt.bas.server.service.IFileTypeService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class FileTypeServiceImpl extends BaseService<FileType> implements IFileTypeService {
    @Autowired
    private FileTypeDao fileTypeDao;

    @Override
    public BaseDao<FileType> getBaseDao() {
        return fileTypeDao;
    }
}
