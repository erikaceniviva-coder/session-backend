
# Session Backend

Backend REST per la gestione degli utenti e delle sessioni, sviluppato con Java e Spring Boot, con frontend React e database PostgreSQL.

## Tecnologie

- Java 21
- Spring Boot
- Spring Security
- Spring JDBC
- PostgreSQL
- Maven
- React, TypeScript e Vite

## Funzionalità

- Registrazione degli utenti
- Login e autenticazione
- Endpoint per verificare l'utente autenticato
- Logout
- Gestione delle sessioni tramite cookie
- Test unitari e di integrazione

## Requisiti

- JDK 21
- Maven oppure Maven Wrapper
- PostgreSQL
- Node.js e npm per il frontend

## Avvio del backend

Clona il repository:

```bash
git clone https://github.com/erikaceniviva-coder/session-backend.git
cd session-backend
```

Esegui i test:

```bash
./mvnw clean test
```

Avvia l'applicazione:

```bash
./mvnw spring-boot:run
```

La porta predefinita di Spring Boot è `8080`, salvo diversa configurazione.

## Configurazione del database

Configura la connessione PostgreSQL tramite le proprietà dell'applicazione o variabili d'ambiente.

Non inserire password, token o credenziali reali nel repository.

## API di autenticazione

| Metodo | Endpoint | Descrizione |
|---|---|---|
| POST | `/api/auth/register` | Registrazione |
| POST | `/api/auth/login` | Login |
| GET | `/api/auth/me` | Utente autenticato |
| POST | `/api/auth/logout` | Logout |

Le richieste di registrazione e login utilizzano JSON. Gli endpoint protetti richiedono una sessione autenticata.

## Frontend

Il frontend si trova nella cartella `frontend/`.

Per installare le dipendenze:

```bash
cd frontend
npm install
```

Per avviare il server di sviluppo:

```bash
npm run dev
```

## Test

Per eseguire tutti i test del backend:

```bash
./mvnw clean test
```

## Deployment

Il backend può essere distribuito su una piattaforma compatibile con Java e Spring Boot, collegando il repository GitHub e configurando le variabili d'ambiente e il database PostgreSQL.

## Licenza

Nessuna licenza specificata al momento.
