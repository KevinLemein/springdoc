# springdoc-ai

An AI-powered developer assistant for Spring Boot projects. Point it at your Java source code and get production-grade Javadoc, inline comments, API documentation, and Conventional Commit messages — all generated via Google Gemini.

Built with **Spring Boot 4.x** and **Spring AI**.

---

## Features

- **Code documentation** — generate Javadoc and inline comments from raw source or a file path
- **Write to disk** — document a file and write the result back in-place with automatic `.bak` backup
- **Batch documentation** — document every `.java` file in a directory in one request
- **Commit messages** — generate Conventional Commit messages from your staged git diff
- **API documentation** — generate Markdown REST API docs from any Spring controller
- **Structured error responses** — consistent JSON envelope on every success and failure

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1.x (SNAPSHOT) |
| AI Integration | Spring AI 2.0.0-M6 |
| AI Provider | Google Gemini 2.5 Flash |
| Language | Java 25 |
| Build | Gradle |

---

## Project Structure

```
src/main/java/com/quavo/springdoc_ai/
├── controller/
│   └── AssistController.java       # All REST endpoints
├── dto/
│   ├── AssistRequest.java          # Raw source comment request
│   ├── AssistResponse.java         # Standard response envelope (record)
│   ├── BatchFileResult.java        # Per-file result in a batch operation
│   ├── BatchRequest.java           # Batch directory request
│   ├── BatchResponse.java          # Batch operation summary
│   ├── CommitRequest.java          # Commit message request
│   ├── DocsRequest.java            # API docs request
│   ├── FileAssistRequest.java      # File path comment request
│   └── WriteResponse.java          # Write-to-disk result
├── exception/
│   └── GlobalExceptionHandler.java # Structured error handling
├── service/
│   └── AssistService.java          # All AI prompt logic via Spring AI ChatClient
└── SpringdocAiApplication.java     # Entry point
```

---

## Prerequisites

- Java 21+
- Gradle
- A free Google Gemini API key

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/KevinLemein/springdoc-ai.git
cd springdoc-ai
```

### 2. Get a free Gemini API key

1. Go to [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click **Create API key → Create API key in new project**
4. Copy the generated key

No credit card required. The free tier includes generous daily limits.

### 3. Configure your API key

Set the key as an environment variable — never hardcode it in `application.properties`.

**Linux / macOS:**
```bash
export GEMINI_API_KEY=your_key_here
```

**Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=your_key_here
```

**IntelliJ IDEA:**
Go to **Run → Edit Configurations → Environment Variables** and add:
```
GEMINI_API_KEY=your_key_here
```

