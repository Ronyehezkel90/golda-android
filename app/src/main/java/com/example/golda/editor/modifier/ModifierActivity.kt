package com.example.golda.editor.modifier

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.golda.R
import com.example.golda.dagger.App
import com.example.golda.editor.managment.EditorActivity
import com.example.golda.editor.modifier.fragments.BranchEditFragment
import com.example.golda.editor.modifier.fragments.ReviewsEditFragment
import com.example.golda.editor.modifier.fragments.TopicEditFragment
import com.example.golda.editor.modifier.fragments.UsersEditFragment
import com.example.golda.model.BranchItem
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.example.golda.model.UserItem
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.hannesdorfmann.mosby.mvp.MvpView


class ModifierActivity : MvpActivity<MvpView, ModifierPresenter>() {

    lateinit var modifierFragment: Fragment
    override fun createPresenter(): ModifierPresenter {
        return App.getAppComponent(this).getModifierComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifier)
        val toModify = intent.getStringExtra(EditorActivity.TO_MODIFY)
        when (toModify) {
            "users" -> modifierFragment =
                UsersEditFragment()
            "topics" -> modifierFragment =
                TopicEditFragment()
            "reviews" -> modifierFragment =
                ReviewsEditFragment()
            "branches" -> modifierFragment =
                BranchEditFragment()
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.modifier_activity_id, modifierFragment)
        transaction.commit()
    }

    fun updateUsersAdapter(usersList: MutableList<UserItem>) {
        (modifierFragment as UsersEditFragment).update(usersList)
    }

    fun updateTopicsAdapter(topicsList: MutableList<TopicItem>) {
        (modifierFragment as TopicEditFragment).update(topicsList)
    }

    fun updateReviewsTopicsSpinner(topicsList: MutableList<TopicItem>) {
        (modifierFragment as ReviewsEditFragment).updateSpinner(topicsList)
    }

    fun updateReviews(reviewsList: MutableList<ReviewItem>) {
        (modifierFragment as ReviewsEditFragment).updateReviews(reviewsList)
    }

    fun updateBranches(branchesList: MutableList<BranchItem>) {
        (modifierFragment as BranchEditFragment).update(branchesList)
    }

    fun updateUsersSpinner(usersList: MutableList<UserItem>) {
        (modifierFragment as BranchEditFragment).updateUsersSpinner(usersList)
    }

}
