package me.dvyy.tasks.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import org.kodein.di.compose.localDI
import org.kodein.di.compose.viewmodel.KodeinViewModelScopedSingleton
import org.kodein.di.instance

@Composable
inline fun <reified VM : ViewModel> rememberGlobalViewModel(
    tag: String? = null,
): ViewModelLazy<VM> = with(localDI()) {
    val viewModelStoreOwner by instance<ViewModelStoreOwner>("global")

    remember(di, tag, viewModelStoreOwner) {
        ViewModelLazy(
            viewModelClass = VM::class,
            storeProducer = { viewModelStoreOwner.viewModelStore },
            factoryProducer = { KodeinViewModelScopedSingleton(di = di, tag = tag) }
        )
    }
}