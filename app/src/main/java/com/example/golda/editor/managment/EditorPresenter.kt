package com.example.golda.editor.managment

import android.content.SharedPreferences
import com.example.golda.MongoManager
import com.google.gson.Gson
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter
import com.hannesdorfmann.mosby.mvp.MvpView
import javax.inject.Inject


class EditorPresenter @Inject constructor(
    private val mongoManager: MongoManager,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
) : MvpNullObjectBasePresenter<MvpView>() {


}
