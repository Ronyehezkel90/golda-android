package com.example.golda.editor.modifier.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.golda.R
import com.example.golda.editor.modifier.ModifierActivity
import com.example.golda.editor.modifier.ModifierAdapter
import com.example.golda.model.UserItem
import kotlinx.android.synthetic.main.fragment_edit_users.*


class UsersEditFragment : Fragment() {

    val usersModifyAdapter: ModifierAdapter =
        ModifierAdapter("user")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_users, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ModifierActivity).presenter.getUsers()
        add_item_button.setOnClickListener {
            userButtonClick()
        }
        remove_item_button.setOnClickListener {
            removeButtonClick()
        }
        editor_recycler_view.layoutManager = LinearLayoutManager(activity)
        editor_recycler_view.adapter = usersModifyAdapter
    }

    private fun removeButtonClick() {
        val user = usersModifyAdapter.chosenItem as UserItem
            (activity as ModifierActivity).presenter.removeUser(user._id)?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getUsers()
            }
    }

    private fun userButtonClick() {
        if (spinner_roles.selectedItem != "Role") {
            (activity as ModifierActivity).presenter.addUser(
                first_edit_text.text.toString(),
                second_edit_text.text.toString(),
                spinner_roles.selectedItem.toString()
            )?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getUsers()
            }

        }
    }

    fun update(usersList: MutableList<UserItem>) {
        val anyList = mutableListOf<Any>()
        for (item in usersList){
            anyList.add(item)
        }
        usersModifyAdapter.updateReviews(anyList)
    }


}