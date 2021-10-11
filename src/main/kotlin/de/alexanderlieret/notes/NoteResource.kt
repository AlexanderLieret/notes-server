package de.alexanderlieret.notes

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class NoteResource(
    val service: NoteService,
    val assembler: NoteModelAssembler,
) {
    private val log: Logger = LoggerFactory.getLogger(NoteResource::class.java)

    @GetMapping("/notes")
    fun index(): CollectionModel<EntityModel<Note>> {
        log.info("GET /notes")
        val notes = service.findNotes()
        return assembler.toCollectionModel(notes)
    }

    @PostMapping("/notes")
    fun post(@RequestBody note: Note): EntityModel<Note> {
        log.info("POST /notes $note")

        note.version = (System.currentTimeMillis() / 1000).toString()
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
    fun update(@RequestBody updated: Note, @PathVariable id: UUID): EntityModel<Note> {
        log.info("PUT /notes/$id $updated")

        if ((updated.id != null) && (updated.id != id)) throw IdNotMatchException(id, updated)

        val note = service.get(id).orElseThrow { NoteNotFoundException(id) }

        updated.id = note.id
        updated.version = (System.currentTimeMillis() / 1000).toString()
        service.post(updated)
        return assembler.toModel(updated)
    }

    @DeleteMapping("/notes/{id}")
    fun delete(@PathVariable id: UUID) {
        log.info("DELETE /notes/$id")
        service.delete(id)
    }
}

@Component
class NoteModelAssembler : RepresentationModelAssembler<Note, EntityModel<Note>> {
    override fun toModel(note: Note): EntityModel<Note> {
        return EntityModel.of(
            note,
//            linkTo { methodOn(NoteResource::class.java).get(note.id!!) }.withSelfRel(),
//            linkTo { methodOn(NoteResource::class.java).index() }.withRel("notes"),
            linkTo(NoteResource::get).withSelfRel(),
            linkTo(NoteResource::index).withRel("notes"),
        )
    }

    override fun toCollectionModel(notes: Iterable<Note>): CollectionModel<EntityModel<Note>> {
        val noteModels: CollectionModel<EntityModel<Note>> = super.toCollectionModel(notes)
//        noteModels.add(linkTo(methodOn(NoteResource::class.java).index()).withSelfRel())
        noteModels.add(linkTo(NoteResource::index).withSelfRel())
        return noteModels
    }
}

interface NoteRepository : CrudRepository<Note, UUID> {
    @Query("select * from Notes")
    fun findNotes(): List<Note>
}

@Service
class NoteService(val db: NoteRepository) {
    fun findNotes(): List<Note> = db.findNotes()

    fun post(note: Note) = db.save(note)

    fun get(id: UUID) = db.findById(id)

    fun delete(id: UUID) = db.deleteById(id)
}
