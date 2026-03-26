package pl.pawelcz.AnonyNote.note;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.pawelcz.AnonyNote.note.dto.NoteRequest;
import pl.pawelcz.AnonyNote.note.dto.NoteResponse;
import pl.pawelcz.AnonyNote.note.exception.NoteNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final MongoTemplate mongoTemplate;

    public NoteServiceImpl(NoteRepository noteRepository, MongoTemplate mongoTemplate) {
        this.noteRepository = noteRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public NoteResponse addNote(NoteRequest request) {
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .content(request.content())
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        return NoteResponse.fromNote(noteRepository.save(note));
    }

    @Override
    public NoteResponse getNoteByIdAndErase(UUID id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Note removed = mongoTemplate.findAndRemove(query, Note.class);

        if (removed == null) {
            throw new NoteNotFoundException(id);
        }

        return NoteResponse.fromNote(removed);
    }
}
