package cn.edu.sdjzu.campussecondhandtradingsystem.common;

import java.util.List;

public class PageResult<T> {

    private long page;

    private long size;

    private long total;

    private List<T> records;

    public PageResult() {
    }

    public PageResult(long page, long size, long total, List<T> records) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.records = records;
    }

    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        return new PageResult<>(page, size, total, records);
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
