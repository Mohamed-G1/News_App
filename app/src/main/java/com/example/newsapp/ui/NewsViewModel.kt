package com.example.newsapp.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.utils.BaseApplication
import com.example.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class NewsViewModel(
    application: Application,
    @Inject
    val newsRepository: NewsRepository

) : AndroidViewModel(application) { // we inhert from AndroidViewModel instead of viewmodel to can use context

    // this MutableLiveData is kind from resource bez resource is wrap our network calls
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1

    //pagination
    var breakingNewsResponse: NewsResponse? = null


    // this for search news
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    //pagination
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }


    // this fun to execute api call from repo
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)

    }

    // fun for search news
    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }


    // this fun to handle call response if the response success or there is error
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // handle pagination
                // increase page number every time we get response
                breakingNewsPage++
                // and if that the first response ever we set resultResponse to breakingNewsResponse
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                }
                // and if we loaded more than one page we get old items(oldArticle) and add the(newArticle)
                //to that list->(oldArticle)
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                // if this is not breakingNewsResponse return resultResponse instead
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // handle searched call
    private fun handleSearchedNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                // handle pagination
                // increase page number every time we get response
                searchNewsPage++
                // and if that the first response ever we set resultResponse to breakingNewsResponse
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                }
                // and if we loaded more than one page we get old items(oldArticle) and add the(newArticle)
                //to that list->(oldArticle)
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                // if this is not searchNewsResponse return resultResponse instead
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // handle db fun coming from repo
    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getAllNews() = newsRepository.getAllNews()


    // this fun to safe network call from error exception
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())

        try {
            // if we have a internet connection do this
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                // here we pass a new value
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            // if there no internet connection
            else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                // IOException can happens from retrofit
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                // if the error from json
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }

        }
    }


    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            // if we have a internet connection do this
            if (hasInternetConnection()) {
                val response = newsRepository.getSearchedNews(searchQuery, searchNewsPage)
                // here we pass a new value
                searchNews.postValue(handleBreakingNewsResponse(response))
            }
            // if there no internet connection
            else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                // IOException can happens from retrofit
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                // if the error from json
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }

        }
    }


    // this fun to check internet connection
    @SuppressLint("ObsoleteSdkInt")
    fun hasInternetConnection(): Boolean {
        // this used to detect if user is currently connect with internet or not
        val connectivityManager = getApplication<BaseApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
