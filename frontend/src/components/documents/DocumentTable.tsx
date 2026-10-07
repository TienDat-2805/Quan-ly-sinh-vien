import { Download, FileText, RefreshCw, Trash2 } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import type { StudentDocument } from '../../types';
import { DataTable, type Column } from '../ui';
import { formatFileSize } from './DocumentFileDrawer';
export function DocumentTable({ rows, loading, error, retry, downloading, onDownload, onReplace, onDelete }: {
  rows: StudentDocument[]; loading: boolean; error: string; retry: () => void; downloading: string | null;
  onDownload: (document: StudentDocument) => void; onReplace: (document: StudentDocument) => void; onDelete: (document: StudentDocument) => void;
}) {
  const { canEdit, canDelete } = useAuth();
  const columns: Column<StudentDocument>[] = [
    { key: 'file', title: 'File', render: item => <div className="person-cell document-file-cell"><span className="class-icon"><FileText size={19} /></span><span><strong>{item.name}</strong><small>{formatFileSize(item.sizeBytes)}</small></span></div> },
    { key: 'type', title: 'Type', render: item => <span className="credit-badge">{item.name.split('.').pop()?.toUpperCase()}</span> },
    { key: 'owner', title: 'Owner', render: item => <span className="document-owner">{item.owner}</span> },
    { key: 'version', title: 'Version', render: item => <span className="document-version">{item.version.toFixed(1)}</span> },
    { key: 'modified', title: 'Modified', render: item => new Date(item.modificationTime).toLocaleDateString('en-GB') },
    { key: 'actions', title: 'Actions', render: item => <div className="row-actions"><button className="icon-button" aria-label={`Download ${item.name}`} title="Download" disabled={downloading === item.id} onClick={() => onDownload(item)}><Download size={16} /></button>{canEdit && <button className="icon-button" aria-label={`Replace ${item.name}`} title="Replace" onClick={() => onReplace(item)}><RefreshCw size={16} /></button>}{canDelete && <button className="icon-button danger-hover" aria-label={`Delete document ${item.name}`} title="Delete" onClick={() => onDelete(item)}><Trash2 size={16} /></button>}</div> },
  ];
  return <DataTable columns={columns} rows={rows} rowKey={item => item.id} loading={loading} error={error} retry={retry} emptyTitle="No documents yet." emptyMessage="Upload files to attach documents to this student." />;
}
