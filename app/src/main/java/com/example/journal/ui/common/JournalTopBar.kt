@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.journal.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Reusable top bar for both Home and Entry screens.
 *
 * Using @OptIn for ExperimentalMaterial3Api because
 * CenterAlignedTopAppBar and its color APIs are still experimental.
 */
@Composable
fun JournalTopBar(
    titleText: String,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    // consistent touch target so title stays visually centered
                    .size(48.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Toggle theme"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
