package com.example.golda.administration

import dagger.Subcomponent

@Subcomponent
interface AdministrationComponent {
    val presenter: AdministrationPresenter
}