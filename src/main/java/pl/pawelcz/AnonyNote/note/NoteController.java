package pl.pawelcz.AnonyNote.note;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pawelcz.AnonyNote.note.dto.NoteRequest;
import pl.pawelcz.AnonyNote.note.dto.NoteResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{searchToken}")
    public ResponseEntity<NoteResponse> getNoteBySearchTokenAndErase(@PathVariable String searchToken) {
        return ResponseEntity.ok(noteService.getNoteBySearchTokenAndErase(searchToken));
    }

    @PostMapping
    public ResponseEntity<NoteResponse> addNote(@RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.addNote(request));
    }
}

