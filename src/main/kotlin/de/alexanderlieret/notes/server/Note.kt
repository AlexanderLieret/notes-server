package de.alexanderlieret.notes.server

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.lang.Long.max
import java.util.*

@Table("Notes")
data class Note(
    @Id val id: UUID?,
    val version: Long = 0,
    val name: String,
    val content: String,
) {
    infix fun merge(note: Note): Note =
        Note(
            id = if (note.id != null) note.id else id,
            version = max(note.version, version),
            name = note.name.ifBlank { name },
            content = note.content.ifBlank { content }
        )

    companion object {
        fun merge(a: Note, b: Note) = a.merge(b)
    }
}
