package com.georgcantor.wallpaperapp.view.activity.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.georgcantor.wallpaperapp.MyApplication
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.repository.Repository
import com.georgcantor.wallpaperapp.util.Constants.ERROR_504
import com.georgcantor.wallpaperapp.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SearchViewModel(
    app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isNetworkAvailable = MutableLiveData<Boolean>()
    val isProgressVisible = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val pictures = MutableLiveData<MutableList<CommonPic>>()
    val isSearchingActive = MutableLiveData<Boolean>().apply { value = false }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            ERROR_504 -> error.postValue(context.getString(R.string.no_internet))
            else -> error.postValue(throwable.message)
        }
        isProgressVisible.postValue(false)
    }

    fun getPictures(query: String, index: Int) {
        isSearchingActive.value = true

        viewModelScope.launch(exceptionHandler) {
            val response = repository.getPixabayPictures(query, index)
            pictures.postValue(response)
            isProgressVisible.postValue(false)
        }
        isNetworkAvailable.value = context.isNetworkAvailable()
    }
}