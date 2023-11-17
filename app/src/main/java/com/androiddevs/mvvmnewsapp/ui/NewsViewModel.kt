package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.utils.Resources
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    val app: Application,
    val newsRepository : NewsRepository
):AndroidViewModel(app) {

    val TAG = "NEWSVIEWMODEL"

    // for Breaking News fragment
    val BreakingNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var BreakingNewsPage =1
    var BreakingNewsResponse : NewsResponse?= null

    init {

        getBreakingNews("in")
    }

    // for search News fragment
    val searchNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var SearchNewsPage =1
    var SearchNewsResponse: NewsResponse?= null


    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)

    }

    fun getSearchedNews(searchQuery: String) = viewModelScope.launch {
       safeSearchNewsCall(searchQuery)

    }



    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                BreakingNewsPage++
                if(BreakingNewsResponse == null){
                    BreakingNewsResponse = resultResponse
                }else{
                    val oldArticles = BreakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resources.Success(BreakingNewsResponse?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resources<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                SearchNewsPage++
                if(SearchNewsResponse == null){
                    SearchNewsResponse = resultResponse
                }else{
                    val oldArticles = SearchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resources.Success(SearchNewsResponse?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticles(article : Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


    private suspend fun safeBreakingNewsCall(countryCode: String){
        BreakingNews.postValue(Resources.Loading())
        try {
            if(hasInternetConnection()){
            val response = newsRepository.getBreakingNews(countryCode,BreakingNewsPage)
            BreakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                BreakingNews.postValue(Resources.Error("No Internet Connection"))
            }

        }catch(t: Throwable){
            when(t){
                is IOException -> BreakingNews.postValue(Resources.Error("Network Failure"))
                else -> BreakingNews.postValue(Resources.Error("COnversion ERROR"))
            }
        }
    }
    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resources.Loading())
        try {
            if(hasInternetConnection()){
            val response = newsRepository.getSearchNews(searchQuery,SearchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resources.Error("No Internet Connection"))
            }

        }catch(t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resources.Error("Network Failure"))
                else -> searchNews.postValue(Resources.Error("COnversion ERROR"))
            }
        }
    }



    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork =connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)-> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) ->true
                capabilities.hasTransport(TRANSPORT_ETHERNET) ->true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI ->true
                    TYPE_MOBILE ->true
                    TYPE_ETHERNET ->true
                    else ->false
                }
            }
        }
        return false
    }

}