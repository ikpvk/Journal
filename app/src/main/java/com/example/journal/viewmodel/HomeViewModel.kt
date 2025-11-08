package com.example.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repo.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val repo: JournalRepository
) : ViewModel() {

    private val _entryDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val entryDates: StateFlow<List<LocalDate>> = _entryDates

    private val _hasTodayEntry = MutableStateFlow(false)
    val hasTodayEntry: StateFlow<Boolean> = _hasTodayEntry

    init {
        refresh()
    }

    /**
     * Load the list of existing entry dates (descending) and update hasTodayEntry flag.
     */
    fun refresh() {
        viewModelScope.launch {
            val dates = repo.listEntryDatesDescending()
            _entryDates.value = dates
            _hasTodayEntry.value = dates.any { it == LocalDate.now() }
        }
    }

    /**
     * Convenience: read today's entry (may return null)
     */
    fun readToday(onResult: (JournalEntry?) -> Unit) {
        viewModelScope.launch {
            val entry = repo.readEntry(LocalDate.now())
            onResult(entry)
        }
    }

    /**
     * Convenience: save today's entry (and refresh listing)
     */
    fun saveToday(content: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val entry = JournalEntry(date = LocalDate.now(), content = content)
            repo.saveEntry(entry)
            refresh()
            onComplete?.invoke()
        }
    }

    /**
     * Expose deletion if needed. UI must enforce rule: no deleting past entries.
     */
    fun deleteDate(date: LocalDate, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.deleteEntry(date)
            refresh()
            onComplete?.invoke()
        }
    }
}

/**
 * Simple ViewModelProvider.Factory so we can create HomeViewModel with a repository instance.
 */
class HomeViewModelFactory(
    private val repo: JournalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
