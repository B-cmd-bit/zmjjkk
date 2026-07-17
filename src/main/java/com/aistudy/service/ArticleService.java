package com.aistudy.service;

import com.aistudy.entity.ArticleHistory;
import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 */
public interface ArticleService {
    Map<String, Object> generateArticle(Integer userId, String title, String requirements, String articleType);
    List<ArticleHistory> getList(Integer userId, int page, int pageSize);
    ArticleHistory getDetail(Integer userId, Integer articleId);
    void deleteArticle(Integer userId, Integer articleId);
    String exportMarkdown(Integer userId, Integer articleId);
    byte[] exportWord(Integer userId, Integer articleId);
    List<ArticleHistory> listAllArticles(int page, int pageSize);
    long countTotalArticles();
    void deleteArticleByAdmin(Integer articleId);
}
