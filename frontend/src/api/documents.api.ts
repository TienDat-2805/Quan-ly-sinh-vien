import { request, requestBlob } from './client';
import type { StudentDocument } from '../types';

export const documentsApi = {
  list: (studentId: number) => request<StudentDocument[]>(`/api/students/${studentId}/documents`),
  upload: (studentId: number, files: File[]) => {
    const body = new FormData();
    files.forEach(file => body.append('files', file));
    return request<StudentDocument[]>(`/api/students/${studentId}/documents`, { method: 'POST', body });
  },
  replace: (id: string, file: File) => {
    const body = new FormData(); body.append('file', file);
    return request<StudentDocument>(`/api/documents/${encodeURIComponent(id)}`, { method: 'PUT', body });
  },
  download: async (document: StudentDocument) => {
    const blob = await requestBlob(`/api/documents/${encodeURIComponent(document.id)}/download`);
    const url = URL.createObjectURL(blob);
    const link = window.document.createElement('a');
    link.href = url; link.download = document.name;
    window.document.body.appendChild(link); link.click(); link.remove();
    // Allow the browser to consume the download before releasing the URL.
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  },
  delete: (id: string) => request<void>(`/api/documents/${encodeURIComponent(id)}`, { method: 'DELETE' }),
};
