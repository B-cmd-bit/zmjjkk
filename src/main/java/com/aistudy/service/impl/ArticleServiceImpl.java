package com.aistudy.service.impl;

import com.aistudy.entity.ArticleHistory;
import com.aistudy.mapper.ArticleMapper;
import com.aistudy.service.AIService;
import com.aistudy.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private AIService aiService;

    @Override
    @Transactional
    public Map<String, Object> generateArticle(Integer userId, String title, String requirements, String articleType) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        // 调用AI生成文章
        String content = aiService.generateArticle(title, requirements, articleType);
        // 保存到数据库
        ArticleHistory article = new ArticleHistory();
        article.setUserId(userId);
        article.setTitle(title.trim());
        article.setContent(content);
        article.setArticleType(articleType != null ? articleType : "议论文");
        article.setRequirements(requirements != null ? requirements : "");
        article.setCreatedAt(LocalDateTime.now());
        articleMapper.insert(article);
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", article.getId());
        result.put("title", article.getTitle());
        result.put("content", article.getContent());
        result.put("articleType", article.getArticleType());
        result.put("createdAt", article.getCreatedAt().toString());
        return result;
    }

    @Override
    public List<ArticleHistory> getList(Integer userId, int page, int pageSize) {
        Page<ArticleHistory> p = new Page<>(page, pageSize);
        QueryWrapper<ArticleHistory> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("created_at");
        IPage<ArticleHistory> result = articleMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    public ArticleHistory getDetail(Integer userId, Integer articleId) {
        ArticleHistory article = articleMapper.selectById(articleId);
        if (article == null || !Objects.equals(article.getUserId(), userId)) {
            throw new IllegalArgumentException("文章不存在");
        }
        return article;
    }

    @Override
    @Transactional
    public void deleteArticle(Integer userId, Integer articleId) {
        ArticleHistory article = articleMapper.selectById(articleId);
        if (article == null || !Objects.equals(article.getUserId(), userId)) {
            throw new IllegalArgumentException("文章不存在");
        }
        articleMapper.deleteById(articleId);
    }

    @Override
    public String exportMarkdown(Integer userId, Integer articleId) {
        ArticleHistory article = getDetail(userId, articleId);
        StringBuilder md = new StringBuilder();
        md.append("# ").append(article.getTitle()).append("\n\n");
        md.append("> 文章类型：").append(article.getArticleType()).append("\n");
        md.append("> 创建时间：").append(article.getCreatedAt()).append("\n\n");
        md.append("---\n\n");
        md.append(article.getContent());
        return md.toString();
    }

    @Override
    public byte[] exportWord(Integer userId, Integer articleId) {
        ArticleHistory article = getDetail(userId, articleId);
        try (XWPFDocument document = new XWPFDocument()) {
            // 标题
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setText(article.getTitle());
            // 元信息
            XWPFParagraph metaParagraph = document.createParagraph();
            XWPFRun metaRun = metaParagraph.createRun();
            metaRun.setFontSize(10);
            metaRun.setText("文章类型：" + article.getArticleType() + "  创建时间：" + article.getCreatedAt());
            // 空行
            document.createParagraph();
            // 内容（按行分割）
            String[] lines = article.getContent().split("\n");
            for (String line : lines) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setFontSize(12);
                run.setText(line);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            document.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出Word失败：" + e.getMessage());
        }
    }

    @Override
    public List<ArticleHistory> listAllArticles(int page, int pageSize) {
        Page<ArticleHistory> p = new Page<>(page, pageSize);
        QueryWrapper<ArticleHistory> query = new QueryWrapper<>();
        query.orderByDesc("created_at");
        IPage<ArticleHistory> result = articleMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    public long countTotalArticles() {
        return articleMapper.selectCount(null);
    }

    @Override
    @Transactional
    public void deleteArticleByAdmin(Integer articleId) {
        articleMapper.deleteById(articleId);
    }
}
