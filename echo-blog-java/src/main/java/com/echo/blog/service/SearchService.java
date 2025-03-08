package com.echo.blog.service;

import com.echo.blog.model.vo.SearchResultVO;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 使用关键词搜索
     *
     * @param keywords 关键词
     * @param count 结果数量
     * @return 搜索结果列表
     */
    List<SearchResultVO> search(String keywords, int count);
} 