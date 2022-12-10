package com.sameh.todoapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sameh.todoapp.data.models.ToDoData

@Dao
interface ToDoDao {

     @Query("select * from todo_table order by id asc")
     fun getAllData(): LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    @Update
    suspend fun updateData(toDoData: ToDoData)

    @Delete
    suspend fun deleteData(toDoData: ToDoData)

    @Query("delete from todo_table")
    suspend fun deleteAllData()

    @Query("select * from todo_table where title like :toDoTitle")
    fun searchOnDatabase(toDoTitle: String): LiveData<List<ToDoData>>

    @Query("select * from todo_table order by case when priority like 'H%' then 1 when priority like 'M%' then 2 when priority like 'L%' then 3 end")
    fun sortByHighPriority(): LiveData<List<ToDoData>>

    @Query("select * from todo_table order by case when priority like 'L%' then 1 when priority like 'M%' then 2 when priority like 'H%' then 3 end")
    fun sortByLowPriority(): LiveData<List<ToDoData>>

}