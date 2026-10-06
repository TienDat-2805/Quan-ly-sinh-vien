import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { authApi } from '../api/auth.api';
import type { User } from '../types';
interface AuthState { user: User | null; loading: boolean; signIn: (email: string, password: string, remember: boolean) => Promise<void>; signOut: () => Promise<void>; canEdit: boolean; canDelete: boolean }
const AuthContext = createContext<AuthState | null>(null);
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => { authApi.me().then(setUser).catch(() => setUser(null)).finally(() => setLoading(false)); }, []);
  const signIn = async (email: string, password: string, remember: boolean) => setUser(await authApi.login(email, password, remember));
  const signOut = async () => { await authApi.logout(); setUser(null); };
  return <AuthContext.Provider value={{ user, loading, signIn, signOut, canEdit: user?.role === 'ADMIN' || user?.role === 'LECTURER', canDelete: user?.role === 'ADMIN' }}>{children}</AuthContext.Provider>;
}
export function useAuth() { const context = useContext(AuthContext); if (!context) throw new Error('Missing AuthProvider'); return context; }
