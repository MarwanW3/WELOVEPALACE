# WELOVEPALACE

A web application that displays Premier League matches and predicts scores using AI (OpenAI).

## Contributors

- Abdullah Al-Shammari
- Amin Hassan
- Ibrahim Halawani
- Marwan Warsame
---

## How to run the project

### 1. Clone the repository

```bash
git clone https://github.com/MarwanW3/WELOVEPALACE.git
cd WELOVEPALACE
```

---

## 2. Requirements

Make sure you have installed:

- Java 17 or newer
- Gradle

Check installation:

```bash
java -version
gradle -v
```

---

## 3. API Keys (IMPORTANT)

This project requires two API keys:

- Football API: https://www.football-data.org/
- OpenAI API: https://platform.openai.com/

### IMPORTANT (Choose ONE of the following methods)

The application uses environment variables, not hardcoded keys.

To run the application you need to create a .env file with the following variables FOOTBALL_API_KEY=Your_Key_Here
OPENAI_API_KEY=Your_Key_Here

If you do not set these correctly, the app will fail with errors like:

- 401 Unauthorized
- Could not resolve placeholder OPENAI_API_KEY

---

## 4. Set API keys

### Mac / Linux

Open terminal and run:

```bash
export FOOTBALL_API_KEY=your_football_api_key
export OPENAI_API_KEY=your_openai_api_key
```

Then run the application:

```bash
./gradlew bootRun
```

---

### Windows (PowerShell)

Run:

```powershell
setx FOOTBALL_API_KEY "your_football_api_key"
setx OPENAI_API_KEY "your_openai_api_key"
```

Then restart your terminal or IDE.

Run the application:

```powershell
gradlew.bat bootRun
```

---

### Alternative (IntelliJ)

You can also set environment variables in:

Run → Edit Configurations → Environment variables

Example:

```
FOOTBALL_API_KEY=place_key;OPENAI_API_KEY=place_key

---

## 5. Open the application

```
http://localhost:8080
```

---

## API Documentation (Swagger)

Access the API documentation here:

```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoint

### GET /api/predict

Query parameter:

```
matchIndex (int)
```

Response:

```json
{
  "homeScore": number,
  "awayScore": number,
  "explanation": string
}
```

---

## Notes


- Link to repository: https://github.com/MarwanW3/WELOVEPALACE 
- If you receive errors
- 401 Unauthorized → invalid OpenAI key
- undefined - undefined → API request failed
- Could not resolve placeholder → environment variables not set correctly  
