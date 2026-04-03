package pl.pawelcz.AnonyNote.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.pawelcz.AnonyNote.note.NoteService;
import pl.pawelcz.AnonyNote.note.dto.NoteRequest;
import pl.pawelcz.AnonyNote.note.dto.NoteResponse;
import pl.pawelcz.AnonyNote.note.exception.NoteExpiredException;
import pl.pawelcz.AnonyNote.note.exception.NoteNotFoundException;

@Controller
public class WebController {

    private final NoteService noteService;

    public WebController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    public String createNote(@RequestParam("content") String content, Model model) {
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("error", "Content cannot be empty.");
            return "index";
        }
        NoteResponse response = noteService.addNote(new NoteRequest(content));
        model.addAttribute("searchToken", response.searchToken());
        return "created";
    }

    @GetMapping("/note/{searchToken}")
    public String viewNote(@PathVariable String searchToken, Model model) {
        try {
            NoteResponse response = noteService.getNoteBySearchTokenAndErase(searchToken);
            model.addAttribute("content", response.content());
            return "note";
        } catch (NoteNotFoundException | NoteExpiredException e) {
            model.addAttribute("error", "Note not found or has already been read/destroyed.");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred.");
            return "error";
        }
    }
}