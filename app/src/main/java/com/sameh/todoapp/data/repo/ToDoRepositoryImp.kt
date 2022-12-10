package com.sameh.todoapp.data.repo

import androidx.lifecycle.LiveData
import com.sameh.todoapp.data.database.ToDoDao
import com.sameh.todoapp.data.models.ToDoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToDoRepositoryImp(private val toDoDao: ToDoDao) : ToDoRepository {

    override val getAllData: LiveData<List<ToDoData>>
        get() = toDoDao.getAllData()

    override val sortByHighPriority: LiveData<List<ToDoData>>
        get() = toDoDao.sortByHighPriority()

    override val sortByLowPriority: LiveData<List<ToDoData>>
        get() = toDoDao.sortByLowPriority()


    override suspend fun insertData(toDoData: ToDoData) {
        withContext(Dispatchers.IO) {
            toDoDao.insertData(toDoData)
        }
    }

    override suspend fun updateData(toDoData: ToDoData) {
        withContext(Dispatchers.IO) {
            toDoDao.updateData(toDoData)
        }
    }

    override suspend fun deleteData(toDoData: ToDoData) {
        withContext(Dispatchers.IO) {
            toDoDao.deleteData(toDoData)
        }
    }

    override suspend fun deleteAllData() {
        withContext(Dispatchers.IO) {
            toDoDao.deleteAllData()
        }
    }

    override fun searchOnDatabase(toDoTitle: String): LiveData<List<ToDoData>> {
        return toDoDao.searchOnDatabase(toDoTitle)
    }

}