# Trace 🚀

> **A Local, AI-Powered Architectural Co-Pilot for Your Codebase**

---

## 📖 The "Why"

If you've ever tried to understand a sprawling, undocumented codebase by pasting 15 different files into ChatGPT, you know the pain of lost context and AI hallucinations.

**Trace** solves this. It is a local Retrieval-Augmented Generation (RAG) pipeline designed specifically for software architecture. Instead of isolated snippets, Trace ingests your entire repository into a local vector database. You can then ask complex, system-level questions (e.g., _"How does the Auth service validate JWTs, and which filters are involved?"_) and get highly accurate answers backed by direct code references.

---

## 🏗 System Architecture & Stack

Trace is built with a modern, full-stack enterprise architecture, leveraging the Strategy Pattern for extensibility and Signals for bleeding-edge frontend performance.

- **Backend:** Java 17+, Spring Boot 3.4
- **AI Orchestration:** Spring AI
- **Database:** PostgreSQL + `pgvector` (Cosine Distance Indexing)
- **LLM Engine:** Google Gemini 3 Flash (via GenAI SDK)
- **Frontend:** Angular 20 (Fully Zoneless, Signal-based state management, SCSS)

---

## 🛡️ Privacy First: Bring Your Own Model

Thanks to Spring AI's abstraction layer, Trace is not vendor-locked. While it defaults to Gemini for speed, you can easily swap the provider in your `application.yml`. Need strict enterprise privacy? Swap in **Ollama** to run Llama 3 or Mistral entirely locally—ensuring your proprietary source code never leaves your machine.

---

## ✨ Key Features

- **Automated Code Discovery:** Recursively walks local directories to parse `.java` files (with more languages planned).
- **Intelligent Chunking:** Utilizes a sliding-window token splitter (~800-1000 tokens) to optimize context windows.
- **Contextual Metadata:** Automatically binds project names, file paths, and origins to vectors, eliminating AI spatial confusion.
- **Sleek IDE-Like UI:** A dark-mode, responsive web interface featuring real-time Markdown rendering and syntax-highlighted code blocks.
- **Extensible Design:** Built on a strict `IngestionStrategy` interface, making it trivial to add new ingestion sources (like S3 or direct Git cloning) without touching core logic.

---

## 🚀 5-Minute Quickstart

### 1. Prerequisites

- **Java 17+** & Maven
- **Node.js (v18+)** & npm
- **PostgreSQL** (running on port `5433`) with the `pgvector` extension installed.

### 2. Database Setup

Ensure your local Postgres instance has a database named `trace_db` and the vector extension enabled:

```sql
CREATE DATABASE trace_db;
\c trace_db
CREATE EXTENSION vector;
```

### 3. Environment Variables

Provide your database credentials and API key to the environment:

```bash
export DB_PASSWORD="your_postgres_password"
export GEMINI_API_KEY="your_gemini_api_key"
```

### 4. Boot up the Backend

Clone the repository and spin up the Spring Boot server:

```bash
git clone https://github.com/aashutoshmalviya/Trace.git
cd Trace

# Run the Spring Boot application (defaults to port 8080)
./mvnw spring-boot:run
```

### 5. Boot up the Frontend

In a new terminal window, start the Angular UI:

```bash
cd trace-ui
npm install
npm start
```

Navigate to `http://localhost:4200` in your browser. Click the **"+"** icon in the sidebar to import your first local project directory and start chatting!

---

## 🗺️ Engineering Backlog (What's Next)

Trace is a living project. Here is what is coming down the pipeline:

- [ ] **Semantic AST Chunking:** Upgrading from basic token splitting to Abstract Syntax Tree (AST) parsing (via JavaParser/Tree-sitter) so the LLM receives logically sound blocks (entire methods/classes) rather than arbitrary text cuts.
- [ ] **Direct GitHub/GitLab Ingestion:** Implementing a `GitIngestionStrategy` to bypass local paths—just paste a public repo URL into the UI and let the backend handle the cloning and vectorization.
- [ ] **Redis Vector Store:** Adding optional support for Redis as an alternative to Postgres for ultra-low latency similarity searches at scale.
- [ ] **Dockerization:** Wrapping the UI, API, and Database into a single `docker-compose.yml` for true 1-click zero-config local deployments.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! If you're tackling something from the backlog, please open an issue first to discuss the implementation.

1. **Fork the Project**
2. **Create your Feature Branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your Changes** (`git commit -m 'feat: Add some AmazingFeature'`)
4. **Push to the Branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**
