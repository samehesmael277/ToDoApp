package com.sameh.todoapp.di

import android.content.Context
import androidx.room.Room
import com.sameh.todoapp.data.database.ToDoDao
import com.sameh.todoapp.data.database.ToDoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("nameOfDatabase")
    fun provideDataBaseName(): String = "todo_database"

    @Singleton
    @Provides
    fun provideToDoDataBase(@ApplicationContext context: Context, @Named("nameOfDatabase") name: String): ToDoDatabase =
        Room.databaseBuilder(context, ToDoDatabase::class.java, name).build()

    @Singleton
    @Provides
    fun provideToDoDao(db: ToDoDatabase): ToDoDao = db.toDoDao()

}