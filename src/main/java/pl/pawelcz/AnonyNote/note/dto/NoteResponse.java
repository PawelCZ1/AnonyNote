package pl.pawelcz.AnonyNote.note.dto;

import pl.pawelcz.AnonyNote.note.Note;

import java.util.UUID;

public record NoteResponse(String searchToken, String content) {
}
