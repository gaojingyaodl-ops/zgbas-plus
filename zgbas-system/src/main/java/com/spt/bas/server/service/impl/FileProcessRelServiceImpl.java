package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.FileProcessRel;
import com.spt.bas.client.entity.FileType;
import com.spt.bas.server.dao.FileProcessRelDao;
import com.spt.bas.server.dao.FileTypeDao;
import com.spt.bas.server.service.IFileProcessRelService;
import com.spt.bas.server.service.IFileTypeService;
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
public class FileProcessRelServiceImpl extends BaseService<FileProcessRel> implements IFileProcessRelService {
    @Autowired
    private FileProcessRelDao fileProcessRelDao;

    @Override
    public BaseDao<FileProcessRel> getBaseDao() {
        return fileProcessRelDao;
    }

    @Override
    public List<FileProcessRel> findList(String processCode) {
        return fileProcessRelDao.findByProcessCodeOrderByOrderNo(processCode);
    }

}
