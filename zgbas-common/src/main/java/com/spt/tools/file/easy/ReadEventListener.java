/**
 * 
 */
package com.spt.tools.file.easy;

import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

/**
 * @author wlddh
 *
 */
public class ReadEventListener<T> extends AnalysisEventListener<T> {
	private List<T> rows;

	public ReadEventListener(List<T> rows) {
		this.rows = rows;
	}

	@Override
	public void invoke(T object, AnalysisContext context) {
		rows.add(object);
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
	}
}
