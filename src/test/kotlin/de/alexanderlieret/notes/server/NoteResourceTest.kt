package de.alexanderlieret.notes.server

import com.fasterxml.jackson.databind.ObjectMapper
import de.alexanderlieret.notes.server.MyMatchers.anyNote
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.AdditionalAnswers.returnsFirstArg
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(NoteResource::class)
class NoteResourceTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var noteRepository: NoteRepository

    @MockBean
    lateinit var noteService: NoteService

    @MockBean
    lateinit var noteModelAssembler: NoteModelAssembler

    private val note1 = Note(UUID.fromString("ffc86e89-eafe-4b3b-bb92-2cf639765c25"), 1, "Test note 1", "lorem ipsum")
    private val note2 = Note(UUID.fromString("a06e2965-1679-4428-9853-06ef8c3cb24d"), 2, "Test note 2", "lorem ipsum")
    private val note3 = Note(UUID.fromString("e2821e3a-87f6-47a3-9518-d91a79f742f6"), 3, "Test note 3", "lorem ipsum")

    @Test
    fun index_success() {
        val notes = listOf(note1, note2, note3)
        Mockito.`when`(noteService.findNotes()).thenReturn(notes)

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/notes")
                .contentType("application/hal+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$._embedded.noteList", hasSize<List<Note>>(3)))
            .andExpect(jsonPath("$._embedded.noteList[1].name", `is`("Test note 2")))
    }

    @Test
    fun get_success() {
        Mockito.`when`(noteService.get(note3.id!!)).thenReturn(Optional.of(note3))

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/notes/${note3.id}")
                .contentType("application/hal+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.name", `is`("Test note 3")))
    }

    @Test
    fun get_noteNotFound() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/notes/${UUID.randomUUID()}")
                .contentType("application/hal+json")
        )
            .andExpect(status().isNotFound)
            .andExpect { result -> assertTrue(result.resolvedException is NoteNotFoundException) }
            .andExpect(content().string(containsString("Could not find note")))
    }

    @Test
    fun create_success() {
        Mockito.`when`(noteService.post(anyNote())).thenAnswer(returnsFirstArg<Note>())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/notes")
                .contentType("application/hal+json")
                .accept("application/hal+json")
                .content(this.mapper.writeValueAsString(note2))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.name", `is`(note2.name)))
            .andExpect(jsonPath("$.content", `is`(note2.content)))
            .andExpect(jsonPath("$.id", `is`(note2.id.toString())))
            .andExpect(jsonPath("$.version", greaterThan(note2.version), Long::class.java))
    }

    @Test
    fun create_overwriteProtection() {
        Mockito.`when`(noteService.get(note3.id!!)).thenReturn(Optional.of(note3))
        Mockito.`when`(noteService.post(anyNote())).thenAnswer(returnsFirstArg<Note>())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/notes")
                .contentType("application/hal+json")
                .accept("application/hal+json")
                .content(this.mapper.writeValueAsString(note3))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.name", `is`(note3.name)))
            .andExpect(jsonPath("$.content", `is`(note3.content)))
            .andExpect(jsonPath("$.id", not(note3.id.toString())))
            .andExpect(jsonPath("$.version", greaterThan(note3.version), Long::class.java))
    }

    @Test
    fun update_success() {
        val note = note2.copy(name = "Foo bar")

        Mockito.`when`(noteService.get(note2.id!!)).thenReturn(Optional.of(note2))
        Mockito.`when`(noteService.post(anyNote())).thenAnswer(returnsFirstArg<Note>())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/notes/${note.id}")
                .contentType("application/hal+json")
                .accept("application/hal+json")
                .content(this.mapper.writeValueAsString(note))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.name", `is`(note.name)))
            .andExpect(jsonPath("$.content", `is`(note.content)))
            .andExpect(jsonPath("$.id", `is`(note.id.toString())))
            .andExpect(jsonPath("$.version", greaterThan(note.version), Long::class.java))
    }

    @Test
    fun update_noteNotFound() {
        val note = note2.copy(name = "Foo bar")

        Mockito.`when`(noteService.post(anyNote())).thenAnswer(returnsFirstArg<Note>())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/notes/${note.id}")
                .contentType("application/hal+json")
                .accept("application/hal+json")
                .content(this.mapper.writeValueAsString(note))
        )
            .andExpect(status().isNotFound)
            .andExpect { result -> assertTrue(result.resolvedException is NoteNotFoundException) }
            .andExpect(content().string(containsString("Could not find note")))
    }

    @Test
    fun update_wrongId() {
        val note = note2.copy(name = "Foo bar")

        Mockito.`when`(noteService.get(note2.id!!)).thenReturn(Optional.of(note2))
        Mockito.`when`(noteService.get(note3.id!!)).thenReturn(Optional.of(note3))
        Mockito.`when`(noteService.post(anyNote())).thenAnswer(returnsFirstArg<Note>())

        mockMvc.perform(
            MockMvcRequestBuilders.put("/notes/${note3.id}")
                .contentType("application/hal+json")
                .accept("application/hal+json")
                .content(this.mapper.writeValueAsString(note))
        )
            .andExpect(status().isBadRequest)
            .andExpect { result -> assertTrue(result.resolvedException is IdNotMatchException) }
            .andExpect(content().string(containsString("does not match body id")))
    }


    @Test
    fun delete_success() {
        Mockito.`when`(noteService.get(note2.id!!)).thenReturn(Optional.of(note2))

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/notes/${note2.id}")
                .contentType("application/hal+json")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").doesNotExist())
    }

    @Test
    fun delete_noteNotFound() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/notes/${UUID.randomUUID()}")
                .contentType("application/hal+json")
        )
            .andExpect(status().isNotFound)
            .andExpect { result -> assertTrue(result.resolvedException is NoteNotFoundException) }
            .andExpect(content().string(containsString("Could not find note")))
    }
}
