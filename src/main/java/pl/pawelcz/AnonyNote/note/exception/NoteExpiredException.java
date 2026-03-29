package pl.pawelcz.AnonyNote.note.exception;

public class NoteExpiredException extends RuntimeException {
    public NoteExpiredException() {
        super("Note has expired and is no longer available.");
    }
}
