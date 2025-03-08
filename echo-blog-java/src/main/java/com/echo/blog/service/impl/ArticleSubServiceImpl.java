package com.echo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echo.blog.entity.ArticleSub;
import com.echo.blog.mapper.ArticleSubMapper;
import com.echo.blog.service.ArticleSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章子表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleSubServiceImpl extends ServiceImpl<ArticleSubMapper, ArticleSub> implements ArticleSubService {

    @Override
    public List<ArticleSub> getByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleSub> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleSub::getArticleId, articleId);
        return list(wrapper);
    }

    @Override
    public ArticleSub getByArticleIdAndSubType(Long articleId, String subType) {
        LambdaQueryWrapper<ArticleSub> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleSub::getArticleId, articleId)
                .eq(ArticleSub::getSubType, subType);
        return getOne(wrapper);
    }
} 