package com.example.golda.editor.modifier

import dagger.Subcomponent

@Subcomponent
interface ModifierComponent {
    val presenter: ModifierPresenter
}