package de.alexanderlieret.notes

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class NoteResource(val service: NoteService) {
    @GetMapping
    fun index(): List<Note> = service.findNotes()

    @PostMapping
    fun post(@RequestBody note: Note) {
        note.version = (System.currentTimeMillis() / 1000).toString()
        service.post(note)
    }
}

interface NoteRepository : CrudRepository<Note, UUID> {
    @Query("select * from Notes")
    fun findNotes(): List<Note>
}

@Service
class NoteService(val db: NoteRepository) {
    fun findNotes(): List<Note> = db.findNotes()

    fun post(note: Note) {
        db.save(note)
    }
}
