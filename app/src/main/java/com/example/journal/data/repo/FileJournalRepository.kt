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

/**
 * FileJournalRepository can be constructed in two ways:
 *  - FileJournalRepository(rootDir: File)  -> useful for unit tests
 *  - FileJournalRepository(appContext: Context) -> convenience for runtime code
 *
 * Files are stored directly inside the provided rootDir (no additional "entries" subfolder
 * is created when constructing with File).
 */
class FileJournalRepository(
    private val rootDir: File
) : JournalRepository {

    constructor(appContext: Context) : this(File(appContext.filesDir, "entries").apply { if (!exists()) mkdirs() })

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun filenameFor(date: LocalDate): String =
        "${date.format(dateFormatter)}.txt"

    private fun fileFor(date: LocalDate): File =
        File(rootDir, filenameFor(date))

    override suspend fun listEntryDatesDescending(): List<LocalDate> = withContext(Dispatchers.IO) {
        val files = rootDir.listFiles { f -> f.isFile && f.name.endsWith(".txt") } ?: arrayOf()
        val dates = files.mapNotNull { file ->
            // ignore zero-length files (treat as non-existent)
            if (file.length() == 0L) return@mapNotNull null
            runCatching {
                val name = file.name.removeSuffix(".txt")
                LocalDate.parse(name, dateFormatter)
            }.getOrNull()
        }
        // sort newest first (descending)
        dates.sortedByDescending { it }
    }

    override suspend fun readEntry(date: LocalDate): JournalEntry? = withContext(Dispatchers.IO) {
        try {
            val f = fileFor(date)
            if (!f.exists() || f.length() == 0L) return@withContext null
            val content = f.readText(Charsets.UTF_8)
            JournalEntry(date = date, content = content)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    override suspend fun saveEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        try {
            val f = fileFor(entry.date)
            val content = entry.content
            // If content is blank -> delete the file instead of writing empty content
            if (content.isBlank()) {
                if (f.exists()) f.delete()
                return@withContext
            }

            // Atomic write: write to temp file and rename
            val tmp = File(rootDir, "${f.name}.tmp")
            tmp.writeText(content, Charsets.UTF_8)

            // Attempt atomic rename; if that fails, fallback to overwrite
            if (!tmp.renameTo(f)) {
                f.writeText(content, Charsets.UTF_8)
                tmp.delete()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override suspend fun deleteEntry(date: LocalDate) = withContext(Dispatchers.IO) {
        try {
            val f = fileFor(date)
            if (f.exists()) {
                f.delete()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
