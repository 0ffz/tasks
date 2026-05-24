package me.dvyy.tasks.takeout

import org.kodein.di.DI
import org.kodein.di.bindSingletonOf

fun takeoutModule() = DI.Module("takeout") {
    bindSingletonOf(::TakeoutRepository)
    bindSingletonOf(::TakeoutViewModel)
}
