package com.example.android_mymedia.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android_mymedia.home.data.PlayListModel
import com.example.android_mymedia.home.repository.HomeRepository
import com.example.android_mymedia.home.repository.HomeRepositoryImpl
import com.example.android_mymedia.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _categoryList: MutableLiveData<List<PlayListModel>> = MutableLiveData()
    val categoryList: LiveData<List<PlayListModel>> get() = _categoryList

    private val _pageToken: MutableLiveData<String?> = MutableLiveData()
    val pageToken: LiveData<String?> get() = _pageToken

    init {
        setPopularList() //이걸 주석하고 디버깅을 하면 홈 화면 API 사용 x
    }

    private fun setPopularList() {
        viewModelScope.launch {
            val token = pageToken.value
            val response = repository.getPopularVideo(token)
            val list = response.first
            val nextToken = response.second
            var currentList = categoryList.value.orEmpty().toMutableList()

            currentList = list.toMutableList()

            _pageToken.value = nextToken
            _categoryList.value = currentList
        }
    }

    private fun setToken(setToken: String?) {
        var token = pageToken.value

        token = setToken

        _pageToken.value = token
    }
}


class HomeViewModelFactory(

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                HomeRepositoryImpl(RetrofitClient)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}
