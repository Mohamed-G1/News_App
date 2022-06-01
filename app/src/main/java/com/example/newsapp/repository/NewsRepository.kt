package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDB
import com.example.newsapp.models.Article
import javax.inject.Inject

class NewsRepository(

    @Inject
    val db: ArticleDB

) {


    // for get news
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    // for search news
    suspend fun getSearchedNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)


    // handle data base fun from dao
    suspend fun insertArticle(article: Article) = db.getArticleDao().insert(article)
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    // and this is not suspend bez it returns live data
    fun getAllNews() = db.getArticleDao().getAllArticles()


}