package com.example.golda.sign

import dagger.Subcomponent

@Subcomponent
interface SignComponent {
    val presenter: SignPresenter
}