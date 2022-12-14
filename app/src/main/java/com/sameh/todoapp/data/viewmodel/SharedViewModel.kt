package com.sameh.todoapp.data.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sameh.todoapp.R
import com.sameh.todoapp.constants.Tag
import com.sameh.todoapp.data.models.Priority
import com.sameh.todoapp.data.models.ToDoData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val context: Application) : ViewModel() {

    private var _emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)
    val emptyDatabase: LiveData<Boolean> get() = _emptyDatabase

    fun checkIfDatabaseIsEmpty(toDoDataList: List<ToDoData>) {
        _emptyDatabase.value = toDoDataList.isEmpty()
    }

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }
                1 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.yellow
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }
                2 -> {
                    try {
                        (parent?.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.green
                            )
                        )
                    } catch (e: Exception) {
                        Log.d(Tag.TAG, e.message.toString())
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    fun verifyDataFromUser(title: String, description: String): Boolean {
        return !(title.isEmpty() || description.isEmpty())
    }

    fun parseToPriority(priority: String): Priority {
        return when (priority) {
            "High Priorities",
            "أولويات عالية" -> {
                Priority.HIGH
            }
            "Medium Priorities",
            "أولويات متوسطة" -> {
                Priority.MEDIUM
            }
            "Low Priorities",
            "أولويات منخفضة" -> {
                Priority.LOW
            }
            else -> {
                Priority.HIGH
            }
        }
    }

    fun parsePriority(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

}