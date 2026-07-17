package com.spt.bas.server.filter;

import com.spt.bas.client.entity.ApplyCtrDCSX;

/**
 * @Author: gaojy
 * @create 2022/11/25 10:59
 * @version: 1.0
 * @description:
 */
public interface IAutoStartPayFilter {

    void doSealUsageFilter(ApplyCtrDCSX entity);

    void doApplyReceiveFilter(ApplyCtrDCSX entity);

    void doPayTaskFilter(ApplyCtrDCSX entity);

    void doSealUsageFilterAutoTask(ApplyCtrDCSX entity ,int i);
}
