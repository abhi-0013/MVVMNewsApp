package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

class NewsRepository(
    val db : ArticleDatabase
) {
    suspend fun getBreakingNews(CountryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(CountryCode,pageNumber)

    suspend fun getSearchNews(stringQuery: String, searchPageNumber: Int) =
        RetrofitInstance.api.searchNews(stringQuery,searchPageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getALlArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticles(article)


}