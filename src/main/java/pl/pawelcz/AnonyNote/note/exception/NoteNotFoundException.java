package pl.pawelcz.AnonyNote.note.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String searchToken) {
        super("Note with token " + searchToken + " not found");
    }
}
