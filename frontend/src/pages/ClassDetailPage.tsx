import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, BookOpen, Pencil } from 'lucide-react';
import { classesApi, classInfoApi } from '../api/class-info.api';
import { useAuth } from '../context/AuthContext';
import { useResource } from '../hooks/useResource';
import { Button, Card, ErrorState, Pagination, StatCard } from '../components/ui';
import { StudentTable } from '../components/students/StudentTable';
import { ClassFormDrawer } from '../components/classes/ClassFormDrawer';
import type { BackendClassInfo, ClassDetail } from '../types';
export function ClassDetailPage() {
  const { id = '' } = useParams(); const navigate = useNavigate(); const { canEdit } = useAuth(); const [editing, setEditing] = useState(false); const [page, setPage] = useState(0);
  const resource = useResource<{ detail?: ClassDetail; legacy?: BackendClassInfo }>(() => id.startsWith('legacy-') ? classInfoApi.getById(id.slice(7)).then(legacy => ({ legacy })) : classesApi.detail(id).then(detail => ({ detail })), id);
  if (resource.error) return <ErrorState message={resource.error} retry={resource.reload} />;
  const c = resource.data?.detail?.info; const legacy = resource.data?.legacy; const roster = resource.data?.detail?.students || [];
  return <><Link className="back-link" to="/classes"><ArrowLeft size={17} />Back to classes</Link>{resource.loading ? <Card><div className="skeleton detail-skeleton" /></Card> : <><Card className="class-hero"><span className="class-icon hero-icon"><BookOpen size={28} /></span><div><h1>{c?.name || legacy?.name}</h1><p>{c?.code || legacy?.appId || '—'}<span className="separator">·</span>{c?.faculty || legacy?.domain}<span className="separator">·</span>{c?.credits ? `${c.credits} credits` : 'No course assigned'}</p><p className="muted">Lecturer: {c?.advisor || legacy?.targetOperator || 'Unassigned'}</p></div>{canEdit && c && <Button variant="secondary" onClick={() => setEditing(true)}><Pencil size={16} />Edit class</Button>}</Card><div className="stats-grid class-metrics"><StatCard label="Students" value={roster.length} caption={c ? `${c.capacity} maximum capacity` : 'Roster metadata unavailable'} icon={<BookOpen size={18} />} /><StatCard label="Average GPA" value={c?.averageGpa?.toFixed(2) || '—'} caption="Weighted average / 4.00" icon={<BookOpen size={18} />} /><StatCard label="Attendance" value="—" caption="Tracking not available in v1" icon={<BookOpen size={18} />} /><StatCard label="Assignments" value="—" caption="Tracking not available in v1" icon={<BookOpen size={18} />} /></div><Card className="table-card"><div className="section-header padded"><h2>Student roster</h2><span className="muted">{roster.length} students</span></div><StudentTable roster students={roster.slice(page * 8, (page + 1) * 8)} onOpen={s => navigate(`/students/${s.id}`)} /><Pagination page={page} total={roster.length} entity="students" onPage={setPage} /></Card></>}{editing && c && <ClassFormDrawer record={c} onClose={() => setEditing(false)} onSaved={resource.reload} />}</>;
}
