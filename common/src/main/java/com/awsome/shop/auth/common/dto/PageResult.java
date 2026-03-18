package com.awsome.shop.auth.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用分页结果类
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从 0 开始）
     */
    private Long currentPage;

    /**
     * 每页记录数
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long totalElements;

    /**
     * 总页数
     */
    private Long totalPages;

    /**
     * 当前页数据列表
     */
    private List<T> content;

    /**
     * 类型转换方法
     *
     * @param converter 转换函数
     * @param <R>       目标类型
     * @return 转换后的分页结果
     */
    public <R> PageResult<R> convert(Function<T, R> converter) {
        List<R> convertedRecords = content.stream()
                .map(converter)
                .collect(Collectors.toList());

        PageResult<R> result = new PageResult<>();
        result.setCurrentPage(this.currentPage);
        result.setSize(this.size);
        result.setTotalElements(this.totalElements);
        result.setTotalPages(this.totalPages);
        result.setContent(convertedRecords);
        return result;
    }
}
