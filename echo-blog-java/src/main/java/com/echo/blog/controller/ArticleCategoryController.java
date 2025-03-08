package com.echo.blog.controller;

import com.echo.blog.common.api.Result;
import com.echo.blog.entity.ArticleCategory;
import com.echo.blog.service.ArticleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文章分类管理", description = "文章分类相关接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ArticleCategoryController {

    private final ArticleCategoryService categoryService;

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<ArticleCategory> createCategory(@RequestBody @Valid ArticleCategory category) {
        if (categoryService.existsByName(category.getName())) {
            return Result.failed("分类名称已存在");
        }
        return Result.success(categoryService.createCategory(category));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<ArticleCategory> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid ArticleCategory category) {
        category.setId(id);
        return Result.success(categoryService.updateCategory(category));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<ArticleCategory> getCategory(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<Page<ArticleCategory>> getCategories(Pageable pageable) {
        return Result.success(categoryService.getAllCategories(pageable));
    }

    @Operation(summary = "获取用户创建的分类")
    @GetMapping("/user/{createdBy}")
    public Result<Page<ArticleCategory>> getCategoriesByCreator(
            @PathVariable String createdBy,
            Pageable pageable) {
        return Result.success(categoryService.getCategoriesByCreator(createdBy, pageable));
    }
}