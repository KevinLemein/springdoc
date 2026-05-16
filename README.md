# springdoc-ai

An AI-powered developer assistant for Spring Boot projects. Point it at your Java source code and get production-grade Javadoc, inline comments, API documentation, and Conventional Commit messages — all generated via Google Gemini.

Built with **Spring Boot 4.x** and **Spring AI**.

---

## Features

- **Code documentation** — generates Javadoc and inline comments for any Java class
- **API documentation** — produces Markdown REST API docs from Spring controllers *(coming soon)*
- **Commit messages** — generates Conventional Commit messages from your staged git diff *(coming soon)*
- **Provider-agnostic** — swap Gemini for any Spring AI-supported model by changing one property

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
│   └── AssistController.java     # REST endpoints
├── dto/
│   ├── AssistRequest.java        # Request payload
│   └── AssistResponse.java       # Standard response envelope
├── service/
│   └── AssistService.java        # AI prompt logic via Spring AI ChatClient
└── SpringdocAiApplication.java   # Entry point
```

---

## Prerequisites

- Java 25+
- Gradle
- A free Google Gemini API key

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/springdoc-ai.git
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

Generates Javadoc and inline comments for a Java class.

**Request body:**

```json
{
  "fileName": "UserService.java",
  "sourceCode": "package com.example;\n\n@Service\npublic class UserService {\n    public String findUserById(Long id) {\n        return \"user-\" + id;\n    }\n}"
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
  "data": "package com.example;\n\n/**\n * Service class responsible for...\n */\n@Service\npublic class UserService {\n\n    /**\n     * Retrieves a user by ID.\n     * @param id the unique identifier\n     * @return a string representing the user\n     */\n    public String findUserById(Long id) {\n        return \"user-\" + id;\n    }\n}",
  "timestamp": "2026-05-16T18:37:11.026Z",
  "requestId": "1a335629-df2e-4fdc-aba9-4f95bc855102"
}
```

**Example curl:**

```bash
curl -X POST http://localhost:8080/api/assist/comment \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "UserService.java",
    "sourceCode": "package com.example;\n\nimport org.springframework.stereotype.Service;\n\n@Service\npublic class UserService {\n\n    public String findUserById(Long id) {\n        return \"user-\" + id;\n    }\n}"
  }'
```

---

## Configuration

All configuration lives in `src/main/resources/application.properties`.

```properties
spring.application.name=springdoc-ai

# AI provider — replace with your key via environment variable
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
```

### Switching models

Replace the model value with any model your API key has access to. To list available models for your key:

```bash
curl "https://generativelanguage.googleapis.com/v1beta/models?key=YOUR_KEY" | grep '"name"'
```

Common options: `gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-2.0-flash-lite`

---

## Security

- **Never commit your API key.** The project is configured to read it from an environment variable.
- Add `application.properties` to your `.gitignore` if you store the key there locally.
- A safe `application.properties.example` with placeholder values is provided for reference.

---

## Roadmap

- [ ] `POST /api/assist/comment/file` — document a file by passing its path on disk
- [ ] `POST /api/assist/commit` — generate a Conventional Commit message from a git diff
- [ ] `POST /api/assist/docs` — generate Markdown API documentation from a controller
- [ ] Global exception handler with structured error responses
- [ ] Support for additional Spring AI providers (OpenAI, Ollama, Groq)

---

## Contributing

Pull requests are welcome. For major changes, open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/your-feature`)
3. Commit using Conventional Commits (`feat: add commit message generator`)
4. Push and open a pull request

---

## License

[MIT](LICENSE)
