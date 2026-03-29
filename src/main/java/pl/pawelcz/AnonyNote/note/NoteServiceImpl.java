package pl.pawelcz.AnonyNote.note;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.pawelcz.AnonyNote.core.security.EncryptionService;
import pl.pawelcz.AnonyNote.note.dto.NoteRequest;
import pl.pawelcz.AnonyNote.note.dto.NoteResponse;
import pl.pawelcz.AnonyNote.note.exception.NoteNotFoundException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
        String searchToken = UUID.randomUUID().toString();
        String hashedSearchToken = hashSearchToken(searchToken);
        Note note = Note.builder()
                .id(UUID.randomUUID())
                .content(encryptedContent)
                .searchToken(hashedSearchToken)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        noteRepository.save(note);
        return new NoteResponse(searchToken, request.content());
    }

    @Override
    public NoteResponse getNoteBySearchTokenAndErase(String searchToken) {
        String hashedSearchToken = hashSearchToken(searchToken);
        Query query = Query.query(Criteria.where("searchToken").is(hashedSearchToken));
        Note removed = mongoTemplate.findAndRemove(query, Note.class);

        if (removed == null) {
            throw new NoteNotFoundException(searchToken);
        }

        String decryptedContent = encryptionService.decrypt(removed.getContent());
        return new NoteResponse(searchToken, decryptedContent);
    }

    private String hashSearchToken(String searchToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(searchToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash failed", e);
        }
    }
}
