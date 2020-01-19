package com.example.golda.sign

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.golda.R
import com.example.golda.administration.AdministrationActivity
import com.example.golda.administration.AdministrationActivity.Companion.ADMINISTRATION_NAME_EXTRA
import com.example.golda.administration.AdministrationActivity.Companion.ADMINISTRATION_ROLE_EXTRA
import com.example.golda.administration.AdministrationPresenter
import com.example.golda.dagger.App
import com.example.golda.editor.managment.EditorActivity
import com.hannesdorfmann.mosby.mvp.MvpActivity
import com.hannesdorfmann.mosby.mvp.MvpView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class SignActivity : MvpActivity<MvpView, SignPresenter>() {
    override fun createPresenter(): SignPresenter {
        return App.getAppComponent(this).getMainComponent().presenter
    }

    var disposable = Disposables.disposed()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        disposable = presenter.mongoUsersBehaviorRelay
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) {
                    logo.visibility = View.GONE
                    main_layout.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }

    fun okClicked(view: View) {
        val userTxt = name_edit_text.text.toString()
        val passwordTxt = password_edit_text.text.toString()
        when {
            userTxt !in presenter.usersMap.keys -> error_text_view.text = "I don't know $userTxt"
            passwordTxt != presenter.usersMap[userTxt]?.password -> error_text_view.text =
                "Wrong password for $userTxt"
            else -> {
                if (presenter.usersMap[userTxt]?.role == "super") {
                    val intent = Intent(this, EditorActivity::class.java)
                    startActivity(intent)
                }
                else {
                    presenter.saveUserToSharedPref(presenter.usersMap[userTxt]?._id?.toHexString())
                    val intent = Intent(this, AdministrationActivity::class.java)
                    intent.putExtra(ADMINISTRATION_NAME_EXTRA, userTxt)
                    intent.putExtra(
                        ADMINISTRATION_ROLE_EXTRA,
                        getRoleEnumByString(presenter.usersMap[userTxt]!!.role)
                    )
                    startActivity(intent)
                }
            }
        }
    }

    private fun getRoleEnumByString(roleString: String): AdministrationPresenter.ROLE {
        return when (roleString) {
            "manager" -> AdministrationPresenter.ROLE.MANAGER
            "super" -> AdministrationPresenter.ROLE.SUPER
            else -> AdministrationPresenter.ROLE.REVIEWER
        }
    }

}

