package de.alexanderlieret.notes

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("Notes")
data class Note(
    @Id var id: UUID?,
    var version: String?,
    var name: String,
    var content: String,
)


