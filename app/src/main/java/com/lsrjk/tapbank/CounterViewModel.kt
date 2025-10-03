package com.lsrjk.tapbank

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CounterViewModel(private val repo: CounterRepository): ViewModel() {

    val count = repo.flow.map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    val hapticsEnabled = repo.hapticsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun increment() = viewModelScope.launch {
        val next = (count.value + 1L).coerceAtMost(Long.MAX_VALUE)
        repo.set(next)
    }

    fun reset() = viewModelScope.launch {
        repo.set(0L)
    }

    fun toggleHaptics() = viewModelScope.launch {
        repo.setHapticsEnabled(!hapticsEnabled.value)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CounterViewModel(CounterRepository(context)) as T
                }
            }
    }
}
