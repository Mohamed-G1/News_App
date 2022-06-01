package com.example.newsapp.models

data class NewsResponse(
    // we change List to MutableList to can addAll() -> new list in oldArticle in view model
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)