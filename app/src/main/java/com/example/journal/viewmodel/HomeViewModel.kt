package com.example.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repo.JournalRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * HomeViewModel now exposes:
 * - entryDates: List<LocalDate>
 * - hasTodayEntry: Boolean
 * - previews: Map<LocalDate, String>  // first up to 3 non-empty lines of the entry
 *
 * The ViewModel will load previews for the visible dates when refreshing.
 */
class HomeViewModel(
    private val repo: JournalRepository
) : ViewModel() {

    private val _entryDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val entryDates: StateFlow<List<LocalDate>> = _entryDates

    private val _hasTodayEntry = MutableStateFlow(false)
    val hasTodayEntry: StateFlow<Boolean> = _hasTodayEntry

    // map date -> preview string (first up to 3 non-empty lines)
    private val _previews = MutableStateFlow<Map<LocalDate, String>>(emptyMap())
    val previews: StateFlow<Map<LocalDate, String>> = _previews

    init {
        refresh()
    }

    /**
     * Load the list of existing entry dates (descending), update hasTodayEntry,
     * and also load a short preview for each date (first 3 non-empty lines).
     */
    fun refresh() {
        viewModelScope.launch {
            val dates = repo.listEntryDatesDescending()
            _entryDates.value = dates
            _hasTodayEntry.value = dates.any { it == LocalDate.now() }

            // Load previews concurrently for each date
            val deferred = dates.map { date ->
                async {
                    val entry: JournalEntry? = repo.readEntry(date)
                    val preview = buildPreviewFromEntry(entry)
                    date to preview
                }
            }

            val pairs = deferred.awaitAll()
            _previews.value = pairs.toMap()
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
     * Convenience: save today's entry (and refresh listing + previews)
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

    /**
     * Helper to build a preview from a JournalEntry.
     * - Splits content by newline
     * - Takes the first up to 3 non-empty lines
     * - Joins them with newline characters (UI can render with maxLines)
     */
    private fun buildPreviewFromEntry(entry: JournalEntry?): String {
        if (entry == null) return ""
        val lines = entry.content
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (lines.isEmpty()) return ""
        return lines.take(3).joinToString("\n")
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
