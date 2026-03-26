# AnonyNote

Prosta aplikacja Spring Boot do tworzenia anonimowych notatek.

## Co robi aplikacja

- tworzy notatkę i zwraca jej `id`
- pozwala odczytać notatkę po `id`
- po odczycie usuwa notatkę (one-time read)

## Stack

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data MongoDB
- MongoDB (Docker Compose)

## Wymagania

- Docker Desktop
- Java 17
- (opcjonalnie) Maven lokalnie, ale wystarczy `mvnw` z repo

## Uruchomienie lokalne

### 1) Uruchom MongoDB z Docker Compose

Upewnij się, że w `compose.yaml` masz stałe mapowanie portu:

```yaml
ports:
  - "27017:27017"
```

Następnie uruchom kontener:

```powershell
docker compose up -d
docker compose ps
```

### 2) Skonfiguruj połączenie do MongoDB

W `src/main/resources/application.properties` ustaw:

```properties
spring.application.name=AnonyNote
spring.data.mongodb.uri=mongodb://root:secret@localhost:27017/mydatabase?authSource=admin
spring.data.mongodb.uuid-representation=standard
```

> `uuid-representation=standard` jest wymagane przy `UUID` jako `@Id`.

### 3) Uruchom aplikację

```powershell
.\mvnw.cmd spring-boot:run
```

Aplikacja domyślnie startuje na `http://localhost:8080`.

## API

Base path: `/api/notes`

### POST `/api/notes`
Tworzy notatkę.

Request body:

```json
{
  "content": "To jest tajna notatka"
}
```

Przykład (PowerShell):

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/notes" -ContentType "application/json" -Body '{"content":"To jest tajna notatka"}'
```

Response `201 Created`:

```json
{
  "id": "6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b",
  "content": "To jest tajna notatka"
}
```

---

### GET `/api/notes/{id}`
Pobiera notatkę i od razu ją usuwa.

Przykład:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/notes/6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b"
```

Response `200 OK` (przy pierwszym odczycie):

```json
{
  "id": "6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b",
  "content": "To jest tajna notatka"
}
```

Kolejna próba odczytu tego samego `id` zwróci `404 Not Found`.

Przykładowa odpowiedź błędu (`application/problem+json`):

```json
{
  "type": "about:blank",
  "title": "Note Not Found",
  "status": 404,
  "detail": "Note with id 6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b not found",
  "instance": "/api/notes/6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b"
}
```

## Szybki test scenariusza

1. `POST /api/notes` i skopiuj `id`.
2. `GET /api/notes/{id}` -> otrzymasz notatkę.
3. Ponów `GET /api/notes/{id}` -> dostaniesz `404`.

## Struktura projektu (najważniejsze pliki)

- `src/main/java/pl/pawelcz/AnonyNote/note/NoteController.java`
- `src/main/java/pl/pawelcz/AnonyNote/note/NoteServiceImpl.java`
- `src/main/java/pl/pawelcz/AnonyNote/note/NoteRepository.java`
- `src/main/java/pl/pawelcz/AnonyNote/core/RestExceptionHandler.java`
- `src/main/resources/application.properties`
- `compose.yaml`

