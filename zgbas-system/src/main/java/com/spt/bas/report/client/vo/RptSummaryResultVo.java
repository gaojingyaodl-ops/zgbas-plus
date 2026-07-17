package com.spt.bas.report.client.vo;

import com.spt.tools.data.vo.PageDown;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/4/12 16:05
 */

public class RptSummaryResultVo implements Serializable {
    PageDown<RptSummaryRoiResultVo> page;

    Map<String, Object> footer;

    public RptSummaryResultVo() {
    }

    public PageDown<RptSummaryRoiResultVo> getPage() {
        return page;
    }

    public void setPage(PageDown<RptSummaryRoiResultVo> page) {
        this.page = page;
    }

    public Map<String, Object> getFooter() {
        return footer;
    }

    public void setFooter(Map<String, Object> footer) {
        this.footer = footer;
    }
}
