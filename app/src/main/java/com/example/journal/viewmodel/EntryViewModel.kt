package com.example.journal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repo.JournalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class EntryViewModel(
    private val repo: JournalRepository,
    private val date: LocalDate = LocalDate.now()
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    // debounce job for delayed save
    private var saveJob: Job? = null
    private val saveDebounceMs: Long = 500L

    init {
        // load today's entry if any
        viewModelScope.launch {
            val entry = repo.readEntry(date)
            _content.value = entry?.content ?: ""
        }
    }

    /**
     * Called when user edits the text. Updates state immediately and schedules a debounced save.
     */
    fun onContentChanged(newText: String) {
        _content.value = newText

        // cancel previous scheduled save, schedule a new one
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(saveDebounceMs)
            // After debounce, write to storage. The repo will delete if content blank.
            val entry = JournalEntry(date = date, content = newText)
            repo.saveEntry(entry)
        }
    }

    /**
     * If needed: immediately persist current content (runs without debounce)
     */
    fun flushSave(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val entry = JournalEntry(date = date, content = _content.value)
                repo.saveEntry(entry)
            } finally {
                onComplete?.invoke()
            }
        }
    }

    /**
     * Delete today's entry file and clear content in-memory.
     */
    fun deleteToday(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.deleteEntry(date)
            _content.value = ""
            onComplete?.invoke()
        }
    }

    override fun onCleared() {
        // ensure final save when ViewModel is cleared
        saveJob?.cancel()
        viewModelScope.launch {
            val entry = JournalEntry(date = date, content = _content.value)
            repo.saveEntry(entry)
        }
        super.onCleared()
    }
}

class EntryViewModelFactory(
    private val repo: JournalRepository,
    private val date: LocalDate = LocalDate.now()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            return EntryViewModel(repo, date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
