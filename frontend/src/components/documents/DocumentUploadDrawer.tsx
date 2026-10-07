import { documentsApi } from '../../api/documents.api';
import { DocumentFileDrawer } from './DocumentFileDrawer';
export function DocumentUploadDrawer({ studentId, onClose, onSaved }: { studentId: number; onClose: () => void; onSaved: () => void }) {
  return <DocumentFileDrawer title="Upload documents" subtitle="Attach one or more files to this student." multiple saveLabel="Upload files" onSave={files => documentsApi.upload(studentId, files)} onClose={onClose} onSaved={onSaved} />;
}
