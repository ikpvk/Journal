package com.example.journal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.journal.data.repo.FileJournalRepository
import com.example.journal.ui.home.HomeScreen
import com.example.journal.viewmodel.HomeViewModel
import com.example.journal.viewmodel.HomeViewModelFactory

class MainActivity : ComponentActivity() {

    private val repo by lazy { FileJournalRepository(applicationContext) }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Collect ViewModel state here and pass pure values to HomeScreen
            JournalApp(homeViewModel)
        }
    }
}

@androidx.compose.runtime.Composable
fun JournalApp(viewModel: HomeViewModel) {
    val entryDates by viewModel.entryDates.collectAsState()
    val hasTodayEntry by viewModel.hasTodayEntry.collectAsState()

    MaterialTheme {
        Surface {
            HomeScreen(
                entryDates = entryDates,
                hasTodayEntry = hasTodayEntry,
                onAddClicked = {
                    // TODO: navigate to entry screen (bind to EntryViewModel)
                },
                onToggleTheme = {
                    // TODO: toggle theme persistence (DataStore) and refresh UI
                }
            )
        }
    }
}
