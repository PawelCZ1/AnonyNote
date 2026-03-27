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
- MongoDB
- Docker Compose (aplikacja + baza)

## Wymagania

- Docker
- Java 17
- (opcjonalnie) Maven lokalnie, ale wystarczy mvnw z repo

## Uruchomienie w kontenerach (zalecane)

1. Zbuduj i uruchom aplikację oraz bazę danych:

  docker compose up --build -d

2. Sprawdź status kontenerów:

  docker compose ps

3. Podgląd logów aplikacji:

  docker compose logs -f app

4. Zatrzymanie środowiska:

  docker compose down

Usługi i porty:
- app: http://localhost:8080
- mongodb: localhost:27017

## Uruchomienie lokalne (bez kontenera aplikacji)

1. Uruchom samą bazę MongoDB:

  docker compose up -d mongodb

2. Upewnij się, że używasz Java 17.

3. Uruchom aplikację z repozytorium:

  Linux/macOS:
  ./mvnw spring-boot:run

  Windows:
  .\\mvnw.cmd spring-boot:run

Aplikacja domyślnie startuje na http://localhost:8080.

Uwaga:
- w kontenerach aplikacja łączy się z bazą przez nazwę usługi mongodb
- lokalnie aplikacja używa domyślnego URI z pliku konfiguracyjnego

## Konfiguracja

Domyślna konfiguracja znajduje się w [src/main/resources/application.properties](src/main/resources/application.properties).

URI bazy:
- spring.mongodb.uri=${SPRING_MONGODB_URI:${SPRING_DATA_MONGODB_URI:mongodb://root:secret@localhost:27017/mydatabase?authSource=admin}}

W Docker Compose ustawiana jest zmienna:
- SPRING_MONGODB_URI=mongodb://root:secret@mongodb:27017/mydatabase?authSource=admin

## API

Base path: /api/notes

### POST /api/notes
Tworzy notatkę.

Request body:

```json
{
  "content": "To jest tajna notatka"
}
```

Przykład (curl):

curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"content":"To jest tajna notatka"}'

Response 201 Created:

```json
{
  "id": "6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b",
  "content": "To jest tajna notatka"
}
```

---

### GET /api/notes/{id}
Pobiera notatkę i od razu ją usuwa.

Przykład (curl):

curl http://localhost:8080/api/notes/6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b

Response 200 OK (przy pierwszym odczycie):

```json
{
  "id": "6f4dbd56-96d8-4ca2-a7e8-df8f5e07cb7b",
  "content": "To jest tajna notatka"
}
```

Kolejna próba odczytu tego samego id zwróci 404 Not Found.

Przykładowa odpowiedź błędu (application/problem+json):

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

- [src/main/java/pl/pawelcz/AnonyNote/note/NoteController.java](src/main/java/pl/pawelcz/AnonyNote/note/NoteController.java)
- [src/main/java/pl/pawelcz/AnonyNote/note/NoteServiceImpl.java](src/main/java/pl/pawelcz/AnonyNote/note/NoteServiceImpl.java)
- [src/main/java/pl/pawelcz/AnonyNote/note/NoteRepository.java](src/main/java/pl/pawelcz/AnonyNote/note/NoteRepository.java)
- [src/main/java/pl/pawelcz/AnonyNote/core/RestExceptionHandler.java](src/main/java/pl/pawelcz/AnonyNote/core/RestExceptionHandler.java)
- [src/main/resources/application.properties](src/main/resources/application.properties)
- [compose.yaml](compose.yaml)

