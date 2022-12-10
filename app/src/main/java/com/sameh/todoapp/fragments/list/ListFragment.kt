package com.sameh.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sameh.todoapp.R
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.data.viewmodel.ToDoViewModel
import com.sameh.todoapp.databinding.FragmentListBinding
import com.sameh.todoapp.fragments.SharedViewModel
import com.sameh.todoapp.fragments.list.adapter.ToDoRecyclerView

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentListBinding

    private val recyclerViewAdapter: ToDoRecyclerView by lazy { ToDoRecyclerView() }

    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setMenu()

        setupRecyclerView()

        toDoViewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            if (it != null) {
                recyclerViewAdapter.setToDoList(it)
                binding.recyclerView.scheduleLayoutAnimation()
            }
        }

        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabaseViews(it)
        }

        binding.floatingActionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        swipeToDelete(binding.recyclerView)

        /*
         for animation
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 200
        }
         */
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                // for search in database
                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_priority_high -> {
                        sortDataByHighPriority()
                    }
                    R.id.menu_priority_low -> {
                        sortDataByLowPriority()
                    }
                    R.id.menu_delete_all -> {
                        confirmDeleteAllData()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun confirmDeleteAllData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove everything!")
        builder.setMessage("Are you sure to remove everything?")

        builder.setPositiveButton("Yes") { _, _ ->
            toDoViewModel.deleteAllData()
            Toast.makeText(requireContext(), "Everything removed successfully!", Toast.LENGTH_SHORT)
                .show()
        }

        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.noDataImage.visibility = View.VISIBLE
        } else {
            binding.noDataImage.visibility = View.INVISIBLE
        }
    }

    // for search in database
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchFromDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchFromDatabase(newText)
        }
        return true
    }

    private fun searchFromDatabase(query: String) {
        val searchQuery = "%$query%"

        toDoViewModel.searchOnDatabase(searchQuery).observe(viewLifecycleOwner) {
            recyclerViewAdapter.setToDoList(it)
        }
    }

    // sort data
    private fun sortDataByHighPriority() {
        toDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
            recyclerViewAdapter.setToDoList(it)
        }
    }

    private fun sortDataByLowPriority() {
        toDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
            recyclerViewAdapter.setToDoList(it)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = recyclerViewAdapter.toDoDataList[viewHolder.adapterPosition]
                toDoViewModel.deleteData(itemToDelete)
                recyclerViewAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view,
            "${deletedItem.title} deleted",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            toDoViewModel.insertData(deletedItem)
            //toDoViewModel.getAllData()
        }
        snackBar.show()
    }

    /*
private fun swipeToDelete2(recyclerView: RecyclerView) {
    val swipeToDeleteCallback = object : SwipeToDelete() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val itemToDelete = recyclerViewAdapter.toDoDataList[viewHolder.adapterPosition]
            val position = viewHolder.adapterPosition

            toDoViewModel.deleteData(itemToDelete)
            recyclerViewAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            toDoViewModel.getAllData()

            val snackBar = Snackbar.make(
                viewHolder.itemView,
                "${itemToDelete.title} deleted",
                Snackbar.LENGTH_SHORT
            )
            snackBar.setAction("Undo") {
                toDoViewModel.insertData(itemToDelete)
                recyclerViewAdapter.notifyItemChanged(position)
                toDoViewModel.getAllData()
            }
            snackBar.show()
        }
    }
    val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)
}
 */

}