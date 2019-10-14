package com.example.golda.dagger

import com.example.golda.MongoManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Mediator @Inject constructor(

    val mongoManager: MongoManager

)