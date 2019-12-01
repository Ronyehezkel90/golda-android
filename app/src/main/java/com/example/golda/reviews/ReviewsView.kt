package com.example.golda.reviews

import com.hannesdorfmann.mosby.mvp.MvpView

interface ReviewsView : MvpView {
    fun createAdapter()
}