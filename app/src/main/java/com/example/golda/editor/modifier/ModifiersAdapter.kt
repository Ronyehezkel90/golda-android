package com.example.golda.editor.modifier

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.golda.R
import com.example.golda.model.BranchItem
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.example.golda.model.UserItem
import kotlinx.android.synthetic.main.modify_item.view.*

class ModifierAdapter(val type: String) : RecyclerView.Adapter<ModifierAdapter.ViewHolder>() {

    var modifyItemList: List<Any> = mutableListOf()
    lateinit var chosenItem: Any

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.modify_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return modifyItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            "user" -> {
                holder.modifyTextView.text = (modifyItemList[position] as UserItem).name
                holder.modifyContainer.setBackgroundResource(if ((modifyItemList[position] as UserItem).selected) R.color.grayRoleButton else R.color.lowPurple)
            }
            "review" -> {
                holder.modifyTextView.text = (modifyItemList[position] as ReviewItem).title
                holder.modifyContainer.setBackgroundResource(if ((modifyItemList[position] as ReviewItem).selected) R.color.grayRoleButton else R.color.lowPurple)
            }
            "topic" -> {
                holder.modifyTextView.text = (modifyItemList[position] as TopicItem).topic
                holder.modifyContainer.setBackgroundResource(if ((modifyItemList[position] as TopicItem).selected) R.color.grayRoleButton else R.color.lowPurple)
            }
            "branch" -> {
                holder.modifyTextView.text = (modifyItemList[position] as BranchItem).name
                holder.modifyContainer.setBackgroundResource(if ((modifyItemList[position] as BranchItem).selected) R.color.grayRoleButton else R.color.lowPurple)
            }
        }
        holder.modifyContainer.setOnClickListener {
            setChosenItemSelected(false)
            chosenItem = modifyItemList[position]
            setChosenItemSelected(true)

            notifyDataSetChanged()
        }
    }

    private fun setChosenItemSelected(selected: Boolean) {
        when (type) {
            "user" -> (chosenItem as UserItem).selected = selected
            "review" -> (chosenItem as ReviewItem).selected = selected
            "topic" -> (chosenItem as TopicItem).selected = selected
            "branch" -> (chosenItem as BranchItem).selected = selected
        }
    }

    fun updateReviews(reviewsList: MutableList<Any>) {
        this.modifyItemList = reviewsList
        if (!modifyItemList.isNullOrEmpty()) {
            chosenItem = modifyItemList[0]
            setChosenItemSelected(true)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modifyTextView: TextView = itemView.modifyTextView
        val modifyContainer: ConstraintLayout = itemView.modify_item_container
    }
}