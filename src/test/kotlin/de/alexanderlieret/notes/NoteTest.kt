package de.alexanderlieret.notes

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class NoteTest {

    private val note = Note(UUID.randomUUID(), 17, "Foo", "Bar")

    @Test
    fun merge_name(){
        val update = Note(null, 42, "New name", " ")
        val expected = note.copy(version = 42, name = "New name")
        assertEquals(note.merge(update), expected)
    }

    @Test
    fun merge_content(){
        val update = Note(null, 1, " ", "Lorem Ipsum")
        val expected = note.copy(content = "Lorem Ipsum")
        assertEquals(note.merge(update), expected)
    }

    @Test
    fun merge_nothing(){
        val update = Note(null, 1, " ", "")
        assertEquals(note.merge(update), note)
        assertEquals(Note.merge(note, update), note)
        assertEquals(note merge update, note)
    }

    @Test
    fun merge_id(){
        val uuid = UUID.randomUUID()
        val update = Note(uuid, 0, " ", "")
        val expected = note.copy(id = uuid)
        assertEquals(note.merge(update), expected)
    }
}