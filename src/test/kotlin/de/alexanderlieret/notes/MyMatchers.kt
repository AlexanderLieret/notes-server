package de.alexanderlieret.notes

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import java.util.*

object MyMatchers : ArgumentMatchers() {
    fun anyNote(): Note {
        argThat(MyMatcher<Note>())
        return Note(null, "", "", "")
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