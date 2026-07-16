/**
 * 
 */
package com.spt.tools.data.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author wlddh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PageDown<T> extends PageImpl<T> {

	private static final long serialVersionUID = 1L;
    private int number;
    private int size;
    private int totalPages;
    private int numberOfElements;
    private long totalElements;
    private boolean first;
    private boolean last;
	private List<T> content;
	private SortImpl sort;
	private PageRequestImpl pageable;

    public PageDown() {
        super(new ArrayList<T>());
    }

    public PageImpl<T> pageImpl() {
        return new PageImpl<T>(getContent(), PageRequest.of(getNumber(), getSize(), getSort()),
				getTotalElements());
    }
}
