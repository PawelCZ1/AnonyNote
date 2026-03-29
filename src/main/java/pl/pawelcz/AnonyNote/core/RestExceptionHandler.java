package pl.pawelcz.AnonyNote.core;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pawelcz.AnonyNote.note.exception.NoteExpiredException;
import pl.pawelcz.AnonyNote.note.exception.NoteNotFoundException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NoteNotFoundException.class)
    public ProblemDetail handleNoteNotFound(NoteNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(404);
        pd.setTitle("Note Not Found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(NoteExpiredException.class)
    public ProblemDetail handleNoteExpired(NoteExpiredException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(410);
        pd.setTitle("Note Expired");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
