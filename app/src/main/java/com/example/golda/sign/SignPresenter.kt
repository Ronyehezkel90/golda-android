package com.example.golda.sign

import android.content.SharedPreferences
import android.util.ArrayMap
import com.example.golda.MongoManager
import com.example.golda.model.UserItem
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import com.jakewharton.rxrelay2.BehaviorRelay
import timber.log.Timber
import javax.inject.Inject


class SignPresenter @Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
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

    fun saveUserToSharedPref(userId: String?) {
        sharedPreferences.edit().putString("userId", userId).apply()
    }
}
