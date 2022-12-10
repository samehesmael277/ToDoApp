package com.sameh.todoapp.data.repo

import androidx.lifecycle.LiveData
import com.sameh.todoapp.data.models.ToDoData

interface ToDoRepository {

    val getAllData: LiveData<List<ToDoData>>

    val sortByHighPriority: LiveData<List<ToDoData>>

    val sortByLowPriority: LiveData<List<ToDoData>>

    suspend fun insertData(toDoData: ToDoData)

    suspend fun updateData(toDoData: ToDoData)

    suspend fun deleteData(toDoData: ToDoData)

    suspend fun deleteAllData()

    fun searchOnDatabase(toDoTitle: String): LiveData<List<ToDoData>>

}