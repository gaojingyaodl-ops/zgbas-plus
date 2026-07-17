package com.spt.bas.server.service;

import com.spt.bas.client.entity.FileProcessRel;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

/**
 * 附件记录
 * @author shengong
 */
public interface IFileProcessRelService extends IBaseService<FileProcessRel> {
    List<FileProcessRel> findList(String processCode);

}
