package com.echo.blog.controller;

import com.echo.blog.common.api.ApiResult;
import com.echo.blog.model.vo.HotspotVO;
import com.echo.blog.service.HotspotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 热点控制器
 */
@RestController
@RequestMapping("/api/hotspot")
@RequiredArgsConstructor
@Tag(name = "热点", description = "热点相关接口")
public class HotspotController {
    
    private final HotspotService hotspotService;
    
    /**
     * 获取热点列表
     *
     * @param source 热点来源
     * @param count 数量
     * @return 热点列表
     */
    @GetMapping("/{source}")
    @Operation(summary = "获取热点列表")
    public ApiResult<List<HotspotVO>> getHotspots(
            @PathVariable String source,
            @RequestParam(required = false, defaultValue = "20") int count) {
        return ApiResult.success(hotspotService.getHotspots(source, count));
    }
} 