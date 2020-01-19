package com.example.golda.administration

import com.example.golda.model.BranchItem
import com.hannesdorfmann.mosby.mvp.MvpView

interface AdministrationView : MvpView {
    fun setBranches(branchesList: MutableList<BranchItem>)
    fun setDates(datesList: MutableList<String>)
    fun setLoaderVisibility(showLoader: Boolean)

}