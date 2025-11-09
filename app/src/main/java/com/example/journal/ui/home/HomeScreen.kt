package com.example.journal.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journal.data.repo.FileJournalRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(
    entryDates: List<LocalDate>,
    hasTodayEntry: Boolean,
    onAddClicked: () -> Unit,
    onToggleTheme: () -> Unit,
    onEntryClicked: (LocalDate) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add journal entry"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top bar (title centered)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp)) // left spacer to center title visually
                Text(
                    text = "Journal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = Icons.Default.Brightness6,
                        contentDescription = "Toggle theme"
                    )
                }
            }

            // Content list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (entryDates.isEmpty()) {
                    Text(
                        "No journal entries yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val ctx = LocalContext.current.applicationContext

                    // For each date, show a Card with date and a one-line snippet (clickable)
                    entryDates.forEachIndexed { _, date ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clickable { onEntryClicked(date) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = date.format(formatter),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                    fontWeight = FontWeight.SemiBold
                                )

                                // Small preview of the entry content (read-only)
                                val previewText = runCatching {
                                    // quick synchronous read (small local I/O)
                                    val repo = FileJournalRepository(ctx)
                                    val entry = runBlocking { repo.readEntry(date) }
                                    entry?.content ?: ""
                                }.getOrDefault("")

                                Text(
                                    text = previewText.replace("\n", " ").trim(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
