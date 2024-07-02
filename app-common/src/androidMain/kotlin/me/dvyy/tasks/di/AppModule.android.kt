package me.dvyy.tasks.di

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.module.Module

actual inline fun <reified VM : ViewModel> Module.viewModel(crossinline factory: Definition<VM>) {
    this.viewModel { factory(it) }
}
