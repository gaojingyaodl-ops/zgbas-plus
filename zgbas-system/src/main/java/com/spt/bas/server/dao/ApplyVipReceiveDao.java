package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.client.entity.ApplyVipReceive;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ApplyVipReceiveDao extends BaseDao<ApplyVipReceive> {

}

