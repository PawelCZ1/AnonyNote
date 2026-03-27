package pl.pawelcz.AnonyNote.note;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.pawelcz.AnonyNote.core.security.EncryptionService;
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
    private final EncryptionService encryptionService;

    public NoteServiceImpl(NoteRepository noteRepository, MongoTemplate mongoTemplate,
                           EncryptionService encryptionService) {
        this.noteRepository = noteRepository;
        this.mongoTemplate = mongoTemplate;
        this.encryptionService = encryptionService;
    }

    @Override
    public NoteResponse addNote(NoteRequest request) {
        String encryptedContent = encryptionService.encrypt(request.content());
        Note note = Note.builder()
                .id(UUID.randomUUID())
            .content(encryptedContent)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        Note saved = noteRepository.save(note);
        return new NoteResponse(saved.getId(), request.content());
    }

    @Override
    public NoteResponse getNoteByIdAndErase(UUID id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Note removed = mongoTemplate.findAndRemove(query, Note.class);

        if (removed == null) {
            throw new NoteNotFoundException(id);
        }

        String decryptedContent = encryptionService.decrypt(removed.getContent());
        return new NoteResponse(removed.getId(), decryptedContent);
    }
}
