package pl.pawelcz.AnonyNote.note;

import pl.pawelcz.AnonyNote.note.dto.NoteRequest;
import pl.pawelcz.AnonyNote.note.dto.NoteResponse;

import java.util.UUID;

public interface NoteService {
    NoteResponse addNote(NoteRequest request);
    NoteResponse getNoteByIdAndErase(UUID id);
}
