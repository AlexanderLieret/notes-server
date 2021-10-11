package de.alexanderlieret.notes

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*


class IdNotMatchException(id: UUID, note: Note) : RuntimeException("Request id $id does not match body id ${note.id}")


@ControllerAdvice
internal class IdNotMatchAdvice {
    @ResponseBody
    @ExceptionHandler(IdNotMatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun idNotMatchHandler(ex: IdNotMatchException) = ex.message
}
