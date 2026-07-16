// STUB — Phase 4 ports the real interface from spt-bas-client. Temporary for Phase 3 IndexController compilation.
package com.spt.bas.client.remote;

import com.spt.bas.client.vo.ApproveWaitSearchVo;

/**
 * Temporary stub contract. Phase 4 replaces this with the real
 * {@code spt-bas-client} FeignClient interface. IndexController injects it via
 * {@code @Autowired(required = false)} so the absence of a runtime bean degrades
 * business-data calls gracefully (D-P3-10) without breaking startup.
 */
public interface IApproveWaitDealClient {

    Long getUserWaitDealNum(ApproveWaitSearchVo vo);
}
