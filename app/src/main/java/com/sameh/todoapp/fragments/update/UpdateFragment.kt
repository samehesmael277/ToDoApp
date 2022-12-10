package com.sameh.todoapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sameh.todoapp.R
import com.sameh.todoapp.data.models.ToDoData
import com.sameh.todoapp.data.viewmodel.ToDoViewModel
import com.sameh.todoapp.databinding.FragmentUpdateBinding
import com.sameh.todoapp.fragments.SharedViewModel

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        setMenu()

        setCurrentDataToUpdateFragment()
    }

    private fun setCurrentDataToUpdateFragment() {
        try {
            binding.edAddTitle.setText(args.currentItem.title)
            binding.edAddDescription.setText(args.currentItem.description)
            binding.spinnerAddPriorities.setSelection(sharedViewModel.parsePriority(args.currentItem.priority))
            binding.spinnerAddPriorities.onItemSelectedListener = sharedViewModel.listener
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        updateCurrentData()
                        true
                    }
                    R.id.menu_delete -> {
                        confirmRemoveData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateCurrentData() {
        val title = binding.edAddTitle.text.toString()
        val description = binding.edAddDescription.text.toString()
        val priority = binding.spinnerAddPriorities.selectedItem.toString()

        val validation = sharedViewModel.verifyDataFromUser(title, description)
        if (validation) {
            val newItem = ToDoData(
                args.currentItem.id,
                title,
                sharedViewModel.parseToPriority(priority),
                description
            )

            toDoViewModel.updateData(newItem)
            Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun confirmRemoveData() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove '${args.currentItem.title}'!")
        builder.setMessage("Are you sure to remove '${args.currentItem.title}'?")

        builder.setPositiveButton("Yes") { _, _ ->
            toDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(), "'${args.currentItem.title}' removed!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }
}