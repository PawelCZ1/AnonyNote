package pl.pawelcz.AnonyNote.note.dto;

import pl.pawelcz.AnonyNote.note.Note;

import java.util.UUID;

public record NoteResponse(UUID id, String content) {
    public static NoteResponse fromNote(Note note) {
        return new NoteResponse(note.getId(), note.getContent());
    }
}
