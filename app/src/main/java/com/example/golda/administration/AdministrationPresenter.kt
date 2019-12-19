package com.example.golda.administration

import com.example.golda.MongoManager
import com.example.golda.model.BranchItem
import com.example.golda.model.ReviewRelationItem
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import org.bson.types.ObjectId
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AdministrationPresenter
@Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson

) : MvpNullObjectBasePresenter<AdministrationView>() {

    private val branchesList = mutableListOf<BranchItem>()

    enum class ROLE { REVIEWER, SUPER, MANAGER }

    fun getBranches() {
        mongoManager.getBranches().addOnSuccessListener { branchesList ->
            branchesList.forEach {
                this.branchesList.add(gson.fromJson(it.toJson(), BranchItem::class.java))
            }
            view.setBranches(this.branchesList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun getDatesByBranch(position: Int) {
        view.setLoaderVisibility(true)
        val branchId = branchesList[position]._id
        val datesList = mutableListOf<String>()
        mongoManager.getReviewsRelationsByBranch(branchId).addOnSuccessListener {
            it.forEach {
                val reviewRelation = gson.fromJson(it.toJson(), ReviewRelationItem::class.java)
                datesList.add(reviewRelation.date)
            }
            view.setDates(datesList)
            view.setLoaderVisibility(false)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun setCurrentDate() {
        view.setDates(mutableListOf(SimpleDateFormat("dd/MM/yyyy").format(Date())))
    }

    fun getChosenBranchObjectIdByPosition(selectedItemPosition: Int): ObjectId {
        return branchesList[selectedItemPosition]._id
    }
}