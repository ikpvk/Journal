package com.example.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.repo.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repo: ThemeRepository
) : ViewModel() {

    private val _isDark = MutableStateFlow(false)
    val isDark: StateFlow<Boolean> = _isDark

    init {
        // Observe persisted preference and mirror into state flow
        viewModelScope.launch {
            repo.isDarkThemeFlow.collect { value ->
                _isDark.value = value
            }
        }
    }

    /** Toggle the theme preference and persist it. */
    fun toggleTheme() {
        viewModelScope.launch {
            val newVal = !_isDark.value
            repo.setDarkTheme(newVal)
            _isDark.value = newVal
        }
    }

    /** Set theme explicitly. */
    fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            repo.setDarkTheme(isDarkTheme)
            _isDark.value = isDarkTheme
        }
    }
}

/** Simple factory so Activity/Composables can obtain this ViewModel with a repo. */
class ThemeViewModelFactory(
    private val repo: ThemeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
