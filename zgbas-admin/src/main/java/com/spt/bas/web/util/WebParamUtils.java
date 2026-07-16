// STUB — Phase 4 ports the real WebParamUtils. Methods return empty/null per D-P3-10.
package com.spt.bas.web.util;

import com.spt.bas.client.entity.BsCompanyDcsx;

/**
 * Temporary stub of {@code WebParamUtils}. The real implementation (source
 * {@code com.spt.bas.web.util.WebParamUtils}) depends on ~15 unmigrated remote
 * clients and cannot be ported in Phase 3. This stub exposes only the two
 * methods IndexController calls.
 *
 * <p>NOT annotated {@code @Component}: IndexController injects it via
 * {@code @Autowired(required = false)}, so with no bean present the field is
 * {@code null} and null-guards skip the calls (business data degrades to
 * empty/zero per D-P3-10). Phase 4 ports the real {@code @Component} which then
 * satisfies the optional injection.
 */
public class WebParamUtils {

    public String formatterWaitDealNum(Long num) {
        return num == null ? "0" : String.valueOf(num);
    }

    public BsCompanyDcsx queryFundCompany() {
        return null;
    }
}
