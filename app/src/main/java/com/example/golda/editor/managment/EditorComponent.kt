package com.example.golda.editor.managment

import dagger.Subcomponent

@Subcomponent
interface EditorComponent {
    val presenter: EditorPresenter
}