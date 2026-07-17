package com.spt.bas.server.service;

import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.vo.AssComplaintsSearchVo;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

/**
 * 投诉记录
 * @Author: gaojy
 * @create 2022/5/23 15:58
 * @version: 1.0
 * @description:
 */
public interface IAssComplaintsService extends IBaseService<AssComplaints> {

    AssComplaints saveComplaints(AssComplaints assComplaints);

    Page<AssComplaints> findComplaintsPage(AssComplaintsSearchVo complaintsSearchVo);
}
