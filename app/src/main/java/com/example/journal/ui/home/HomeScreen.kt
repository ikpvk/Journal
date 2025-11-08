package com.example.journal.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Pure UI composable — accepts state and callbacks rather than a ViewModel.
 * This makes it easily previewable and testable.
 */
@Composable
fun HomeScreen(
    entryDates: List<LocalDate>,
    hasTodayEntry: Boolean,
    onAddClicked: () -> Unit,
    onToggleTheme: () -> Unit
) {
    androidx.compose.material3.Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Title (top center)
            Text(
                text = "Journal",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )

            // Theme toggle (top-right)
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("☼")
            }

            // Content area: list of dates or placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (entryDates.isEmpty()) {
                    Text("No entries yet", modifier = Modifier.padding(8.dp))
                } else {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    entryDates.forEach { date ->
                        Text(text = date.format(formatter), modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}
