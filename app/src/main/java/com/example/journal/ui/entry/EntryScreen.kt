package com.example.journal.ui.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// --------------------------------------------
// DATA MODEL FOR CHAT MESSAGES
// --------------------------------------------
data class ChatMessage(
    val text: String,
    val time: String
)

// Convert the SINGLE stored paragraph into INDIVIDUAL chat messages
private fun parseMessages(raw: String): List<ChatMessage> {
    if (raw.isBlank()) return emptyList()

    return raw.split("\n")
        .filter { it.trim().isNotEmpty() }
        .map {
            ChatMessage(
                text = it.trim(),
                time = "—" // No timestamps in old data
            )
        }
}

// Convert list of chat messages back into a SINGLE string to save
private fun serializeMessages(list: List<ChatMessage>): String {
    return list.joinToString("\n") { it.text }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION") // suppress the minor deprecation warnings for Icons.Filled usage
@Composable
fun EntryScreen(
    date: LocalDate,
    content: String,
    isEditable: Boolean,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit
) {
    // formatted date for title (13-Nov-2025)
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
    val formattedDate = date.format(dateFormatter)

    val messages = remember(content) { parseMessages(content).toMutableStateList() }
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                // CENTER THE TITLE using a full-width Box
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = formattedDate)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Text("🌓")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --------------------------
            // CHAT MESSAGES LIST
            // --------------------------
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg = msg)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // --------------------------
            // INPUT BOX (only today)
            // --------------------------
            if (isEditable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (inputValue.text.isEmpty()) {
                                    Text(
                                        text = "Type a message...",
                                        style = TextStyle(color = Color.Gray)
                                    )
                                }
                                inner()
                            }
                        )
                    }

                    IconButton(
                        onClick = {
                            val trimmed = inputValue.text.trim()
                            if (trimmed.isNotEmpty()) {

                                // Add new chat bubble
                                messages.add(
                                    ChatMessage(
                                        text = trimmed,
                                        time = LocalTime.now()
                                            .format(DateTimeFormatter.ofPattern("hh:mm a"))
                                    )
                                )

                                // Save as single string
                                onContentChange(serializeMessages(messages))

                                inputValue = TextFieldValue("")
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

// --------------------------------------------
// COMPOSABLE: A SINGLE MESSAGE BUBBLE
// --------------------------------------------
@Composable
fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .padding(12.dp)
        ) {
            Text(text = msg.text, color = MaterialTheme.colorScheme.onSurface)
        }

        if (msg.time != "—") {
            Text(
                text = msg.time,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
