package com.sameh.todoapp.fragments.list

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sameh.todoapp.R
import com.sameh.todoapp.constants.SortingData
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.data.viewmodel.ToDoViewModel
import com.sameh.todoapp.databinding.FragmentListBinding
import com.sameh.todoapp.data.viewmodel.SharedViewModel
import com.sameh.todoapp.fragments.list.adapter.ToDoRecyclerViewAdapter
import com.sameh.todoapp.utils.hideKeyboard
import com.sameh.todoapp.utils.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import javax.inject.Inject

@AndroidEntryPoint
class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentListBinding

    private val recyclerViewAdapter: ToDoRecyclerViewAdapter by lazy { ToDoRecyclerViewAdapter() }

    private val toDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var searchQuery: String? = null

    private val sortingDataForUser = "sortingBy"
    private lateinit var userSortingFromSharedPreferences: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        readUserSortingFromSharedPreferencesFile()

        toDoViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                SortingData.LATEST -> {
                    getDataByLatest()
                }
                SortingData.HIGHEST -> {
                    getDataByHighest()
                }
                SortingData.LOWEST -> {
                    getDataByLowest()
                }
            }
        }

        hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.queryHint = getString(R.string.search_hint)
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)

                toDoViewModel.queryToSearchOnDatabaseLiveData.observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        try {
                            searchView?.setQuery(it, false)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        toDoViewModel.userSortingTypeLiveData.observe(viewLifecycleOwner) { sort ->
                            when (sort) {
                                SortingData.LATEST -> {
                                    getDataByLatest()
                                }
                                SortingData.HIGHEST -> {
                                    getDataByHighest()
                                }
                                SortingData.LOWEST -> {
                                    getDataByLowest()
                                }
                            }
                        }
                    }
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_priority_latest -> {
                        saveUserSortingOnSharedPreferencesFile(SortingData.LATEST)
                    }
                    R.id.menu_priority_high -> {
                        saveUserSortingOnSharedPreferencesFile(SortingData.HIGHEST)
                    }
                    R.id.menu_priority_low -> {
                        saveUserSortingOnSharedPreferencesFile(SortingData.LOWEST)
                    }
                    R.id.menu_delete_all -> {
                        confirmDeleteAllData()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabaseViews(it)
        }

        binding.floatingActionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!searchQuery.isNullOrEmpty()) {
            outState.putString("query", searchQuery)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val query = savedInstanceState?.getString("query")

        if (!query.isNullOrEmpty()) {
            toDoViewModel.setQueryForSearchOnDataBase(query)
        } else {
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = recyclerViewAdapter
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 200
        }
        swipeToDelete(binding.recyclerView)
    }

    private fun getDataByLatest() {
        toDoViewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            recyclerViewAdapter.setToDoList(it)
        }
    }

    private fun getDataByHighest() {
        toDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            recyclerViewAdapter.setToDoList(it)
        }
    }

    private fun getDataByLowest() {
        toDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseIsEmpty(it)
            recyclerViewAdapter.setToDoList(it)
        }
    }

    private fun confirmDeleteAllData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.remove_everything))
        builder.setMessage(getString(R.string.are_you_sure_to_remove_everything))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            toDoViewModel.deleteAllData()
            Toast.makeText(
                requireContext(),
                getString(R.string.everything_removed_successfully),
                Toast.LENGTH_SHORT
            )
                .show()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
        builder.show()
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.noDataImage.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.noDataImage.visibility = View.INVISIBLE
            binding.tvNoData.visibility = View.INVISIBLE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchQuery = query
            toDoViewModel.setQueryForSearchOnDataBase(query)
            searchFromDatabase(query)
        } else {
            searchQuery = null
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty()) {
            searchQuery = newText
            toDoViewModel.setQueryForSearchOnDataBase(newText)
            searchFromDatabase(newText)
        } else {
            searchQuery = null
            toDoViewModel.setQueryForSearchOnDataBase(null)
        }
        return true
    }

    private fun searchFromDatabase(query: String) {
        if (query.isNotEmpty()) {
            val searchQuery = "%$query%"

            toDoViewModel.searchOnDatabase(searchQuery).observeOnce(viewLifecycleOwner) { list ->
                list?.let {
                    recyclerViewAdapter.setToDoList(it)
                }
            }
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = recyclerViewAdapter.toDoDataList[viewHolder.adapterPosition]
                toDoViewModel.deleteData(itemToDelete)
                recyclerViewAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                recyclerViewAdapter.notifyItemRangeRemoved(
                    0,
                    recyclerViewAdapter.toDoDataList.size - 1
                )
                toDoViewModel.setUserSortingType(userSortingFromSharedPreferences)
                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view,
            "'${deletedItem.title}' ${getString(R.string.deleted)}",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction(getString(R.string.undo)) {
            toDoViewModel.insertData(deletedItem)
            toDoViewModel.setUserSortingType(userSortingFromSharedPreferences)
        }
        snackBar.show()
    }

    private fun saveUserSortingOnSharedPreferencesFile(sortingData: String) {
        try {
            val editor = sharedPreferences.edit()

            when (sortingData) {
                SortingData.LATEST -> {
                    editor.putString(sortingDataForUser, SortingData.LATEST)
                    editor.apply()
                    readUserSortingFromSharedPreferencesFile()
                }
                SortingData.HIGHEST -> {
                    editor.putString(sortingDataForUser, SortingData.HIGHEST)
                    editor.apply()
                    readUserSortingFromSharedPreferencesFile()
                }
                SortingData.LOWEST -> {
                    editor.putString(sortingDataForUser, SortingData.LOWEST)
                    editor.apply()
                    readUserSortingFromSharedPreferencesFile()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun readUserSortingFromSharedPreferencesFile() {
        try {
            userSortingFromSharedPreferences =
                sharedPreferences.getString(sortingDataForUser, SortingData.LATEST).toString()

            toDoViewModel.setUserSortingType(userSortingFromSharedPreferences)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

}