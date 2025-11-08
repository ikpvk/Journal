package com.example.journal.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

@Preview(showBackground = true)
@Composable
fun HomeScreen_Preview() {
    val sampleDates = listOf(LocalDate.parse("2025-11-08"), LocalDate.parse("2025-11-07"))
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeScreen(
                entryDates = sampleDates,
                hasTodayEntry = true,
                onAddClicked = {},
                onToggleTheme = {}
            )
        }
    }
}
