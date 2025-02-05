package me.dvyy.tasks.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.dvyy.tasks.database.Vault
import me.dvyy.tasks.utils.WhileUiSubscribed

class VaultViewModel(
    val vault: Vault
): ViewModel() {
    val fileTree = vault.fileTree().stateIn(viewModelScope, WhileUiSubscribed, emptyList())

    init {
        viewModelScope.launch {
            vault.index()
        }
    }
}
