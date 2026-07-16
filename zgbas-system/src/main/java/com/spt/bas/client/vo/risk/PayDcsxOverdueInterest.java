package com.spt.bas.client.vo.risk;

import com.spt.tools.core.bean.PageSearchVo;
import lombok.Data;


/**
 * <p>
 *  业务台账中游付息
 * </p>
 *
 */
@Data
public class PayDcsxOverdueInterest extends PageSearchVo {

    /**
     * 中游合同id集合（以,分割的字符串）
     */
    private String dcsxContractIdList;

    /**
     * 申请人ID
     */
    private Long applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;
    /**
     * 企业账套ID
     */
    private Long enterpriseId;

}
