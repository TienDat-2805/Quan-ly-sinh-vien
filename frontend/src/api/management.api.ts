import { jsonBody, queryString, request } from './client';
import type { Course, CourseInput, Dashboard, Grade, GradeInput, Lecturer, LecturerInput, Page, Student, StudentInput } from '../types';
export const studentsApi = {
  list: (params: Record<string, string | number | undefined>) => request<Page<Student>>(`/api/students?${queryString(params)}`),
  detail: (id: string) => request<Student>(`/api/students/${encodeURIComponent(id)}`),
  save: (id: number | undefined, input: StudentInput) => request<Student>(`/api/students${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: jsonBody(input) }),
  delete: (id: number) => request<void>(`/api/students/${id}`, { method: 'DELETE' }),
};
export const coursesApi = {
  list: () => request<Course[]>('/api/courses'),
  save: (id: number | undefined, input: CourseInput) => request<Course>(`/api/courses${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: jsonBody(input) }),
  delete: (id: number) => request<void>(`/api/courses/${id}`, { method: 'DELETE' }),
};
export const lecturersApi = {
  list: () => request<Lecturer[]>('/api/lecturers'),
  save: (id: number | undefined, input: LecturerInput) => request<Lecturer>(`/api/lecturers${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: jsonBody(input) }),
  delete: (id: number) => request<void>(`/api/lecturers/${id}`, { method: 'DELETE' }),
};
export const gradesApi = {
  save: (id: number | undefined, input: GradeInput) => request<Grade>(`/api/grades${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: jsonBody(input) }),
  delete: (id: number) => request<void>(`/api/grades/${id}`, { method: 'DELETE' }),
};
export const dashboardApi = { get: () => request<Dashboard>('/api/dashboard') };
