import { Pencil, Trash2 } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import type { Student } from '../../types';
import { Avatar, DataTable, StatusBadge, type Column } from '../ui';
export function StudentTable({ students, loading, error, retry, onOpen, onEdit, onDelete, roster = false, sort, onSort }: { students: Student[]; loading?: boolean; error?: string; retry?: () => void; onOpen: (student: Student) => void; onEdit?: (student: Student) => void; onDelete?: (student: Student) => void; roster?: boolean; sort?: string; onSort?: (sort: string) => void }) {
  const { canEdit, canDelete } = useAuth();
  const columns: Column<Student>[] = [
    { key: 'name', title: 'Student', sortable: !!onSort, render: s => <div className="person-cell"><Avatar name={s.name} /><span><strong>{s.name}</strong><small>{s.email}</small></span></div> },
    { key: 'code', title: 'Student ID', sortable: !!onSort, render: s => <span className="code-text">{s.code}</span> },
    ...(!roster ? [{ key: 'class', title: 'Class', render: (s: Student) => s.classCode }, { key: 'faculty', title: 'Faculty', render: (s: Student) => <span className="faculty-text">{s.faculty}</span> }] : []),
    { key: 'status', title: 'Status', render: s => <StatusBadge status={s.status} /> },
    ...(roster ? [{ key: 'attendance', title: 'Attendance', render: () => <span className="muted" title="Attendance tracking is not available in this version.">—</span> }] : []),
    { key: 'gpa', title: 'GPA', render: s => <strong className="gpa">{s.gpa?.toFixed(2) ?? '—'}</strong> },
    ...(!roster && canEdit ? [{ key: 'actions', title: 'Actions', render: (s: Student) => <div className="row-actions" onClick={e => e.stopPropagation()}>{onEdit && <button className="icon-button" aria-label={`Edit ${s.name}`} onClick={() => onEdit(s)}><Pencil size={15} /></button>}{canDelete && onDelete && <button className="icon-button danger-hover" aria-label={`Delete ${s.name}`} onClick={() => onDelete(s)}><Trash2 size={15} /></button>}</div> }] : []),
  ];
  return <DataTable columns={columns} rows={students} rowKey={s => s.id} onRowClick={onOpen} loading={loading} error={error} retry={retry} sort={sort} onSort={onSort} />;
}
