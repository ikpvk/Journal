package com.example.journal

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.data.repo.FileJournalRepository
import com.example.journal.data.repo.ThemeRepository
import com.example.journal.ui.entry.EntryScreen
import com.example.journal.ui.home.HomeScreen
import com.example.journal.viewmodel.EntryViewModel
import com.example.journal.viewmodel.EntryViewModelFactory
import com.example.journal.viewmodel.HomeViewModel
import com.example.journal.viewmodel.HomeViewModelFactory
import com.example.journal.viewmodel.ThemeViewModel
import com.example.journal.viewmodel.ThemeViewModelFactory
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    // repo for HomeViewModel
    private val repo by lazy { FileJournalRepository(applicationContext) }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repo)
    }

    // Theme repository + ViewModel (persisted via DataStore)
    private val themeRepo by lazy { ThemeRepository(applicationContext) }
    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModelFactory(themeRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JournalApp(homeViewModel = homeViewModel, themeViewModel = themeViewModel)
        }

        // --- temporary: create dummy entries for testing ---
        val repo = FileJournalRepository(applicationContext)
        val dummyEntries = mapOf(
            java.time.LocalDate.of(2025, 11, 1) to """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        Sed at velit sit amet justo volutpat malesuada.
        Suspendisse potenti. Nullam commodo sapien vel nunc viverra volutpat.
    """.trimIndent(),
            java.time.LocalDate.of(2025, 10, 20) to """
        Curabitur in leo eu justo placerat gravida.
        Aliquam erat volutpat. Sed a imperdiet nulla.
        Quisque at nunc ac justo tincidunt suscipit.
    """.trimIndent()
        )

        kotlinx.coroutines.GlobalScope.launch {
            dummyEntries.forEach { (date, content) ->
                if (repo.readEntry(date) == null) {
                    repo.saveEntry(com.example.journal.data.model.JournalEntry(date, content))
                }
            }
        }

    }
}

@Composable
fun JournalApp(homeViewModel: HomeViewModel, themeViewModel: ThemeViewModel) {
    // Collect theme preference
    val isDark by themeViewModel.isDark.collectAsState()

    // collect HomeViewModel state
    val entryDates by homeViewModel.entryDates.collectAsState()
    val hasTodayEntry by homeViewModel.hasTodayEntry.collectAsState()

    // simple navigation state: false = Home, true = Entry
    var showEntryScreen by remember { mutableStateOf(false) }

    // EntryViewModel via Compose helper (factory)
    val context = LocalContext.current.applicationContext
    val repoForEntry = remember { FileJournalRepository(context) }
    val entryVm: EntryViewModel = viewModel(
        factory = EntryViewModelFactory(repoForEntry, java.time.LocalDate.now())
    )

    // Choose color scheme based on persisted preference
    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

    // ---- System UI (status bar / nav bar) handling ----
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            // set status bar & navigation bar background to match app surface
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()

            // For light backgrounds we want dark icons; for dark backgrounds we want light icons
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }
    // --------------------------------------------------

    MaterialTheme(colorScheme = colorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (showEntryScreen) {
                val content by entryVm.content.collectAsState()
                EntryScreen(
                    date = java.time.LocalDate.now(),
                    content = content,
                    onContentChange = { entryVm.onContentChanged(it) },
                    onBack = {
                        showEntryScreen = false
                        homeViewModel.refresh()
                    }
                )
            } else {
                HomeScreen(
                    entryDates = entryDates,
                    hasTodayEntry = hasTodayEntry,
                    onAddClicked = { showEntryScreen = true },
                    onToggleTheme = { themeViewModel.toggleTheme() }
                )
            }
        }
    }
}
