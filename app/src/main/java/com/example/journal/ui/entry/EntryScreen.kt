@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.journal.ui.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Entry screen which supports:
 *  - editable mode (isEditable = true) -> shows a TextField and calls onContentChange
 *  - read-only mode (isEditable = false) -> shows a scrollable Text view (non-editable)
 *
 * Tapping the date at the top always invokes onBack() to return to the previous screen.
 * The theme toggle is available on this screen via onToggleTheme.
 */
@Composable
fun EntryScreen(
    date: LocalDate,
    content: String,
    isEditable: Boolean,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit = {}
) {
    val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

    Scaffold(
        topBar = {
            // Center-aligned top app bar to match the Home screen alignment & spacing.
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                title = {
                    // Make the title clickable to go back
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable { onBack() }
                    )
                },
                actions = {
                    // Keep icon touch target consistent with Home screen
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brightness6,
                            contentDescription = "Toggle theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Editor / Reader area placed under the top bar automatically via innerPadding
                    if (isEditable) {
                        // editable TextField that fills remaining space
                        TextField(
                            value = content,
                            onValueChange = onContentChange,
                            modifier = Modifier
                                .fillMaxSize(),
                            textStyle = TextStyle(fontSize = 16.sp),
                            placeholder = {
                                Text(
                                    text = "Write your thoughts for today...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
                            singleLine = false,
                            maxLines = Int.MAX_VALUE
                        )
                    } else {
                        // Read-only: show content as scrollable text
                        val preview = if (content.isBlank()) {
                            "No content"
                        } else {
                            content
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = preview,
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    )
}
