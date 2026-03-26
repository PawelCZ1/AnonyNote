package pl.pawelcz.AnonyNote.note.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(UUID id) {
        super("Note with id " + id + " not found");
    }
}
