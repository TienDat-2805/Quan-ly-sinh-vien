import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Plus, RotateCcw } from 'lucide-react';
import { studentsApi } from '../api/management.api';
import { classesApi } from '../api/class-info.api';
import { useAuth } from '../context/AuthContext';
import { useDebounced, useResource } from '../hooks/useResource';
import { Button, Card, PageHeader, Pagination, SearchInput, Select } from '../components/ui';
import { StudentTable } from '../components/students/StudentTable';
import { StudentFormDrawer } from '../components/students/StudentFormDrawer';
import { ConfirmDrawer } from '../components/ui/ConfirmDrawer';
import type { Student } from '../types';
export function StudentsPage() {
  const navigate = useNavigate(); const { canEdit } = useAuth(); const [params] = useSearchParams();
  const [search, setSearch] = useState(params.get('q') || ''); const searchTerm = useDebounced(search);
  const [faculty, setFaculty] = useState(''); const [classId, setClassId] = useState(''); const [status, setStatus] = useState(''); const [page, setPage] = useState(0); const [sort, setSort] = useState('createdAt'); const [direction, setDirection] = useState('DESC');
  const [drawer, setDrawer] = useState<Student | 'new' | null>(null); const [deleting, setDeleting] = useState<Student | null>(null);
  const globalQuery = params.get('q') || '';
  useEffect(() => { setSearch(globalQuery); setPage(0); }, [globalQuery]);
  const classes = useResource(classesApi.metadata);
  const query = { search: searchTerm, faculty, classId, status, page, size: 8, sort, direction };
  const records = useResource(() => studentsApi.list(query), JSON.stringify(query));
  const faculties = [...new Set(classes.data?.map(c => c.faculty))];
  const changeSort = (key: string) => { setDirection(sort === key && direction === 'ASC' ? 'DESC' : 'ASC'); setSort(key); setPage(0); };
  const reset = () => { setSearch(''); setFaculty(''); setClassId(''); setStatus(''); setPage(0); };
  return <><PageHeader title="Students" description="Manage student information and academic status." action={canEdit && <Button onClick={() => setDrawer('new')}><Plus size={18} />Add student</Button>} /><Card className="table-card"><div className="filters"><SearchInput placeholder="Search students..." value={search} onChange={e => { setSearch(e.target.value); setPage(0); }} /><Select aria-label="Filter by faculty" value={faculty} onChange={e => { setFaculty(e.target.value); setClassId(''); setPage(0); }}><option value="">Faculty: All</option>{faculties.map(f => <option key={f}>{f}</option>)}</Select><Select aria-label="Filter by class" value={classId} onChange={e => { setClassId(e.target.value); setPage(0); }}><option value="">Class: All</option>{classes.data?.filter(c => !faculty || c.faculty === faculty).map(c => <option key={c.id} value={c.id}>{c.code}</option>)}</Select><Select aria-label="Filter by status" value={status} onChange={e => { setStatus(e.target.value); setPage(0); }}><option value="">Status: All</option><option value="ACTIVE">Active</option><option value="PENDING">Pending</option><option value="INACTIVE">Inactive</option></Select>{(search || faculty || classId || status) && <button className="icon-button" aria-label="Reset filters" onClick={reset}><RotateCcw size={16} /></button>}</div><StudentTable students={records.data?.content || []} loading={records.loading} error={records.error} retry={records.reload} sort={sort} onSort={changeSort} onOpen={s => navigate(`/students/${s.id}`)} onEdit={setDrawer} onDelete={setDeleting} /><Pagination page={page} total={records.data?.totalElements || 0} entity="students" onPage={setPage} /></Card>{drawer && <StudentFormDrawer student={drawer === 'new' ? undefined : drawer} onClose={() => setDrawer(null)} onSaved={records.reload} />}{deleting && <ConfirmDrawer name={deleting.name} onDelete={() => studentsApi.delete(deleting.id)} onClose={() => setDeleting(null)} onSaved={records.reload} />}</>;
}
