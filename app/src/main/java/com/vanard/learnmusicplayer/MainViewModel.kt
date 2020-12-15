package com.vanard.learnmusicplayer

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    application: Application,
    private val dataStoreRepo: DataStoreRepo
): AndroidViewModel(application) {

    val readSelectedSortBy = dataStoreRepo.readSelectedSortBy.asLiveData()

    fun saveSelectedSortBy(sortBy: String) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepo.saveSelectedSortBy(sortBy)
        }

}