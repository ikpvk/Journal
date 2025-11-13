package com.example.journal.ui.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.journal.ui.theme.JournalTheme
import java.time.LocalDate
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EntryScreenPreview() {
    JournalTheme {
        EntryScreen(
            date = LocalDate.now(),
            content = "Hello\nThis is a sample message",
            isEditable = true,
            onContentChange = {},
            onBack = {},
            onToggleTheme = {}
        )
    }
}
