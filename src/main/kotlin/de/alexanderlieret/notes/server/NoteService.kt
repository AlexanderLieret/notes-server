package de.alexanderlieret.notes.server

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*

@Service
class NoteService(val db: NoteRepository) {
    fun findNotes(): List<Note> = db.findNotes()

    fun post(note: Note) = db.save(note)

    fun get(id: UUID) = db.findById(id)

    fun delete(id: UUID) = db.deleteById(id)
}

@Repository
interface NoteRepository : CrudRepository<Note, UUID> {
    @Query("select * from Notes")
    fun findNotes(): List<Note>
}
