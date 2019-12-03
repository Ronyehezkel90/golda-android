package com.example.golda.topics

import dagger.Subcomponent

@Subcomponent
interface TopicsComponent {
    val presenter: TopicsPresenter
}