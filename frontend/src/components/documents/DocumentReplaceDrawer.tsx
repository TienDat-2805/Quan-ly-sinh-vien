import { documentsApi } from '../../api/documents.api';
import type { StudentDocument } from '../../types';
import { DocumentFileDrawer } from './DocumentFileDrawer';
export function DocumentReplaceDrawer({ document, onClose, onSaved }: { document: StudentDocument; onClose: () => void; onSaved: () => void }) {
  return <DocumentFileDrawer title="Replace document" subtitle="Replace the stored file with a new version." multiple={false} saveLabel="Save new version" hint={`${document.name} · Current version: ${document.version.toFixed(1)}. Saving creates version ${(document.version + 1).toFixed(1)} and updates modification time.`} onSave={files => documentsApi.replace(document.id, files[0])} onClose={onClose} onSaved={onSaved} />;
}