### 4. Run the application

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8080`.

---

## API Reference

### POST `/api/assist/comment`

Generate Javadoc and inline comments from raw Java source code.

**Request body:**
```json
{
  "sourceCode": "package com.example;\n\n@Service\npublic class UserService {\n    public String findUserById(Long id) {\n        return \"user-\" + id;\n    }\n}",
  "fileName": "UserService.java"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `sourceCode` | string | yes | Raw Java source code to document |
| `fileName` | string | no | File name — helps the AI understand context |

**Response:**
```json
{
  "success": true,
  "message": "Documentation generated successfully",
  "data": "package com.example;\n\n/**\n * Service class...\n */\n@Service\npublic class UserService {\n\n    /**\n     * Retrieves a user by ID.\n     * @param id the unique identifier\n     * @return a string representing the user\n     */\n    public String findUserById(Long id) {\n        return \"user-\" + id;\n    }\n}",
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "bf521e74-5479-4d3a-aee0-9b14be46d114"
}
```

---

### POST `/api/assist/comment/file`

Generate Javadoc and inline comments by pointing at a file on disk. Returns the documented source — does not write to disk.

**Request body:**
```json
{
  "filePath": "/absolute/path/to/UserService.java"
}
```

---

### POST `/api/assist/comment/file/write`

Document a file and write the result back to disk in-place. Creates a `.bak` backup before overwriting.

**Request body:**
```json
{
  "filePath": "/absolute/path/to/UserService.java"
}
```

**Response:**
```json
{
  "success": true,
  "message": "File documented and written to disk. Backup created at: /path/to/UserService.java.bak",
  "data": {
    "filePath": "/absolute/path/to/UserService.java",
    "backupPath": "/absolute/path/to/UserService.java.bak",
    "methodsDocumented": 5
  },
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "34802d58-abd8-4661-944f-e29b8c658734"
}
```

---

### POST `/api/assist/comment/batch`

Document every `.java` file in a directory tree. Writes each file to disk with a `.bak` backup. Skips test files. Adds a 1-second delay between files to respect Gemini free tier rate limits.

**Request body:**
```json
{
  "directoryPath": "/absolute/path/to/src/main/java/com/example/service"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Batch complete. 8/9 files documented successfully.",
  "data": {
    "totalFiles": 9,
    "succeeded": 8,
    "failed": 1,
    "results": [
      {
        "filePath": "/path/to/UserService.java",
        "success": true,
        "methodsDocumented": 4,
        "error": null
      },
      {
        "filePath": "/path/to/BrokenService.java",
        "success": false,
        "methodsDocumented": 0,
        "error": "Failed to generate content"
      }
    ]
  },
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "23470871-02c0-420c-b503-5dc0b4cbc681"
}
```

---

### POST `/api/assist/commit`

Generate a Conventional Commit message from a staged git diff.

**Request body:**
```json
{
  "diff": "diff --git a/src/...",
  "branch": "feat/user-service",
  "recentCommits": "feat(auth): add JWT validation\nfix(payment): handle M-Pesa timeout"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `diff` | string | yes | Output of `git diff --staged` |
| `branch` | string | no | Current branch name |
| `recentCommits` | string | no | Recent commit messages for style matching |

**Response:**
```json
{
  "success": true,
  "message": "Commit message generated successfully",
  "data": "feat(assist): add batch directory documentation endpoint\n\nProcesses all .java files in a directory tree, writing documented\nversions to disk with automatic .bak backups. Includes a 1s delay\nbetween files to respect Gemini free tier rate limits.",
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "6b7c6155-12a7-4fb5-bb09-1ac36da4324c"
}
```

**Tip — pipe your real diff directly:**
```bash
curl -X POST http://localhost:8080/api/assist/commit \
  -H "Content-Type: application/json" \
  -d "{
    \"diff\": $(git diff --staged | jq -Rs .),
    \"branch\": \"$(git branch --show-current)\",
    \"recentCommits\": $(git log -5 --pretty=format:'%s' | jq -Rs .)
  }"
```

---

### POST `/api/assist/docs`

Generate Markdown REST API documentation from a Spring controller file.

**Request body:**
```json
{
  "filePath": "/absolute/path/to/UserController.java"
}
```

**Response:**
```json
{
  "success": true,
  "message": "API documentation generated successfully",
  "data": "## User Controller\n\n### POST /api/users\nCreates a new user...\n\n### GET /api/users/{id}\nRetrieves a user by ID...",
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "630b7bfe-a10c-4c82-bab1-0f17ffbecdee"
}
```

---

## Error Responses

All errors return the same envelope with `success: false`:

```json
{
  "success": false,
  "message": "Invalid request",
  "error": "File not found: /path/to/Missing.java",
  "timestamp": "2026-05-17T07:00:00.000Z",
  "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

| Status | Cause |
|---|---|
| `400` | File not found, not a `.java` file, blank source code, validation failure |
| `500` | AI call failure, file read/write error, unexpected exception |

---

## Configuration

```properties
spring.application.name=springdoc-ai
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
spring.jackson.default-property-inclusion=non_null
```

### Switching models

List models available for your key:
```bash
curl "https://generativelanguage.googleapis.com/v1beta/models?key=YOUR_KEY" | grep '"name"'
```

Common options: `gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-2.0-flash-lite`

---

## Security

- **Never commit your API key.** Read it from an environment variable as shown above.
- Add `*.bak` and `application.properties` to `.gitignore`.
- A safe `application.properties.example` with placeholder values is provided for reference.

---

## Roadmap

- [ ] CLI wrapper — run `springdoc comment UserService.java` from your terminal
- [ ] Support for additional Spring AI providers (OpenAI, Ollama, Groq)
- [ ] Configurable batch concurrency and retry on rate limit
- [ ] `POST /api/assist/review` — AI code review and suggestions
- [ ] `POST /api/assist/test` — generate JUnit 5 test stubs

---

## Contributing

Pull requests are welcome. For major changes, open an issue first to discuss what you would like to change.

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/your-feature`)
3. Commit using Conventional Commits (`feat: add commit message generator`)
4. Push and open a pull request

---

## License

[MIT](LICENSE)
