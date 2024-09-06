package me.dvyy.tasks.di

import androidx.lifecycle.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

actual inline fun <reified VM : ViewModel> Module.viewModel(crossinline factory: Definition<VM>) {
    this.viewModel { factory(it) }
}
