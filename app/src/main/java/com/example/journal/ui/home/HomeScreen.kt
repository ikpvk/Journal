package com.example.journal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journal.ui.theme.JournalTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    entryDates: List<LocalDate>,
    hasTodayEntry: Boolean,
    onAddClicked: () -> Unit,
    onToggleTheme: () -> Unit,
    onEntryClicked: (LocalDate) -> Unit,
    // optional previews map (date -> preview string). Default kept empty so existing callers need not change.
    previews: Map<LocalDate, String> = emptyMap()
) {
    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Journal")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Text("🌓")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Add"
                    )
                },
                text = { Text(if (hasTodayEntry) "Open Today" else "Add Today") },
                onClick = onAddClicked
            )
        }
    ) { paddingValues ->
        if (entryDates.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "No entries",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No entries yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Tap the button to add today's chat-style entry",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
        ) {
            items(entryDates) { date ->
                DateRow(
                    date = date,
                    formatted = date.format(formatter),
                    onClick = { onEntryClicked(date) },
                    preview = previews[date] ?: ""
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DateRow(date: LocalDate, formatted: String, onClick: () -> Unit, preview: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Removed the calendar icon entirely
        // Removed the circular background bubble

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = formatted,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (preview.isNotBlank()) {
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "Tap to open chat-style entry",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



/* --------------------------
   Previews
   -------------------------- */
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sampleDates = remember {
        listOf(
            LocalDate.now(),
            LocalDate.now().minusDays(1),
            LocalDate.now().minusDays(3),
            LocalDate.now().minusDays(7)
        )
    }

    val samplePreviews = remember {
        mapOf(
            sampleDates[0] to "Hey — started the day with coffee.\nHad a short walk.\nFelt productive.",
            sampleDates[1] to "Met with team.\nNotes:\n- release\n- tests",
            sampleDates[2] to "Shopping list:\nmilk\nbread\neggs",
            sampleDates[3] to ""
        )
    }

    JournalTheme {
        HomeScreen(
            entryDates = sampleDates,
            hasTodayEntry = true,
            onAddClicked = {},
            onToggleTheme = {},
            onEntryClicked = {},
            previews = samplePreviews
        )
    }
}
