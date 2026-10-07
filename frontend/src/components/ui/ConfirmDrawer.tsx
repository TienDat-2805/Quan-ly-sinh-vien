import { useState } from 'react';
import { Button, Drawer } from './index';
export function ConfirmDrawer({ name, onDelete, onClose, onSaved, title = 'Delete record', message = 'This will permanently remove the record and its associated student grades, if any.', confirmLabel = 'Delete record' }: { name: string; onDelete: () => Promise<void>; onClose: () => void; onSaved: () => void; title?: string; message?: string; confirmLabel?: string }) {
  const [busy, setBusy] = useState(false); const [error, setError] = useState('');
  const remove = async () => { setBusy(true); try { await onDelete(); onSaved(); onClose(); } catch (e) { setError(e instanceof Error ? e.message : 'Unable to delete record.'); } finally { setBusy(false); } };
  return <Drawer title={title} subtitle="Review the record before deleting." onClose={onClose}><div className="drawer-body"><h3>{name}</h3><p className="muted">{message}</p>{error && <p role="alert" className="form-error">{error}</p>}</div><div className="drawer-footer"><Button variant="secondary" disabled={busy} onClick={onClose}>Cancel</Button><Button variant="danger" disabled={busy} onClick={remove}>{busy ? 'Deleting…' : confirmLabel}</Button></div></Drawer>;
}
