package com.example.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repo.FileJournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.io.IOException

/**
 * ViewModel for a single entry/date.
 *
 * This keeps the entry content in a MutableStateFlow so the UI can collect it.
 * onContentChanged updates the in-memory state immediately and writes to disk
 * asynchronously on the IO dispatcher via viewModelScope.
 *
 * The repo API expected here:
 * - suspend fun readEntry(date: LocalDate): JournalEntry?
 * - suspend fun saveEntry(entry: JournalEntry)
 *
 * The JournalEntry data class is assumed to have (date: LocalDate, content: String).
 */
class EntryViewModel(
    private val repo: FileJournalRepository,
    private val date: LocalDate
) : ViewModel() {

    // exposed to UI
    private val _content = MutableStateFlow<String>("")
    val content: StateFlow<String> = _content.asStateFlow()

    init {
        // Load initial content from repo
        viewModelScope.launch {
            try {
                val loadedEntry: JournalEntry? = repo.readEntry(date)
                _content.value = loadedEntry?.content ?: ""
            } catch (e: IOException) {
                // If read fails, keep content empty and don't crash the UI.
                _content.value = ""
            } catch (e: Exception) {
                // Defensive: catch any unexpected issue
                _content.value = ""
            }
        }
    }

    /**
     * Called by UI when the content changes (e.g., user types a new message).
     * We update the state immediately so the UI is responsive and then persist.
     */
    fun onContentChanged(newContent: String) {
        _content.value = newContent

        // Persist in background
        viewModelScope.launch {
            try {
                val entry = JournalEntry(date = date, content = newContent)
                repo.saveEntry(entry)
            } catch (e: IOException) {
                // Swallow IO errors for now; consider surfacing to UI later.
            } catch (e: Exception) {
                // Defensive: swallow other errors to avoid crashing UI
            }
        }
    }
}

/**
 * Factory for EntryViewModel.
 *
 * MainActivity constructs the VM using:
 * EntryViewModelFactory(FileJournalRepository(...), selectedDate!!)
 */
class EntryViewModelFactory(
    private val repo: FileJournalRepository,
    private val date: LocalDate
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            return EntryViewModel(repo, date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
