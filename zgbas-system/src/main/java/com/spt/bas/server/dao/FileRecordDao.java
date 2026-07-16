package com.spt.bas.server.dao;

import com.spt.bas.client.entity.FileRecord;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 附件记录
 * @author shengong
 */
public interface FileRecordDao extends BaseDao<FileRecord> {

    @Transactional
    @Modifying
    void deleteByFileId(String fileId);

    @Query("select a from FileRecord a where a.fileId in ?1")
    List<FileRecord> findByFileIds(List<String> fileIds);

    FileRecord findByFileId(String fileId);

}
