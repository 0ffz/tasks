package me.dvyy.tasks.takeout

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun takeoutModule() = module {
    singleOf(::TakeoutRepository)
    viewModelOf(::TakeoutViewModel)
}
