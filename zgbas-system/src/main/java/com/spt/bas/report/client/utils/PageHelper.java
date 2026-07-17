package com.spt.bas.report.client.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spt.tools.data.vo.PageRequestImpl;
import com.spt.tools.data.vo.SortImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-10 09:31
 */
public class PageHelper<T> extends PageImpl<T> {

    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private int number;
    @JsonIgnore
    private int size;
    private int totalPages;
    @JsonIgnore
    private int numberOfElements;
    private long totalElements;
    @JsonIgnore
    private boolean first;
    @JsonIgnore
    private boolean last;
    private List<T> content;
    @JsonIgnore
    private SortImpl sort;
    @JsonIgnore
    private PageRequestImpl pageable;

    private Object footer;
    public PageHelper() {
        super(new ArrayList<T>());
    }

    public PageImpl<T> pageImpl() {
        return new PageImpl<T>(getContent(), PageRequest.of(getNumber(), getSize(), getSort()),
                getTotalElements());
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    @Override
    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public SortImpl getSort() {
        return sort;
    }

    public void setSort(SortImpl sort) {
        this.sort = sort;
    }

    @Override
    public PageRequestImpl getPageable() {
        return pageable;
    }

    public void setPageable(PageRequestImpl pageable) {
        this.pageable = pageable;
    }

    public Object getFooter() {
        return footer;
    }

    public void setFooter(Object footer) {
        this.footer = footer;
    }
}
