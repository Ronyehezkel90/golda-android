package com.example.golda

import dagger.Subcomponent

@Subcomponent
interface MainComponent {
    val presenter: MainPresenter
}