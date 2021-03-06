package de.alexanderlieret.notes.server

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import java.util.*

object MyMatchers : ArgumentMatchers() {
    fun anyNote(): Note {
        argThat(MyMatcher<Note>())
        return Note(null, 0, "", "")
    }

    fun anyUUID(): UUID {
        argThat(MyMatcher<UUID>())
        return UUID(0, 0)
    }

    internal class MyMatcher<T> : ArgumentMatcher<T> {
        override fun matches(t: T): Boolean = true

        //printed in verification errors
        override fun toString(): String = this::class.qualifiedName ?: "???"
    }
}