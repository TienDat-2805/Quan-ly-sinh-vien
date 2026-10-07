export type UserRole = 'ADMIN' | 'LECTURER' | 'STUDENT';
export type Status = 'ACTIVE' | 'PENDING' | 'INACTIVE';
export interface User { username: string; displayName: string; role: UserRole }
export interface Grade { id: number; studentId: number; studentName: string; courseId: number; courseName: string; courseCode: string; credits: number; lecturer: string | null; semester: string; score: number; letter: string; points: number }
export interface Student { id: number; code: string; name: string; gender: string; dob: string; email: string; phone: string; address: string; status: Status; classId: number; classCode: string; className: string; faculty: string; enrollmentYear: number; gpa: number | null; creditsEarned: number; courses: Grade[] }
export type StudentInput = Pick<Student, 'code' | 'name' | 'gender' | 'dob' | 'email' | 'phone' | 'address' | 'status' | 'classId'>;
export interface AcademicClass { id: number; classInfoId: string; code: string; name: string; faculty: string; advisor: string; cohort: number; capacity: number; studentCount: number; averageGpa: number | null; lecturerId: number | null; courseId: number | null; credits: number | null }
export type ClassInput = Pick<AcademicClass, 'code' | 'name' | 'faculty' | 'advisor' | 'cohort' | 'capacity' | 'lecturerId' | 'courseId'>;
export interface BackendClassInfo { id: string; appId?: string; name: string; softwareType?: string; domain?: string; targetOperator?: string; review?: number; reviewCount: number; installationCount: number; iconPath?: string; templateDetailId?: string; createdTime?: string }
export interface ClassViewModel { id: string; academicId?: number; legacyId: string; name: string; domain: string; manager: string; studentCount: number | null; code: string; cohort: number | null; metadata?: AcademicClass }
export interface Lecturer { id: number; name: string; email: string; phone: string; faculty: string; specialty: string; status: Status; classCount: number }
export type LecturerInput = Omit<Lecturer, 'id' | 'classCount'>;
export interface Course { id: number; code: string; name: string; credits: number; faculty: string; lecturerId: number | null; lecturer: string | null }
export type CourseInput = Omit<Course, 'id' | 'lecturer'>;
export interface Activity { id: number; type: string; message: string; createdAt: string }
export interface Dashboard { students: number; classes: number; lecturers: number; courses: number; faculties: { name: string; count: number }[]; recentStudents: Student[]; recentClasses: AcademicClass[]; activities: Activity[] }
export interface Page<T> { content: T[]; totalElements: number; totalPages: number; page: number; size: number }
export interface ClassDetail { info: AcademicClass; students: Student[] }
export interface GradeInput { studentId: number; subjectId: number; semester: string; score: number }
export interface StudentDocument {
  id: string;
  studentId: number;
  name: string;
  owner: string;
  version: number;
  creationTime: string;
  modificationTime: string;
  active: boolean;
  contentType: string;
  sizeBytes: number;
}
