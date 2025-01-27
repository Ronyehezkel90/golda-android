package com.example.golda.administration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.golda.R
import com.example.golda.administration.AdministrationPresenter.ROLE
import com.example.golda.dagger.App
import com.example.golda.editor.managment.EditorActivity
import com.example.golda.model.BranchItem
import com.example.golda.reviews.ReviewsActivity
import com.hannesdorfmann.mosby.mvp.MvpActivity
import kotlinx.android.synthetic.main.activity_manager.*
import kotlinx.android.synthetic.main.activity_manager.loader_spinner
import kotlinx.android.synthetic.main.fragment_topics.*
import org.bson.types.ObjectId

class AdministrationActivity : MvpActivity<AdministrationView, AdministrationPresenter>(),
    AdministrationView {

    companion object {
        val ADMINISTRATION_NAME_EXTRA = "ADMINISTRATION_NAME_EXTRA"
        val ADMINISTRATION_ROLE_EXTRA = "ADMINISTRATION_ROLE_EXTRA"
        val BRANCH_ID_EXTRA = "BRANCH_ID_EXTRA"
        val CHOSEN_DATE_EXTRA = "CHOSEN_DATE_EXTRA"
    }

    lateinit var role: ROLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        presenter.getBranches()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        role = intent.getSerializableExtra(ADMINISTRATION_ROLE_EXTRA) as ROLE
        administration_text_view.text = administration_text_view.text.toString().format(
            intent.getStringExtra(ADMINISTRATION_NAME_EXTRA), role.name
        )
        setViewByRole()
    }

    private fun setViewByRole() {
        when (role) {
            ROLE.REVIEWER -> {
                watch_review_button.visibility = View.GONE
            }
            ROLE.MANAGER -> {
                set_review_button.visibility = View.GONE
            }
        }
    }

    override fun createPresenter(): AdministrationPresenter {
        return App.getAppComponent(this).getManagerComponent().presenter

    }

    override fun setBranches(branchesList: MutableList<BranchItem>) {
        val branchesNames = branchesList.map { it.name }.toMutableList()
        val adapter = ArrayAdapter(this, R.layout.spinner_item, branchesNames)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        branches_spinner.adapter = adapter
        branches_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                if (role == ROLE.MANAGER) {
                    presenter.getDatesByBranch(position)
                } else {
                    presenter.setCurrentDate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    override fun setDates(datesList: MutableList<String>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, datesList)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        date_spinner.adapter = adapter
        date_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                // Display the selected item text on text view
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }

    private fun getChosenBranchId(): ObjectId {
        return presenter.getChosenBranchObjectIdByPosition(branches_spinner.selectedItemPosition)
    }

    fun setReviewClicked(view: View) {
        val intent = Intent(this, ReviewsActivity::class.java)
        intent.putExtra(BRANCH_ID_EXTRA, getChosenBranchId())
        intent.putExtra(CHOSEN_DATE_EXTRA, date_spinner.selectedItem as String)
        intent.putExtra(ADMINISTRATION_ROLE_EXTRA, role)
        startActivity(intent)
    }

    override fun setLoaderVisibility(showLoader: Boolean) {
        loader_spinner.visibility = if (showLoader) View.VISIBLE else View.GONE
    }
}
