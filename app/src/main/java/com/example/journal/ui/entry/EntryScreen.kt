package com.example.journal.ui.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Entry screen which supports:
 *  - editable mode (isEditable = true) -> shows a TextField and calls onContentChange
 *  - read-only mode (isEditable = false) -> shows a scrollable Text view (non-editable)
 *
 * Tapping the date at the top always invokes onBack() to return to the previous screen.
 */
@Composable
fun EntryScreen(
    date: LocalDate,
    content: String,
    isEditable: Boolean,
    onContentChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top: Date label (tappable to go back)
            Text(
                text = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .clickable { onBack() }
            )

            // Editor / Reader area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp)
            ) {
                if (isEditable) {
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
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
