const BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/test').replace(/\/$/, '');
let csrf: { token: string; headerName: string } | null = null;
export class ApiError extends Error {
  constructor(message: string, public status: number, public fields: Record<string, string> = {}) { super(message); }
}
export async function refreshCsrf() {
  const response = await fetch(`${BASE_URL}/api/auth/csrf`, { credentials: 'include' });
  if (!response.ok) throw new ApiError('Unable to connect to the server.', response.status);
  csrf = await response.json() as { token: string; headerName: string };
  return csrf;
}
export async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  const method = init.method || 'GET';
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    const token = csrf || await refreshCsrf();
    if (token) headers.set(token.headerName, token.token);
  }
  if (init.body && !(init.body instanceof URLSearchParams)) headers.set('Content-Type', 'application/json');
  const response = await fetch(`${BASE_URL}${path}`, { ...init, headers, credentials: 'include' });
  if (!response.ok) {
    const body: { message?: string; fields?: Record<string, string> } = await response.json().catch(() => ({}));
    throw new ApiError(body.message || 'Unable to complete this request.', response.status, body.fields);
  }
  if (response.status === 204) return undefined as T;
  return response.headers.get('content-type')?.includes('application/json') ? await response.json() as T : await response.text() as T;
}
export function queryString(params: Record<string, string | number | undefined>) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => { if (value !== undefined && value !== '') query.set(key, String(value)); });
  return query.toString();
}
export const jsonBody = (body: unknown) => JSON.stringify(body);
