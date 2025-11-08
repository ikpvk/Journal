package com.example.journal.data.model

import java.time.LocalDate

data class JournalEntry(
    val date: LocalDate,
    val content: String
)
