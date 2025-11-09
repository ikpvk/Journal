package com.example.journal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                    onToggleTheme = {
                        themeViewModel.toggleTheme()
                    }
                )
            }
        }
    }
}
