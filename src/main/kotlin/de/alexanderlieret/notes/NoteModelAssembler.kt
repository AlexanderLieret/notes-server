package de.alexanderlieret.notes

import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Component

@Component
class NoteModelAssembler :
    RepresentationModelAssembler<Note, EntityModel<Note>> {
    override fun toModel(note: Note): EntityModel<Note> {
        return EntityModel.of(
            note,
            linkTo(methodOn(NoteResource::class.java).get(note.id!!)).withSelfRel(),
            linkTo(methodOn(NoteResource::class.java).index()).withRel("notes"),
        )
    }

    override fun toCollectionModel(notes: Iterable<Note>): CollectionModel<EntityModel<Note>> {
        val noteModels: CollectionModel<EntityModel<Note>> = super.toCollectionModel(notes)
        noteModels.add(linkTo(methodOn(NoteResource::class.java).index()).withSelfRel())
        return noteModels
    }
}