package com.example.journal.data.repo

import android.content.Context
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface JournalRepository {
    suspend fun listEntryDatesDescending(): List<LocalDate>
    suspend fun readEntry(date: LocalDate): JournalEntry?
    suspend fun saveEntry(entry: JournalEntry)
    suspend fun deleteEntry(date: LocalDate)
}

class FileJournalRepository(
    private val appContext: Context
) : JournalRepository {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val entriesDir: File by lazy {
        File(appContext.filesDir, "entries").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun filenameFor(date: LocalDate): String =
        "${date.format(dateFormatter)}.txt"

    private fun fileFor(date: LocalDate): File =
        File(entriesDir, filenameFor(date))

    override suspend fun listEntryDatesDescending(): List<LocalDate> = withContext(Dispatchers.IO) {
        val files = entriesDir.listFiles { f -> f.isFile && f.name.endsWith(".txt") } ?: arrayOf()
        val dates = files.mapNotNull { file ->
            runCatching {
                val name = file.name.removeSuffix(".txt")
                LocalDate.parse(name, dateFormatter)
            }.getOrNull()
        }
        // sort newest first (descending)
        dates.sortedByDescending { it }
    }

    override suspend fun readEntry(date: LocalDate): JournalEntry? = withContext(Dispatchers.IO) {
        val f = fileFor(date)
        if (!f.exists()) return@withContext null
        val content = f.readText(Charsets.UTF_8)
        JournalEntry(date = date, content = content)
    }

    override suspend fun saveEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        val f = fileFor(entry.date)
        // overwrite with plain text content (simple behavior for draft/sample)
        f.writeText(entry.content, Charsets.UTF_8)
    }

    override suspend fun deleteEntry(date: LocalDate) = withContext(Dispatchers.IO) {
        val f = fileFor(date)
        if (f.exists()) {
            f.delete()
        }
    }
}
