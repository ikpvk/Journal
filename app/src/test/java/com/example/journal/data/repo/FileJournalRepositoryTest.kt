package com.example.journal.data.repo

import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import java.io.File
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileJournalRepositoryTest {

    private lateinit var tempDir: File
    private lateinit var repo: FileJournalRepository

    @BeforeAll
    fun setupAll() {
        tempDir = createTempDir(prefix = "journalTest_")
        repo = FileJournalRepository(tempDir)
    }

    @AfterAll
    fun teardownAll() {
        tempDir.deleteRecursively()
    }

    @BeforeEach
    fun clearDir() {
        tempDir.listFiles()?.forEach { it.delete() }
    }

    @Test
    fun `save and read entry works`() = runBlocking {
        val date = LocalDate.now()
        val content = "Today was a good day."
        val entry = JournalEntry(date, content)
        repo.saveEntry(entry)

        val loaded = repo.readEntry(date)
        assertEquals(content, loaded?.content)
    }

    @Test
    fun `save blank entry deletes file`() = runBlocking {
        val date = LocalDate.now()
        repo.saveEntry(JournalEntry(date, "non-empty"))
        repo.saveEntry(JournalEntry(date, "")) // blank -> delete
        assertNull(repo.readEntry(date))
        val files = repo.listEntryDatesDescending()
        assertTrue(files.isEmpty(), "Empty file should not appear in list")
    }

    @Test
    fun `listEntryDatesDescending returns sorted dates`() = runBlocking {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val tomorrow = today.plusDays(1)

        repo.saveEntry(JournalEntry(yesterday, "yesterday"))
        repo.saveEntry(JournalEntry(today, "today"))
        repo.saveEntry(JournalEntry(tomorrow, "tomorrow"))

        val result = repo.listEntryDatesDescending()
        assertEquals(listOf(tomorrow, today, yesterday), result)
    }
}
