package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.models.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article) : Long

    @Query("SELECT * FROM article_table")
    // this fun is not a suspend because the suspend dont work with live data
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}