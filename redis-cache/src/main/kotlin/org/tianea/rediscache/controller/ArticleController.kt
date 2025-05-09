package org.tianea.rediscache.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.tianea.rediscache.entity.Article
import org.tianea.rediscache.service.ArticleService
import java.util.*

@RestController
@RequestMapping("/api/articles")
class ArticleController(private val articleService: ArticleService) {

    @GetMapping
    fun getAllArticles(): ResponseEntity<List<Article>> {
        return ResponseEntity.ok(articleService.getAllArticles())
    }

    @GetMapping("/published")
    fun getPublishedArticles(): ResponseEntity<List<Article>> {
        return ResponseEntity.ok(articleService.getPublishedArticles())
    }

    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<Article> {
        val article = articleService.getArticleById(id)
        return if (article != null) {
            ResponseEntity.ok(article)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/author/{author}")
    fun getArticlesByAuthor(@PathVariable author: String): ResponseEntity<List<Article>> {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(author))
    }

    @GetMapping("/search")
    fun searchArticles(@RequestParam title: String): ResponseEntity<List<Article>> {
        return ResponseEntity.ok(articleService.searchArticlesByTitle(title))
    }

    @PostMapping
    fun createArticle(@RequestBody article: Article): ResponseEntity<Article> {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.saveArticle(article))
    }

    @PutMapping("/{id}")
    fun updateArticle(
        @PathVariable id: Long,
        @RequestBody articleDetails: Article
    ): ResponseEntity<Article> {
        return try {
            val updatedArticle = articleService.updateArticle(id, articleDetails)
            ResponseEntity.ok(updatedArticle)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            articleService.deleteArticle(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/clear-cache")
    fun clearCache(): ResponseEntity<String> {
        articleService.clearCache()
        return ResponseEntity.ok("Cache cleared successfully")
    }
}
