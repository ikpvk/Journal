package com.example.journal.ui.entry

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@Preview(showBackground = true)
@Composable
fun EntryScreen_Preview() {
    val sampleDate = LocalDate.parse("2025-11-09")
    val sampleText = """
        This is a sample journal entry.
        
        Use this preview to tune spacing, fonts and layout.
        
        - Bullet 1
        - Bullet 2
    """.trimIndent()

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            // For preview, show both editable and read-only variants by defaulting to editable.
            EntryScreen(
                date = sampleDate,
                content = sampleText,
                isEditable = true, // preview editable mode
                onContentChange = { /* preview only */ },
                onBack = { /* preview only */ }
            )
        }
    }
}
