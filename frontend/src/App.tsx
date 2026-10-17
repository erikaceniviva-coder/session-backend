
import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { getMe, login, logout, register } from "./api/auth";
import type { AuthUser } from "./api/auth";
import "./App.css";

type Page = "login" | "register";

function App() {
  const [page, setPage] = useState<Page>("login");
  const [user, setUser] = useState<AuthUser | null>(null);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getMe()
      .then(setUser)
      .catch(() => setUser(null));
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      if (page === "register") {
        await register(username, password);
        setMessage("Account creato! Ora puoi accedere.");
        setPage("login");
        setPassword("");
      } else {
        const authenticatedUser = await login(username, password);
        setUser(authenticatedUser);
        setPassword("");
      }
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Si è verificato un errore"
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleLogout() {
    setError("");
    setLoading(true);

    try {
      await logout();
      setUser(null);
      setUsername("");
      setPassword("");
      setMessage("Logout effettuato correttamente.");
      setPage("login");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Logout non riuscito");
    } finally {
      setLoading(false);
    }
  }

  if (user) {
    return (
      <main className="page">
        <section className="login-card dashboard">
          <div className="app-icon" aria-hidden="true">
            <svg viewBox="0 0 48 48" fill="none">
              <rect x="5" y="5" width="38" height="38" rx="12" fill="#087CFA" />
              <path
                d="M15 25l6 6 13-14"
                stroke="white"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          </div>
          <p className="eyebrow">SESSION AUTH</p>
          <h1>Benvenuto, {user.username}!</h1>
          <p className="subtitle">
            Hai effettuato l'accesso alla tua area personale.
          </p>

          <div className="profile-panel">
            <span className="profile-label">Utente autenticato</span>
            <strong>{user.username}</strong>
            <span className="status">
              <span className="status-dot" />
              Sessione attiva
            </span>
          </div>

          {error && <p className="error">{error}</p>}

          <button
            className="primary-button"
            onClick={handleLogout}
            disabled={loading}
          >
            {loading ? "Attendi…" : "Esci dall'account"}
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="page">
      <section className="login-card">
        <div className="app-icon" aria-hidden="true">
          <svg viewBox="0 0 48 48" fill="none">
            <rect x="5" y="5" width="38" height="38" rx="12" fill="#087CFA" />
            <path
              d="M16 22V17a8 8 0 0 1 16 0v5"
              stroke="white"
              strokeWidth="3"
              strokeLinecap="round"
            />
            <rect x="13" y="21" width="22" height="16" rx="4" fill="white" />
            <circle cx="24" cy="28" r="2" fill="#087CFA" />
          </svg>
        </div>

        <p className="eyebrow">SESSION AUTH</p>
        <h1>{page === "login" ? "Bentornato" : "Crea il tuo account"}</h1>
        <p className="subtitle">
          {page === "login"
            ? "Accedi alla tua area personale."
            : "Registrati per iniziare."}
        </p>

        <form onSubmit={handleSubmit}>
          <label htmlFor="username">Nome utente</label>
          <input
            id="username"
            autoComplete="username"
            minLength={3}
            maxLength={50}
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            placeholder="Il tuo username"
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            autoComplete={page === "login" ? "current-password" : "new-password"}
            minLength={12}
            maxLength={72}
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="Almeno 12 caratteri"
            required
          />

          {error && <p className="error" role="alert">{error}</p>}
          {message && <p className="success" role="status">{message}</p>}

          <button className="primary-button" disabled={loading}>
            {loading
              ? "Attendi…"
              : page === "login"
                ? "Accedi"
                : "Crea account"}
          </button>
        </form>

        <button
          type="button"
          className="text-button"
          onClick={() => {
            setPage(page === "login" ? "register" : "login");
            setError("");
            setMessage("");
          }}
        >
          {page === "login"
            ? "Non hai un account? Registrati"
            : "Hai già un account? Accedi"}
        </button>

        <p className="hint">Autenticazione protetta tramite sessione HTTP</p>
      </section>
    </main>
  );
}

export default App;
