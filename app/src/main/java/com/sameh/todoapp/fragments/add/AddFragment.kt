package com.sameh.todoapp.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.sameh.todoapp.R
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.data.viewmodel.ToDoViewModel
import com.sameh.todoapp.databinding.FragmentAddBinding
import com.sameh.todoapp.data.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    private val toDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_add -> {
                        insertDataToDatabase()
                    }
                    android.R.id.home -> {
                        findNavController().navigate(R.id.action_addFragment_to_listFragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.spinnerAddPriorities.onItemSelectedListener = sharedViewModel.listener
    }

    private fun insertDataToDatabase() {
        val title = binding.edAddTitle.text.toString()
        val priority = binding.spinnerAddPriorities.selectedItem.toString()
        val description = binding.edAddDescription.text.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val newToDoData = ToDoData(
                0,
                title,
                sharedViewModel.parseToPriority(priority),
                description
            )

            toDoViewModel.insertData(newToDoData)
            Toast.makeText(requireContext(), getString(R.string.added_successfully), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), getString(R.string.please_fill_out_all_fields), Toast.LENGTH_SHORT)
                .show()
        }
    }

}