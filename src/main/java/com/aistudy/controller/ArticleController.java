package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.ArticleDTO;
import com.aistudy.entity.ArticleHistory;
import com.aistudy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI作文生成控制器
 * 处理文章的生成、查询、导出、删除等操作
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 生成文章
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generateArticle(
            @RequestAttribute("userId") Integer userId,
            @RequestBody ArticleDTO dto) {
        Map<String, Object> result = articleService.generateArticle(
                userId, dto.getTitle(), dto.getRequirements(), dto.getArticleType()
        );
        return Result.success("文章生成成功", result);
    }

    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public Result<List<ArticleHistory>> getList(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<ArticleHistory> list = articleService.getList(userId, page, pageSize);
        return Result.success(list);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleHistory> getDetail(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer articleId) {
        ArticleHistory article = articleService.getDetail(userId, articleId);
        return Result.success(article);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer articleId) {
        articleService.deleteArticle(userId, articleId);
        return Result.success("删除成功");
    }

    /**
     * 导出Markdown
     */
    @GetMapping("/{id}/export/md")
    public ResponseEntity<byte[]> exportMarkdown(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer articleId) {
        ArticleHistory article = articleService.getDetail(userId, articleId);
        String mdContent = articleService.exportMarkdown(userId, articleId);
        byte[] bytes = mdContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = article.getTitle() + ".md";
        String encodedFilename = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .body(bytes);
    }

    /**
     * 导出Word
     */
    @GetMapping("/{id}/export/docx")
    public ResponseEntity<byte[]> exportWord(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer articleId) {
        ArticleHistory article = articleService.getDetail(userId, articleId);
        byte[] bytes = articleService.exportWord(userId, articleId);
        String filename = article.getTitle() + ".docx";
        String encodedFilename = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(bytes);
    }
}
