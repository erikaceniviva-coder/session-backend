
const API_URL = "http://localhost:8080/api";

export type AuthUser = {
  username: string;
};

async function readResponse(response: Response) {
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(
      data.error ?? data.message ?? "Si è verificato un errore"
    );
  }

  return data;
}

export async function login(
  username: string,
  password: string
): Promise<AuthUser> {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, password }),
  });

  const data = await readResponse(response);
  return { username: data.username };
}

export async function getMe(): Promise<AuthUser> {
  const response = await fetch(`${API_URL}/auth/me`, {
    credentials: "include",
  });

  return readResponse(response);
}

export async function register(
  username: string,
  password: string
): Promise<void> {
  const response = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, password }),
  });

  await readResponse(response);
}

export async function logout(): Promise<void> {
  const response = await fetch(`${API_URL}/auth/logout`, {
    method: "POST",
    credentials: "include",
  });

  await readResponse(response);
}
