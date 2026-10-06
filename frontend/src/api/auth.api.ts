import { jsonBody, refreshCsrf, request } from './client';
import type { User } from '../types';
export const authApi = {
  me: () => request<User>('/api/auth/me'),
  login: async (email: string, password: string, remember: boolean) => {
    const body = new URLSearchParams({ username: email.trim().toLowerCase(), password });
    if (remember) body.set('remember-me', 'true');
    const user = await request<User>('/api/auth/login', { method: 'POST', body });
    await refreshCsrf(); return user;
  },
  logout: async () => { await request<void>('/api/auth/logout', { method: 'POST' }); await refreshCsrf(); },
  changePassword: (currentPassword: string, newPassword: string) => request<void>('/api/auth/password', { method: 'PUT', body: jsonBody({ currentPassword, newPassword }) }),
};
