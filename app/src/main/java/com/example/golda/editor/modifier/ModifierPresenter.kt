package com.example.golda.editor.modifier

import com.example.golda.MongoManager
import com.example.golda.model.BranchItem
import com.example.golda.model.ReviewItem
import com.example.golda.model.TopicItem
import com.example.golda.model.UserItem
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import com.mongodb.stitch.core.services.mongodb.remote.RemoteDeleteResult
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult
import org.bson.types.ObjectId
import javax.inject.Inject


class ModifierPresenter @Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson
) : MvpNullObjectBasePresenter<MvpView>() {

    fun getUsers(fragmentType: String = "usersFragment") {
        mongoManager.getUsers().addOnSuccessListener {
            val usersList = mutableListOf<UserItem>()
            it.forEach {
                val user = gson.fromJson(it.toJson(), UserItem::class.java)
                usersList.add(user)
            }
            if (fragmentType == "usersFragment") {
                (view as ModifierActivity).updateUsersAdapter(usersList)
            } else if (fragmentType == "branchesFragment") {
                (view as ModifierActivity).updateUsersSpinner(usersList)
            }

        }
    }

    fun addUser(name: String, password: String, role: String): Task<RemoteInsertOneResult>? {
        return mongoManager.addUser(UserItem(ObjectId.get(), name, password, role))
    }

    fun removeUser(userId: ObjectId): Task<RemoteDeleteResult>? {
        return mongoManager.removeUser(userId)
    }

    fun getTopics(fragmentType: String = "topicsFragment") {
        mongoManager.getTopics().addOnSuccessListener {
            val topicsList = mutableListOf<TopicItem>()
            it.forEach {
                topicsList.add(gson.fromJson(it.toJson(), TopicItem::class.java))
            }
            if (fragmentType == "reviewsFragment") {
                (view as ModifierActivity).updateReviewsTopicsSpinner(topicsList)
            } else if (fragmentType == "topicsFragment") {
                (view as ModifierActivity).updateTopicsAdapter(topicsList)
            }
        }
    }

    fun removeTopic(topicId: ObjectId): Task<RemoteDeleteResult>? {
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                if (reviewItem.topic == topicId) {
                    mongoManager.removeReview(reviewItem._id)
                }
            }
        }
        return mongoManager.removeTopic(topicId)
    }

    fun addTopic(topic: String): Task<RemoteInsertOneResult>? {
        return mongoManager.addTopic(TopicItem(ObjectId.get(), topic))
    }

    fun addReview(
        title: String,
        subtitle: String,
        topicId: ObjectId
    ): Task<RemoteInsertOneResult>? {
        return mongoManager.addReview(ObjectId.get(), title, subtitle, topicId)
    }

    fun removeReview(reviewId: ObjectId): Task<RemoteDeleteResult>? {
        return mongoManager.removeReview(reviewId)
    }

    fun getReviews() {
        val reviewsList = mutableListOf<ReviewItem>()
        mongoManager.getReviews().addOnSuccessListener {
            it.forEach {
                val reviewItem = gson.fromJson(it.toJson(), ReviewItem::class.java)
                reviewsList.add(reviewItem)
                (view as ModifierActivity).updateReviews(reviewsList)
            }
        }
    }

    fun getBranches() {
        mongoManager.getBranches().addOnSuccessListener {
            val branchesList = mutableListOf<BranchItem>()
            it.forEach {
                branchesList.add(gson.fromJson(it.toJson(), BranchItem::class.java))
            }
            (view as ModifierActivity).updateBranches(branchesList)
        }
    }

    fun addBranch(name: String, address: String, phone: String, managerObjectId: ObjectId):Task<RemoteInsertOneResult>? {
        return mongoManager.addBranch(ObjectId.get(), name, address, phone, managerObjectId)
    }

    fun removeBranch(branchId: ObjectId): Task<RemoteDeleteResult>? {
        return mongoManager.removeBranch(branchId)
    }

}
