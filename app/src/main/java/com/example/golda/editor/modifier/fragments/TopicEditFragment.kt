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
import com.example.golda.model.TopicItem
import kotlinx.android.synthetic.main.fragment_edit_users.*


class TopicEditFragment : Fragment() {

    val topicsModifyAdapter: ModifierAdapter =
        ModifierAdapter("topic")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit_users, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ModifierActivity).presenter.getTopics()
        add_item_button.setOnClickListener {
            addButtonClick()
        }
        remove_item_button.setOnClickListener {
            removeButtonClick()
        }
        editor_recycler_view.layoutManager = LinearLayoutManager(activity)
        editor_recycler_view.adapter = topicsModifyAdapter
        first_edit_text.hint = "topic name"
        second_edit_text.visibility = View.GONE
        spinner_roles.visibility = View.GONE
    }

    private fun removeButtonClick() {
        val topicItem = topicsModifyAdapter.chosenItem as TopicItem
        (activity as ModifierActivity).presenter.removeTopic(topicItem._id)
            ?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getTopics()
            }

    }

    private fun addButtonClick() {
        if (first_edit_text.text.toString() != "") {
            (activity as ModifierActivity).presenter.addTopic(
                first_edit_text.text.toString()
            )?.addOnSuccessListener {
                (activity as ModifierActivity).presenter.getTopics()
            }
        }
    }

    fun update(topicsList: MutableList<TopicItem>) {
        val anyList = mutableListOf<Any>()
        for (item in topicsList){
            anyList.add(item)
        }
        topicsModifyAdapter.updateReviews(anyList)
    }

}