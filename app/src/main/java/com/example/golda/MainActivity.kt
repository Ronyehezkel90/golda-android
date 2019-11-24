package com.example.golda

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.golda.dagger.App
import com.example.golda.reviews.ReviewsActivity
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.hannesdorfmann.mosby.mvp.MvpView
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : MvpActivity<MvpView, MainPresenter>() {
    override fun createPresenter(): MainPresenter {
        return App.getAppComponent(this).getMainComponent().presenter
    }

    var disposable = Disposables.disposed()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        disposable = Completable
            .timer(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                logo.visibility = View.GONE
                main_layout.visibility = View.VISIBLE
            }
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }

    fun okClicked(view: View) {
        for (user in presenter.usersList) {
            if (name_edit_text.text.toString() == user.name && password_edit_text.text.toString() == user.password && role_text_view.text == user.role) {
                val intent = Intent(this, ReviewsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun reviewerClicked(view: View) {
        role_text_view.text = reviewer.text
    }

    fun managerClicked(view: View) {
        role_text_view.text = manager.text
    }

    fun superClicked(view: View) {
        role_text_view.text = super_manager.text
    }
}
