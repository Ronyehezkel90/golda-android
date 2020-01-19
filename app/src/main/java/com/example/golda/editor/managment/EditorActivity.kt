package com.example.golda.editor.managment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.golda.R
import com.example.golda.dagger.App
import com.example.golda.editor.modifier.ModifierActivity
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.hannesdorfmann.mosby.mvp.MvpView

class EditorActivity : MvpActivity<MvpView, EditorPresenter>() {
    companion object {
        val TO_MODIFY = "toModify"
    }

    override fun createPresenter(): EditorPresenter {
        return App.getAppComponent(this).getEditorComponent().presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
    }

    fun usersOnClick(view: View) {
        modify("users")
    }

    fun topicsOnClick(view: View) {
        modify("topics")
    }

    fun reviewsOnClick(view: View) {
        modify("reviews")
    }
    fun branchesOnClick(view: View) {
        modify("branches")
    }

    fun modify(toModify: String) {
        val intent = Intent(this, ModifierActivity::class.java)
        intent.putExtra(TO_MODIFY, toModify)
        startActivity(intent)
    }

}