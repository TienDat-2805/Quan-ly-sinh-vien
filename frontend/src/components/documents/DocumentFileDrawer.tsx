import { useRef, useState, type FormEvent } from 'react';
import { FileText, Upload, X } from 'lucide-react';
import { Button, Drawer } from '../ui';

export const ALLOWED_DOCUMENT_EXTENSIONS = ['PDF', 'XLSX', 'XLS', 'DOC', 'DOCX', 'JPG', 'JPEG'];
const ACCEPT = ALLOWED_DOCUMENT_EXTENSIONS.map(extension => `.${extension.toLowerCase()}`).join(',');
export function formatFileSize(bytes: number) { return bytes < 1024 * 1024 ? `${(bytes / 1024).toFixed(1)} KB` : `${(bytes / (1024 * 1024)).toFixed(1)} MB`; }

export function DocumentFileDrawer({ title, subtitle, multiple, hint, saveLabel, onSave, onSaved, onClose }: {
  title: string; subtitle: string; multiple: boolean; hint?: string; saveLabel: string;
  onSave: (files: File[]) => Promise<unknown>; onSaved: () => void; onClose: () => void;
}) {
  const input = useRef<HTMLInputElement>(null);
  const [files, setFiles] = useState<File[]>([]);
  const [error, setError] = useState(''); const [busy, setBusy] = useState(false); const [dragging, setDragging] = useState(false);
  const choose = (incoming: File[]) => {
    if (busy || !incoming.length) return;
    const next = multiple ? [...files, ...incoming] : incoming;
    const unique = next.filter((file, index) => next.findIndex(other => other.name === file.name && other.size === file.size && other.lastModified === file.lastModified) === index);
    if ((!multiple && incoming.length !== 1) || unique.length > 20) { setError(multiple ? 'Choose up to 20 files.' : 'Choose exactly one file to replace this document.'); return; }
    for (const file of unique) {
      if (!ALLOWED_DOCUMENT_EXTENSIONS.includes(file.name.split('.').pop()?.toUpperCase() || '')) { setError(`Unsupported file type. Allowed: ${ALLOWED_DOCUMENT_EXTENSIONS.join(', ')}.`); return; }
      if (file.size === 0) { setError(`The file "${file.name}" is empty.`); return; }
      if (file.size > 20 * 1024 * 1024) { setError('Each file must be 20 MB or smaller.'); return; }
    }
    if (unique.reduce((total, file) => total + file.size, 0) > 50 * 1024 * 1024) { setError('The total upload must be 50 MB or smaller.'); return; }
    setError(''); setFiles(unique);
  };
  const submit = async (event: FormEvent) => {
    event.preventDefault(); if (!files.length || busy) return;
    setBusy(true); setError('');
    try { await onSave(files); onSaved(); onClose(); }
    catch (reason) { setError(reason instanceof Error ? reason.message : 'Unable to save document.'); }
    finally { setBusy(false); }
  };
  return <Drawer title={title} subtitle={subtitle} onClose={() => { if (!busy) onClose(); }}>
    <form className="drawer-form" onSubmit={submit}>
      <div className="drawer-body">
        {hint && <div className="document-version-hint">{hint}</div>}
        {error && <p className="form-error" role="alert">{error}</p>}
        <div className={`document-drop-zone ${dragging ? 'dragging' : ''}`} onDragOver={event => { event.preventDefault(); setDragging(true); }} onDragLeave={() => setDragging(false)} onDrop={event => { event.preventDefault(); setDragging(false); choose(Array.from(event.dataTransfer.files)); }}>
          <Upload size={28} />
          <h3>Drop files here or <button type="button" className="text-link" disabled={busy} onClick={() => input.current?.click()}>browse</button></h3>
          <p>PDF, XLSX, XLS, DOC, DOCX, JPG, JPEG</p>
          <small>20 MB per file · 50 MB total{multiple ? ' · Up to 20 files' : ''}</small>
          <input ref={input} className="file-input" type="file" aria-label={multiple ? 'Select documents' : 'Select replacement file'} accept={ACCEPT} multiple={multiple} disabled={busy} onChange={event => { choose(Array.from(event.target.files || [])); event.target.value = ''; }} />
        </div>
        <div className="selected-documents">{files.map((file, index) => <div key={`${file.name}-${file.size}-${file.lastModified}`}><span className="class-icon"><FileText size={18} /></span><span><strong>{file.name}</strong><small>{formatFileSize(file.size)}</small></span><button type="button" className="icon-button" aria-label={`Remove ${file.name}`} disabled={busy} onClick={() => setFiles(previous => previous.filter((_, position) => position !== index))}><X size={16} /></button></div>)}</div>
        <p className="document-storage-hint">Files are attached to this student's record.<br />Your current files stay available until a replacement is saved.</p>
      </div>
      <div className="drawer-footer"><Button type="button" variant="secondary" disabled={busy} onClick={onClose}>Cancel</Button><Button disabled={busy || !files.length}>{busy ? 'Saving…' : saveLabel}</Button></div>
    </form>
  </Drawer>;
}
