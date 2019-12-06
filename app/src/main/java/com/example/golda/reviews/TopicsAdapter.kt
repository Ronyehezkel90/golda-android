package com.example.golda.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.golda.R
import com.example.golda.model.TopicItem
import kotlinx.android.synthetic.main.topic_item.view.*

class TopicsAdapter(val topicOnClick: (Int) -> Unit) : RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {

    var topicItemList: List<TopicItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topic_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topicItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = topicItemList[position].topic
        holder.itemView.setOnClickListener {
            topicOnClick(topicItemList[position].id)
        }
    }

    fun updateTopics(topicsList: MutableList<TopicItem>) {
        this.topicItemList = topicsList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.topicTextView
    }
}