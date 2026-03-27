package pl.pawelcz.AnonyNote.note.dto;

import pl.pawelcz.AnonyNote.note.Note;

import java.util.UUID;

public record NoteResponse(UUID id, String content) {
}
