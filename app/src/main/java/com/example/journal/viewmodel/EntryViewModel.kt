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

class EntryViewModel(
    private val repo: JournalRepository,
    private val date: LocalDate = LocalDate.now()
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    init {
        // load today's entry if any
        viewModelScope.launch {
            val entry = repo.readEntry(date)
            _content.value = entry?.content ?: ""
        }
    }

    /**
     * Called when user edits the text. Saves immediately (auto-save behavior).
     * If you later want to debounce writes, we can change this to batch saves.
     */
    fun onContentChanged(newText: String) {
        _content.value = newText
        viewModelScope.launch {
            val entry = JournalEntry(date = date, content = newText)
            repo.saveEntry(entry) // immediate save on every change (per requirement)
        }
    }

    /**
     * If needed: delete today's file (UI must enforce rules about deleting past entries).
     */
    fun deleteToday(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.deleteEntry(date)
            _content.value = ""
            onComplete?.invoke()
        }
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
