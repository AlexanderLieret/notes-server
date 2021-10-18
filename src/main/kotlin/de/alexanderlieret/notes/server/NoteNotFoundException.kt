package de.alexanderlieret.notes.server

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*


class NoteNotFoundException(id: UUID?) : RuntimeException("Could not find note $id")


@ControllerAdvice
internal class NoteNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(NoteNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun employeeNotFoundHandler(ex: NoteNotFoundException) = ex.message
}
