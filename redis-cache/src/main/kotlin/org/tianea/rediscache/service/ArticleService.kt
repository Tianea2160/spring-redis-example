package org.tianea.rediscache.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.tianea.rediscache.entity.Article
import org.tianea.rediscache.repository.ArticleRepository
import java.util.*

@Service
class ArticleService(
    private val articleRepository: ArticleRepository
) {

    @Cacheable(value = ["articles"], key = "#id", unless = "#result == null")
    fun getArticleById(id: Long): Article? {
        println("Fetching article from database, id: $id")
        return articleRepository.findById(id).orElse(null)
    }

    fun getAllArticles(): List<Article> {
        return articleRepository.findAll()
    }

    fun getPublishedArticles(): List<Article> {
        return articleRepository.findByIsPublishedTrue()
    }

    fun getArticlesByAuthor(author: String): List<Article> {
        return articleRepository.findByAuthor(author)
    }

    fun searchArticlesByTitle(title: String): List<Article> {
        return articleRepository.findByTitleContaining(title)
    }

    @CachePut(value = ["articles"], key = "#result.id")
    fun saveArticle(article: Article): Article {
        return articleRepository.save(article)
    }

    @CachePut(value = ["articles"], key = "#id")
    fun updateArticle(id: Long, articleDetails: Article): Article {
        val article = articleRepository.findById(id).orElseThrow {
            NoSuchElementException("Article not found with id: $id")
        }

        article.title = articleDetails.title
        article.content = articleDetails.content
        article.author = articleDetails.author
        article.isPublished = articleDetails.isPublished

        return articleRepository.save(article)
    }

    @CacheEvict(value = ["articles"], key = "#id")
    fun deleteArticle(id: Long) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Article not found with id: $id")
        }
    }

    @CacheEvict(value = ["articles"], allEntries = true)
    fun clearCache() {
        println("Clearing all articles from cache")
    }
}
