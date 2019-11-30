package com.example.golda

import android.util.ArrayMap
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import com.jakewharton.rxrelay2.BehaviorRelay
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson
) : MvpNullObjectBasePresenter<MvpView>() {

    val usersMap = ArrayMap<String, UserItem>()
    val mongoUsersBehaviorRelay: BehaviorRelay<Boolean> = BehaviorRelay.createDefault(false)

    override fun attachView(view: MvpView?) {
        mongoManager.observeMongoConnection().subscribe {
            if (it) {
                mongoManager.getUsers().addOnSuccessListener {
                    it.forEach {
                        val user = gson.fromJson(it.toJson(), UserItem::class.java)
                        usersMap[user.name] = user
                    }
                    mongoUsersBehaviorRelay.accept(true)
                }
                    .addOnFailureListener {
                        Timber.d("Failed to get users")
                    }
            }
        }

        super.attachView(view)
    }
}
