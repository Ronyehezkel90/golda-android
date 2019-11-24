package com.example.golda

import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson
) : MvpNullObjectBasePresenter<MvpView>() {

    val usersList = ArrayList<UserItem>()
    override fun attachView(view: MvpView?) {
        mongoManager.getUsers().addOnSuccessListener {
            it.forEach {
                usersList.add(gson.fromJson(it.toJson(), UserItem::class.java))
            }
        }
            .addOnFailureListener {
                Timber.d("Failed to get users")
            }
        super.attachView(view)
    }
}
