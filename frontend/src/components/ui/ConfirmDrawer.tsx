import { useState } from 'react';
import { Button, Drawer } from './index';
export function ConfirmDrawer({ name, onDelete, onClose, onSaved }: { name: string; onDelete: () => Promise<void>; onClose: () => void; onSaved: () => void }) {
  const [busy, setBusy] = useState(false); const [error, setError] = useState('');
  const remove = async () => { setBusy(true); try { await onDelete(); onSaved(); onClose(); } catch (e) { setError(e instanceof Error ? e.message : 'Unable to delete record.'); } finally { setBusy(false); } };
  return <Drawer title="Delete record" subtitle="Review the record before deleting." onClose={onClose}><div className="drawer-body"><h3>{name}</h3><p className="muted">This will permanently remove the record and its associated student grades, if any.</p>{error && <p role="alert" className="form-error">{error}</p>}</div><div className="drawer-footer"><Button variant="secondary" disabled={busy} onClick={onClose}>Cancel</Button><Button variant="danger" disabled={busy} onClick={remove}>{busy ? 'Deleting…' : 'Delete record'}</Button></div></Drawer>;
}
