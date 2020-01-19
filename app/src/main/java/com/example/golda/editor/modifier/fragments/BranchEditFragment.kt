package com.example.golda.editor.modifier.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.editor.modifier.ModifierActivity
import com.example.golda.editor.modifier.ModifierAdapter
import com.example.golda.model.BranchItem
import com.example.golda.model.TopicItem
import com.example.golda.model.UserItem
import kotlinx.android.synthetic.main.fragment_edit_users.*

class BranchEditFragment : Fragment() {

    private lateinit var usersList: MutableList<UserItem>
    val branchModifyAdapter: ModifierAdapter = ModifierAdapter("branch")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_users, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ModifierActivity).presenter.getBranches()
        add_item_button.setOnClickListener {
            addButtonClick()
        }
        remove_item_button.setOnClickListener {
            removeButtonClick()
        }
        editor_recycler_view.layoutManager = LinearLayoutManager(activity)
        editor_recycler_view.adapter = branchModifyAdapter


        first_edit_text.hint = "name"
        second_edit_text.hint = "address"
        third_edit_text.visibility = View.VISIBLE
        third_edit_text.hint = "phone"
        (activity as ModifierActivity).presenter.getUsers("branchesFragment")
    }

    private fun removeButtonClick() {
        val topicItem = branchModifyAdapter.chosenItem as TopicItem
        (activity as ModifierActivity).presenter.removeTopic(topicItem._id)
            ?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getTopics()
            }

    }

    private fun addButtonClick() {
        if (first_edit_text.text.toString() != "" && second_edit_text.text.toString() != "" && third_edit_text.text.toString() != "" ) {
            (activity as ModifierActivity).presenter.addBranch(first_edit_text.text.toString(), second_edit_text.text.toString(), third_edit_text.text.toString(), usersList[spinner_roles.selectedItemPosition]._id
            )?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getBranches()
            }
        }
    }

    fun update(branchesList: MutableList<BranchItem>) {
        val anyList = mutableListOf<Any>()
        for (item in branchesList) {
            anyList.add(item)
        }
        branchModifyAdapter.updateReviews(anyList)
    }

    fun updateUsersSpinner(usersList: MutableList<UserItem>) {
        this.usersList = usersList
        val adapter = ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        )
        adapter.clear()
        for (user in usersList) {
            adapter.add(user.name)
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_roles.adapter = adapter
    }

}