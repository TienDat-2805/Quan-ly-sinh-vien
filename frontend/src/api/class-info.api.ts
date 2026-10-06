import { jsonBody, queryString, request } from './client';
import type { AcademicClass, BackendClassInfo, ClassDetail, ClassInput, ClassViewModel } from '../types';
export const classInfoApi = {
  getAll: () => request<BackendClassInfo[]>('/api/class-info/list'),
  getById: (id: string) => request<BackendClassInfo>(`/api/class-info/${encodeURIComponent(id)}`),
  getByName: (name: string) => request<BackendClassInfo>(`/api/class-info/name/${encodeURIComponent(name)}`),
  getByDomain: (domain: string) => request<BackendClassInfo[]>(`/api/class-info/domain/${encodeURIComponent(domain)}`),
  search: (params: Record<string, string | number | undefined>) => request<BackendClassInfo[]>(`/api/class-info/search?${queryString(params)}`),
  count: (query?: string) => request<number>(`/api/class-info/count?${queryString({ query })}`),
  create: (payload: Omit<BackendClassInfo, 'id'>) => request<BackendClassInfo>('/api/class-info/create', { method: 'POST', body: jsonBody(payload) }),
  update: (payload: BackendClassInfo) => request<BackendClassInfo>('/api/class-info/update', { method: 'PUT', body: jsonBody(payload) }),
  delete: (id: string) => request<string>(`/api/class-info/${encodeURIComponent(id)}`, { method: 'DELETE' }),
};
export const classesApi = {
  metadata: () => request<AcademicClass[]>('/api/classes'),
  getAll: async (): Promise<ClassViewModel[]> => {
    const [legacy, metadata] = await Promise.all([classInfoApi.getAll(), request<AcademicClass[]>('/api/classes')]);
    return legacy.map(info => {
      const meta = metadata.find(c => c.classInfoId === info.id);
      return { id: meta ? String(meta.id) : `legacy-${info.id}`, academicId: meta?.id, legacyId: info.id, name: info.name, domain: info.domain || 'Unassigned', manager: info.targetOperator || meta?.advisor || 'Unassigned', studentCount: meta?.studentCount ?? null, code: meta?.code || info.appId || '—', cohort: meta?.cohort ?? null, metadata: meta };
    });
  },
  detail: (id: string) => request<ClassDetail>(`/api/classes/${encodeURIComponent(id)}`),
  save: (id: number | undefined, input: ClassInput) => request<AcademicClass>(`/api/classes${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: jsonBody(input) }),
  delete: (id: number) => request<void>(`/api/classes/${id}`, { method: 'DELETE' }),
};
