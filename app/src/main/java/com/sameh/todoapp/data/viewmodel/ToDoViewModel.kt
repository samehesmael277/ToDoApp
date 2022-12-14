package com.sameh.todoapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sameh.todoapp.constants.SortingData
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.data.repo.ToDoRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(private val repository: ToDoRepositoryImp): ViewModel() {

    val getAllData: LiveData<List<ToDoData>> = repository.getAllData
    val sortByHighPriority: LiveData<List<ToDoData>> = repository.sortByHighPriority
    val sortByLowPriority: LiveData<List<ToDoData>> = repository.sortByLowPriority

    private var _queryToSearchOnDatabaseMutableLiveData: MutableLiveData<String?> = MutableLiveData(null)
    val queryToSearchOnDatabaseLiveData: LiveData<String?> get() = _queryToSearchOnDatabaseMutableLiveData

    private var _userSortingTypeMutableLiveData: MutableLiveData<String> = MutableLiveData(
        SortingData.LATEST)
    val userSortingTypeLiveData: LiveData<String> get() = _userSortingTypeMutableLiveData

    fun insertData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(toDoData)
        }
    }

    fun updateData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(toDoData)
        }
    }

    fun deleteData(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(toDoData)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun searchOnDatabase(toDoTitle: String): LiveData<List<ToDoData>> {
        return repository.searchOnDatabase(toDoTitle)
    }

    fun setQueryForSearchOnDataBase(query: String?) {
        _queryToSearchOnDatabaseMutableLiveData.value = query
    }

    fun setUserSortingType(sortingType: String) {
        _userSortingTypeMutableLiveData.value = sortingType
    }

}