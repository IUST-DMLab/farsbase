package ir.ac.iust.dml.kg.knowledge.commons;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * Class for paging list
 */
public class PagingList<T> {
    private List<T> data;
    private int page;
    private int pageSize;
    private long pageCount;
    private long totalSize;

    public PagingList() {
        data = null;
        page = pageSize = 0;
        pageCount = totalSize = 0;
    }

    public PagingList(List<T> data) {
        this.data = data;
        this.page = this.pageSize = 0;
        this.totalSize = data.size();
        this.pageCount = 1;
    }

    public PagingList(List<T> data, int page, int pageSize, long totalSize) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.pageCount = totalSize % pageSize != 0 ? totalSize / pageSize + 1 : totalSize / pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
