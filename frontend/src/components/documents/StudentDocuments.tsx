import { useState } from 'react';
import { Plus } from 'lucide-react';
import { documentsApi } from '../../api/documents.api';
import { useResource } from '../../hooks/useResource';
import { useAuth } from '../../context/AuthContext';
import type { StudentDocument } from '../../types';
import { Button, Card, Pagination } from '../ui';
import { ConfirmDrawer } from '../ui/ConfirmDrawer';
import { DocumentTable } from './DocumentTable';
import { DocumentUploadDrawer } from './DocumentUploadDrawer';
import { DocumentReplaceDrawer } from './DocumentReplaceDrawer';
export function StudentDocuments({ studentId }: { studentId: number }) {
  const { canEdit } = useAuth(); const resource = useResource(() => documentsApi.list(studentId), String(studentId));
  const [uploading, setUploading] = useState(false); const [replacing, setReplacing] = useState<StudentDocument | null>(null); const [deleting, setDeleting] = useState<StudentDocument | null>(null); const [downloading, setDownloading] = useState<string | null>(null); const [error, setError] = useState(''); const [page, setPage] = useState(0);
  const saved = () => { setPage(0); setError(''); resource.reload(); };
  const download = async (item: StudentDocument) => { setDownloading(item.id); setError(''); try { await documentsApi.download(item); } catch (reason) { setError(reason instanceof Error ? reason.message : 'Unable to download document.'); } finally { setDownloading(null); } };
  return <section id="student-documents" className="documents-section"><Card className="table-card"><div className="section-header padded"><div><h2>Documents</h2><p className="muted">Files attached to this student's record.</p></div>{canEdit && <Button variant="secondary" onClick={() => setUploading(true)}><Plus size={16} />Upload files</Button>}</div>{error && <p className="form-error document-action-error" role="alert">{error}</p>}<DocumentTable rows={(resource.data || []).slice(page * 8, (page + 1) * 8)} loading={resource.loading} error={resource.error} retry={resource.reload} downloading={downloading} onDownload={download} onReplace={setReplacing} onDelete={setDeleting} /><Pagination page={page} total={resource.data?.length || 0} entity="documents" onPage={setPage} /><p className="documents-footer">Allowed: PDF, XLSX, XLS, DOC, DOCX, JPG, JPEG</p></Card>{uploading && <DocumentUploadDrawer studentId={studentId} onSaved={saved} onClose={() => setUploading(false)} />}{replacing && <DocumentReplaceDrawer document={replacing} onSaved={saved} onClose={() => setReplacing(null)} />}{deleting && <ConfirmDrawer title="Delete document" name={`Delete "${deleting.name}"?`} message="The document will be hidden from the active student record but retained as a soft-deleted database record." confirmLabel="Delete document" onDelete={() => documentsApi.delete(deleting.id)} onSaved={saved} onClose={() => setDeleting(null)} />}</section>;
}
