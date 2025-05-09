package org.tianea.rediscache.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tianea.rediscache.entity.Article

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findByAuthor(author: String): List<Article>
    fun findByTitleContaining(title: String): List<Article>
    fun findByIsPublishedTrue(): List<Article>
}
