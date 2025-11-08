package com.example.journal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.data.repo.FileJournalRepository
import com.example.journal.ui.entry.EntryScreen
import com.example.journal.ui.home.HomeScreen
import com.example.journal.viewmodel.EntryViewModel
import com.example.journal.viewmodel.EntryViewModelFactory
import com.example.journal.viewmodel.HomeViewModel
import com.example.journal.viewmodel.HomeViewModelFactory

class MainActivity : ComponentActivity() {

    // single repo instance for HomeViewModel (applicationContext is safe here)
    private val repo by lazy { FileJournalRepository(applicationContext) }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JournalApp(homeViewModel)
        }
    }
}

@Composable
fun JournalApp(homeViewModel: HomeViewModel) {
    // collect ViewModel state
    val entryDates by homeViewModel.entryDates.collectAsState()
    val hasTodayEntry by homeViewModel.hasTodayEntry.collectAsState()

    // simple navigation state: false = Home, true = Entry screen
    var showEntryScreen by remember { mutableStateOf(false) }

    // create an EntryViewModel via Compose helper + factory
    val context = LocalContext.current.applicationContext
    val repoForEntry = remember { FileJournalRepository(context) }
    val entryVm: EntryViewModel = viewModel(
        factory = EntryViewModelFactory(repoForEntry, java.time.LocalDate.now())
    )

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (showEntryScreen) {
                // Entry screen: bind to EntryViewModel's state
                val content by entryVm.content.collectAsState()
                EntryScreen(
                    date = java.time.LocalDate.now(),
                    content = content,
                    onContentChange = { entryVm.onContentChanged(it) },
                    onBack = {
                        // return to home and refresh list to show today's entry
                        showEntryScreen = false
                        homeViewModel.refresh()
                    }
                )
            } else {
                HomeScreen(
                    entryDates = entryDates,
                    hasTodayEntry = hasTodayEntry,
                    onAddClicked = {
                        // open today's entry (per requirement)
                        showEntryScreen = true
                    },
                    onToggleTheme = {
                        // TODO: implement theme toggle persistence (DataStore or SharedPreferences)
                    }
                )
            }
        }
    }
}
