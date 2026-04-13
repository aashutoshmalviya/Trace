# Trace 🚀

> **AI-Powered Codebase Ingestion & Semantic Search**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![Version](https://img.shields.io/badge/version-1.0.0-blue)](#)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](#)

## 📖 Project Overview

**Trace** is a sophisticated local codebase ingestion tool built with Spring Boot and Spring AI. It scans local directories for source code (specifically Java files), breaks the code into searchable chunks using a sliding window technique, and generates vector embeddings via the Google Gemini API. These embeddings are stored in a PostgreSQL database utilizing the `pgvector` extension, enabling powerful semantic search and context-aware interactions with your codebase.

---

## 🖼 Visuals

_(Placeholder for an architectural diagram or screenshot of a semantic search query in action)_

---

## 🛠 Tech Stack

- **Primary Language:** Java
- **Framework:** Spring Boot, Spring AI
- **AI / LLM Integration:** Google GenAI (Gemini `gemini-3-flash-preview` for chat, `text-embedding-004` for vectors)
- **Database:** PostgreSQL with `pgvector` extension
- **Build Tool:** Maven

---

## 🛡️ Privacy & Model Extensibility

Thanks to **Spring AI**'s abstraction layer, Trace is highly flexible and not locked into a single AI provider:

- **Complete Privacy:** You can swap out Gemini for **Ollama** to run open-source models (like Llama 3 or Mistral) entirely locally. This ensures your proprietary source code never leaves your machine!
- **Enhanced Processing:** For handling massive codebases or needing deeper logical reasoning, you can easily integrate other industry-leading models like **OpenAI (GPT-4o)** or **Anthropic (Claude 3.5 Sonnet)**.

---

## ✨ Key Features

- **Automated Code Discovery:** Recursively walks through target local directories to discover `.java` source files.
- **Intelligent Chunking:** Uses `TokenTextSplitter` to break down large files into ~800-1000 token chunks for optimized LLM processing.
- **Contextual Metadata:** Automatically injects rich metadata (project name, file path, and file name) into each document so the LLM knows precisely where the code lives.
- **Vectorization & Storage:** Automatically generates embeddings using Gemini and persists them to PostgreSQL with Cosine Distance vector indexing.
- **Resiliency:** Built-in retry mechanisms for the AI API calls (excluding client-side 4xx errors).

---

## 📂 Project Structure

```text
Trace/
├── src/main/
│   ├── java/com/illusion/trace/
│   │   └── strategy/
│   │       ├── IngestionStrategy.java            # Interface for ingestion handlers
│   │       └── LocalDirectoryIngestionStrategy.java # Core logic for parsing and chunking local Java files
│   └── resources/
│       └── application.yml                       # Application configuration (DB, Spring AI, Gemini)
├── mvnw / mvnw.cmd                               # Maven wrappers for cross-platform builds
└── HELP.md                                       # Default Spring Boot help documentation
```

---

## 🚀 Getting Started

### Prerequisites

Before running Trace, ensure you have the following installed and configured:

- **Java JDK** (17 or higher recommended)
- **PostgreSQL** running locally on port `5433`.
- **pgvector** extension installed in your PostgreSQL instance.
- A valid **Google Gemini API Key**.

### Database Setup

Ensure your PostgreSQL instance has a database named `trace_db` and `pgvector` enabled:

```sql
CREATE DATABASE trace_db;
\c trace_db
CREATE EXTENSION vector;
```

### Installation

1. Clone the repository and navigate into the directory:

```bash
git clone <repository-url> trace
cd trace
```

2. Set the required environment variables:

```bash
export DB_PASSWORD="your_postgres_password"
export GEMINI_API_KEY="your_gemini_api_key"
```

3. Run the application locally using the Maven wrapper:

```bash
# On Linux/macOS:
./mvnw spring-boot:run

# On Windows:
mvnw.cmd spring-boot:run
```

---

## 💻 Usage

While the project is running, the underlying strategy can be invoked to ingest a directory. Here is an example of how the core logic interacts with a target directory:

```java
// Example of internal strategy invocation
LocalDirectoryIngestionStrategy strategy = new LocalDirectoryIngestionStrategy(vectorStore);
String result = strategy.ingest("/path/to/my/project/src", "My Awesome App");
System.out.println(result);
// Output: "Success! Ingested 42 files into 125 searchable chunks."
```

_(Note: If you have exposed this via a REST endpoint or CLI runner, you can invoke it via `curl` or standard input)._

---

## 🗺️ Roadmap / Future Development

As an evolving MVP, the following features are planned to make Trace even more powerful:

- **Remote Repository Support:** Native integration to ingest directly from GitHub or GitLab without needing to clone projects locally.
- **Intelligent AST Chunking:** Replacing basic token splitting with Abstract Syntax Tree (AST) parsing (e.g., Tree-sitter or JavaParser) to chunk code logically by methods and classes, reducing LLM hallucinations.
- **Redis Vector Store:** Adding optional support for Redis as a vector database for ultra-low latency similarity searches alongside PostgreSQL.
- **Interactive Web UI:** Developing a sleek, modern frontend (React/Next.js) to replace CLI/REST interactions with a rich chat interface featuring syntax highlighting and markdown support.

---

## 🤝 Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m 'Add some amazing feature'`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.
