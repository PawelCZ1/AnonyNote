package pl.pawelcz.AnonyNote.note;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("notes")
public class Note {
    @Id
    private UUID id;

    private String content;

    private String searchToken;

    @CreatedDate
    private Instant createdAt;

    private Instant expiresAt;
}
