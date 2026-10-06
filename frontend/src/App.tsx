import { Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { AppShell } from './components/layout/AppShell';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { StudentsPage } from './pages/StudentsPage';
import { StudentDetailPage } from './pages/StudentDetailPage';
import { ClassesPage } from './pages/ClassesPage';
import { ClassDetailPage } from './pages/ClassDetailPage';
import { LecturersPage } from './pages/LecturersPage';
import { CoursesPage } from './pages/CoursesPage';
import { SettingsPage } from './pages/SettingsPage';
function ProtectedRoute() { const { user, loading } = useAuth(); if (loading) return <div className="boot-loading"><span className="skeleton" />Loading Educare…</div>; return user ? <Outlet /> : <Navigate to="/login" replace />; }
function AdministratorRoute() { const { user } = useAuth(); return user?.role === 'ADMIN' ? <Outlet /> : <Navigate to="/dashboard" replace />; }
export default function App() { return <Routes><Route path="/login" element={<LoginPage />} /><Route element={<ProtectedRoute />}><Route element={<AppShell />}><Route index element={<Navigate to="/dashboard" replace />} /><Route path="/dashboard" element={<DashboardPage />} /><Route path="/students" element={<StudentsPage />} /><Route path="/students/:id" element={<StudentDetailPage />} /><Route path="/classes" element={<ClassesPage />} /><Route path="/classes/:id" element={<ClassDetailPage />} /><Route element={<AdministratorRoute />}><Route path="/lecturers" element={<LecturersPage />} /></Route><Route path="/courses" element={<CoursesPage />} /><Route path="/settings" element={<SettingsPage />} /><Route path="*" element={<Navigate to="/dashboard" replace />} /></Route></Route><Route path="*" element={<Navigate to="/login" replace />} /></Routes>; }
