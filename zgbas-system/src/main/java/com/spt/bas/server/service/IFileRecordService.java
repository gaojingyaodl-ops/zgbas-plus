package com.spt.bas.server.service;

import com.spt.bas.client.entity.FileRecord;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * 附件记录
 * @author shengong
 */
public interface IFileRecordService extends IBaseService<FileRecord> {
    void deleteByFileId(String fileId);

    List<FileRecord> findByFileIds(List<String> fileIds);

    FileRecord findByFileId(String fileId);

}
