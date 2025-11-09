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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
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
import java.time.LocalDate

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

    // simple navigation state:
    // selectedDate = null -> show Home
    // selectedDate != null -> show Entry for that date
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Choose color scheme based on persisted preference
    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

    // ---- System UI (status bar / nav bar) handling ----
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        SideEffect {
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }
    // --------------------------------------------------

    MaterialTheme(colorScheme = colorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (selectedDate != null) {
                // Create an EntryViewModel keyed by the selected date so each date gets its own VM
                val key = selectedDate.toString()
                val entryVm: EntryViewModel = viewModel(
                    key = key,
                    factory = EntryViewModelFactory(/* repo */ FileJournalRepository(LocalContext.current.applicationContext), selectedDate!!)
                )

                val content by entryVm.content.collectAsState()

                EntryScreen(
                    date = selectedDate!!,
                    content = content,
                    isEditable = (selectedDate == LocalDate.now()),
                    onContentChange = { if (selectedDate == LocalDate.now()) entryVm.onContentChanged(it) },
                    onBack = {
                        // when closing entry screen, go back to home and refresh list
                        selectedDate = null
                        homeViewModel.refresh()
                    }
                )
            } else {
                HomeScreen(
                    entryDates = entryDates,
                    hasTodayEntry = hasTodayEntry,
                    onAddClicked = {
                        // open today's entry in edit mode
                        selectedDate = LocalDate.now()
                    },
                    onToggleTheme = { themeViewModel.toggleTheme() },
                    onEntryClicked = { date ->
                        selectedDate = date
                    }
                )
            }
        }
    }
}
