package de.alexanderlieret.notes.server

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
        log.debug("Found ${notes.size} notes")

        return assembler.toCollectionModel(notes)
    }

    @PostMapping("/notes")
    fun add(@RequestBody input: Note): EntityModel<Note> {
        var note = input
        log.info("POST /notes $note")

        // update version and remove id
        note = note.copy(id = null, version = version())
        log.debug("new note $note")

        return assembler.toModel(service.post(note))
    }

    @GetMapping("/notes/{id}")
    fun get(@PathVariable id: UUID): EntityModel<Note> {
        log.info("GET /notes/$id")

        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }
        log.debug("requested note $note")

        return assembler.toModel(note)
    }

    @PutMapping("/notes/{id}")
    fun update(@RequestBody update: Note, @PathVariable id: UUID): EntityModel<Note> {
        log.info("PUT /notes/$id $update")

        // check if request id and payload id match
        if ((update.id != null) && (update.id != id)) throw IdNotMatchException(id, update)

        // check if note exists
        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }
        log.debug("old version $note")

        // merge and update version
        val updated = note.merge(update.copy(version = version()))
        log.debug("merged version $updated")

        return assembler.toModel(service.post(updated))
    }

    @DeleteMapping("/notes/{id}")
    fun delete(@PathVariable id: UUID) {
        log.info("DELETE /notes/$id")

        // check if note does exist
        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }
        log.debug("delete note $note")

        service.delete(note.id!!)
    }
}
