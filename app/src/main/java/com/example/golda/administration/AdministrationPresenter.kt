package com.example.golda.administration

import com.example.golda.MongoManager
import com.example.golda.model.BranchItem
import com.example.golda.model.ReviewRelationItem
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AdministrationPresenter
@Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson

) : MvpNullObjectBasePresenter<AdministrationView>() {

    private val branchItemsList = mutableListOf<BranchItem>()

    enum class ROLE { REVIEWER, SUPER, MANAGER }

    fun getBranches() {
        mongoManager.getBranches().addOnSuccessListener { branchesList ->
            branchesList.forEach {
                branchItemsList.add(gson.fromJson(it.toJson(), BranchItem::class.java))
            }
            view.setBranches(branchItemsList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun getDatesByBranch(position: Int) {
        val branchId = branchItemsList[position]._id
        val datesList = mutableListOf<String>()
        mongoManager.getReviewsRelationsByBranch(branchId).addOnSuccessListener {
            it.forEach {
                val reviewRelation = gson.fromJson(it.toJson(), ReviewRelationItem::class.java)
                datesList.add(SimpleDateFormat("dd/MM/yyyy").format(reviewRelation.date))
            }
            view.setDates(datesList)
        }.addOnFailureListener {
            Timber.e("Topics failure")
        }
    }

    fun setCurrentDate() {
        view.setDates(mutableListOf(SimpleDateFormat("dd/MM/yyyy").format(Date())))
    }
}