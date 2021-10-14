package de.alexanderlieret.notes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class NoteResource(
    val service: NoteService,
    val assembler: NoteModelAssembler,
) {
    private val log: Logger = LoggerFactory.getLogger(NoteResource::class.java)
    private fun version(): Long = System.currentTimeMillis() / 1000

    @GetMapping("/notes")
    fun index(): CollectionModel<EntityModel<Note>> {
        log.info("GET /notes")
        val notes = service.findNotes()
        log.info("Found ${notes.size} notes")
        return assembler.toCollectionModel(notes)
    }

    @PostMapping("/notes")
    fun post(@RequestBody input: Note): EntityModel<Note> {
        var note = input
        log.info("POST /notes $note")

        if (note.id?.let { service.get(it).isPresent } == true) note = note.copy(id = UUID.randomUUID())

        note = note.copy(version = version())
        service.post(note)
        return assembler.toModel(note)
    }

    @GetMapping("/notes/{id}")
    fun get(@PathVariable id: UUID): EntityModel<Note> {
        log.info("GET /notes/$id")

        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }
        return assembler.toModel(note)
    }

    @PutMapping("/notes/{id}")
    fun update(@RequestBody update: Note, @PathVariable id: UUID): EntityModel<Note> {
        log.info("PUT /notes/$id $update")

        if ((update.id != null) && (update.id != id)) throw IdNotMatchException(id, update)

        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }

        val updated = note.merge(update.copy(version = version()))
        service.post(updated)
        return assembler.toModel(updated)
    }

    @DeleteMapping("/notes/{id}")
    fun delete(@PathVariable id: UUID) {
        log.info("DELETE /notes/$id")

        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }
        note.id?.let { service.delete(it) }
    }
}
