package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;
import lombok.Data;

import java.util.List;

/**
 * 库存台账查询实体
 */
@Data
public class RptStockBookVo extends PageSearchVo {
    // 库存编号
    private String stockVirtualNo;
    // 销售合同号
    private String sellContractNo;
    // 业务区域
    private Long deptId;
    // 签约开始日期
    private String  contractTimeBegin;
    // 签约结束日期
    private String contractTimeEnd;
    // 业务类型
    private String businessType;
    // 赊销标识
    private Boolean matchCreditFlg =false;
    // 货名
    private String productName;
    // 当前登录人Id
    private Long userId;
    // 资金方能查看的企业名称
    private List<String> ourCompanyList;
    // 查看所有数据权限
    private Boolean viewAllFlg =false;
}
