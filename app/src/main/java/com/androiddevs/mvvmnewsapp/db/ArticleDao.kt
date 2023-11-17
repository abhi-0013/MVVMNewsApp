package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(articles: Article): Long

    @Query("SELECT * FROM articles")
    fun getALlArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(articles: Article)
}