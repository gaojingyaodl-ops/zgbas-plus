package com.spt.pm.vo;

/**
 * 流程节点引用统计VO
 * 用于统计节点被审批流程引用的流程名称和引用次数
 *
 * @author gaojy
 */
public class PmProcessNodeRefVo {

    /**
     * 引用流程名称
     */
    private String processName;

    /**
     * 引用次数
     */
    private Long refCount;

    public PmProcessNodeRefVo() {
    }

    public PmProcessNodeRefVo(String processName, Long refCount) {
        this.processName = processName;
        this.refCount = refCount;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getRefCount() {
        return refCount;
    }

    public void setRefCount(Long refCount) {
        this.refCount = refCount;
    }
}