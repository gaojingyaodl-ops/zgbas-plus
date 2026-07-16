package com.spt.bas.server.dao;

import com.spt.bas.client.entity.FileProcessRel;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

/**
 * 附件流程关系
 *
 * @author shengong
 */
public interface FileProcessRelDao extends BaseDao<FileProcessRel> {

    List<FileProcessRel> findByProcessCodeOrderByOrderNo(String processCode);

}
