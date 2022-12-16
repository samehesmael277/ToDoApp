package com.sameh.todoapp.fragments.list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sameh.todoapp.R
import com.sameh.todoapp.data.models.Priority
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.databinding.ItemOnRecyclerViewBinding
import com.sameh.todoapp.fragments.list.ListFragmentDirections

class ToDoRecyclerViewAdapter : RecyclerView.Adapter<ToDoRecyclerViewAdapter.ToDoViewHolder>() {

    var toDoDataList = emptyList<ToDoData>()

    private var toDoDiffUtil: ToDoDiffUtil? = null

    fun setToDoList(toDoList: List<ToDoData>) {
        toDoDiffUtil = ToDoDiffUtil(toDoDataList, toDoList)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil!!)
        this.toDoDataList = toDoList
        toDoDiffResult.dispatchUpdatesTo(this)
    }

    class ToDoViewHolder(private val binding: ItemOnRecyclerViewBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        fun bind(toDoData: ToDoData) {
            binding.tvItemTitle.text = toDoData.title
            binding.tvItemDescription.text = toDoData.description

            // to go to update fragment
            binding.constraintLayoutItemContainer.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToUpdateFragment(toDoData)
                itemView.findNavController().navigate(action)
            }

            when (toDoData.priority) {
                Priority.HIGH -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.red)
                }

                Priority.MEDIUM -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.yellow)
                }

                Priority.LOW -> {
                    binding.tvViewPriorityIndicator.backgroundTintList =
                        ContextCompat.getColorStateList(itemView.context, R.color.green)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            ItemOnRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val toDoData: ToDoData = toDoDataList[position]
        holder.bind(toDoData)
    }

    override fun getItemCount(): Int {
        return toDoDataList.size
    }
}