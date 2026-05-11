# WELOVEPALACE

A mashup web application that combines Premier League match data with AI-generated score predictions.

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

The application uses environment variables for secure configuration.

Required variables:

FOOTBALL_API_KEY=your_key_here  
OPENAI_API_KEY=your_key_here

You can configure these variables either:

- directly in your terminal
- in your IDE run configuration

Example (IntelliJ):

Run → Edit Configurations → Environment variables

FOOTBALL_API_KEY=your_key;OPENAI_API_KEY=your_key

If the variables are missing or incorrect, the application may fail with errors such as:

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

To run the application:

```bash
./gradlew build
./gradlew bootRun
```

---

### Windows (Powershell)

```powershell
setx FOOTBALL_API_KEY "your_football_api_key"
setx OPENAI_API_KEY "your_openai_api_key"
```

Then restart your terminal or IDE for changes to take effect.

Run the application:

```powershell
gradlew.bat build
gradlew.bat bootRun
```

---

### Alternative (IntelliJ)

You can also set environment variables in:

Run → Edit Configurations → Environment variables

Example:

```
FOOTBALL_API_KEY=your_key_here;OPENAI_API_KEY=your_key_here
```

## 5. Open the application

```
http://localhost:8080
```

---

## API Documentation (Swagger)

Access the API documentation here:

```
http://localhost:8080/swagger-ui/index.html
```
### GET /api/matches/top5

Returns the 5 upcoming Premier League matches together with AI-generated predictions and explanations.

Response example:

```json
[
  {
    "homeTeam": "Arsenal",
    "awayTeam": "Chelsea",
    "predictedHomeScore": 2,
    "predictedAwayScore": 1,
    "explanation": "Arsenal has stronger recent form..."
  }
]
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
