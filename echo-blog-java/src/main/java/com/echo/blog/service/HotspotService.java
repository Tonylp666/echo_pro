package com.echo.blog.service;

import com.echo.blog.model.vo.HotspotVO;

import java.util.List;

/**
 * 热点服务接口
 */
public interface HotspotService {
    
    /**
     * 获取热点列表
     *
     * @param source 热点来源
     * @param count 数量
     * @return 热点列表
     */
    List<HotspotVO> getHotspots(String source, int count);
} 