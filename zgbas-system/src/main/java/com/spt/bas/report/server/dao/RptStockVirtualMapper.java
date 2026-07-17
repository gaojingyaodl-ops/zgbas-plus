package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptStockVirtualSearchVo;
import com.spt.bas.report.client.vo.RptStockVirtualVo;
import com.spt.bas.report.client.vo.RptWxStockVirtualSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:58
 */
@MyBatisDao
public interface RptStockVirtualMapper {

    /**
     * 查询虚拟库存的数据
     * @param queryVo 查询参数
     * @return 查询结果
     */
    List<RptStockVirtualVo> selectAssessment(RptStockVirtualSearchVo queryVo);

    List<RptStockVirtualVo> getWxStockVirtualPage(RptWxStockVirtualSearchVo queryVo);
}
