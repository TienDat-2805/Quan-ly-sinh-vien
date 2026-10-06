# Frontend Design Specification — Quản lý sinh viên

> Project: `Quan-ly-sinh-vien`  
> Frontend direction: EduSync-inspired university administration dashboard  
> Figma source: https://www.figma.com/design/jbrd9jDpIYWHYybrVDwULk  
> Backend repository: https://github.com/TienDat-2805/Quan-ly-sinh-vien  
> Target: React frontend integrated with the existing Spring Boot backend  
> Primary viewport: Desktop 1440 × 1024

---

## 1. Mục tiêu

Xây dựng frontend cho hệ thống quản lý sinh viên theo phong cách dashboard quản trị giáo dục hiện đại, lấy cảm hứng từ EduSync:

- Đầy đủ chức năng nhưng bố cục phải gọn.
- Data table là trọng tâm đối với dữ liệu quản lý.
- Card chỉ dùng cho dashboard, KPI và các entity phù hợp.
- Sidebar cố định bên trái.
- Topbar gọn, có search + notification + account.
- Màu sắc tối giản, ưu tiên trắng / xám nhạt / indigo.
- Không tự ý redesign nếu thiết kế hoặc spec đã mô tả rõ.
- Tái sử dụng component tối đa.
- Frontend phải thích nghi với backend hiện tại, không sửa backend chỉ để làm UI thuận tiện hơn.

---

## 2. Nguyên tắc triển khai

### 2.1. Source of truth

Thứ tự ưu tiên khi có xung đột:

1. `frontend-design-spec.md`
2. Figma
3. Backend API hiện có
4. Mock data chỉ dùng cho phần backend chưa hỗ trợ

Không tự thêm module lớn ngoài phạm vi nếu không cần thiết.

### 2.2. Không thay đổi backend không cần thiết

Backend Spring Boot hiện tại phải được giữ nguyên.

Frontend nên:

- Tạo service/adapter để chuyển dữ liệu backend sang UI model.
- Dùng mock data cho các module chưa có API thật.
- Không thay đổi entity backend chỉ vì tên field chưa phù hợp.
- Không sửa endpoint nếu chưa được yêu cầu.

---

## 3. Design system

### 3.1. Màu sắc

```css
--color-bg-app: #F7F8FC;
--color-bg-surface: #FFFFFF;
--color-bg-surface-soft: #F1F3F9;

--color-primary: #5B5BD6;
--color-primary-soft: #EEECFF;

--color-text-primary: #1C1C28;
--color-text-secondary: #74788D;

--color-border: #E7E8EF;

--color-success: #2F9E6F;
--color-success-soft: #E9F8F1;

--color-warning: #B7791F;
--color-warning-soft: #FFF5DC;

--color-danger: #D94F5C;
--color-danger-soft: #FDEDEF;
```

### 3.2. Typography

Font chính:

```text
Inter
```

Các cấp chữ:

```text
Heading 1: 28px / 36px / Bold
Heading 2: 20px / 28px / Semi Bold
Heading 3: 16px / 24px / Semi Bold

Body: 14px / 20px / Regular
Body Medium: 14px / 20px / Medium

Small: 12px / 18px / Regular
Small Medium: 12px / 18px / Medium

Statistic: 26px / 32px / Bold
```

### 3.3. Radius

```text
Main card: 14px
Button: 10px
Input: 10px
Sidebar nav item: 10px
Avatar: 50% / circle
Status badge: pill / 999px
```

### 3.4. Shadow

Card shadow phải nhẹ.

Gợi ý:

```css
box-shadow: 0 6px 18px rgba(30, 35, 56, 0.06);
```

Không dùng shadow nặng.

---

## 4. Layout toàn hệ thống

Desktop:

```text
1440 × 1024
```

### Sidebar

```text
Width: 240px
Height: 100vh
Background: #FFFFFF
```

### Main content

```text
Width: calc(100% - 240px)
```

### Topbar

```text
Height: 80px
Background: #FFFFFF
```

### Content

```text
Horizontal padding: 32px
Top padding: 28px
Bottom padding: 28px
Section gap: ~24px
```

---

## 5. Sidebar

Brand:

```text
Educare
```

Menu:

```text
MANAGEMENT

Dashboard
Students
Classes
Lecturers
Courses

----------------

Settings
```

Bottom account:

```text
Nguyen Tien Dat
Administrator
```

### Sidebar behavior

Active item:

```text
Background: #EEECFF
Text/icon: #5B5BD6
```

Inactive item:

```text
Background: transparent
Text/icon: #74788D
```

Sidebar navigation routes:

```text
Dashboard  -> /dashboard
Students   -> /students
Classes    -> /classes
Lecturers  -> /lecturers
Courses    -> /courses
Settings   -> /settings (placeholder if not implemented)
```

---

## 6. Topbar

Bố cục:

```text
[ Global search........................ ]              [ Notification ] [ Admin ] [ Avatar ]
```

Search:

```text
Width ~300px
Height 42px
```

Không cần làm notification logic phức tạp ở version đầu.

---

## 7. Routes

```text
/login

/dashboard

/students
/students/:id

/classes
/classes/:id

/lecturers

/courses

/settings
```

Nếu dùng React Router:

```text
/login
/dashboard
/students
/students/:studentId
/classes
/classes/:classId
/lecturers
/courses
```

---

# 8. Screen — Login

Route:

```text
/login
```

Desktop layout chia 2 cột:

```text
720px | 720px
```

## Left panel

Background:

```text
#5B5BD6
```

Có một card trắng lớn ở giữa.

Nội dung:

```text
Educare

Student management made simple.

A clean workspace for students, classes,
lecturers and courses.
```

Mini statistics:

```text
1,248 Students
42 Classes
87 Lecturers
```

## Right panel

Form ở giữa.

Nội dung:

```text
Welcome back
Sign in to continue to Educare.
```

Fields:

```text
Email
Password
Remember me
```

Primary button:

```text
Sign in
```

Footer text:

```text
Use your university account to access the system.
```

### Login flow

```text
Sign in -> /dashboard
```

Nếu backend authentication chưa dùng được:

- Cho phép fake login ở development mode.
- Không block việc dựng các màn hình khác.
- Chuẩn bị cấu trúc auth service để thay API thật sau.

---

# 9. Screen — Dashboard

Route:

```text
/dashboard
```

Header:

```text
Dashboard
Overview of students, classes and academic activity.
```

## KPI cards

4 card ngang:

```text
Total students
1,248
+8.4% this semester

Classes
42
+3 new classes

Lecturers
87
+5 this semester

Courses
116
94% active
```

## Row 2

### Students by faculty

Chart dạng bar.

Các nhãn demo:

```text
IT
Business
Design
Finance
Law
Other
```

### Recent students

Danh sách 4 sinh viên gần đây:

```text
Nguyen Minh Anh
22110081 • IT

Tran Gia Bao
22110124 • Business

Le Thu Ha
22110312 • Design

Pham Duc Long
22110403 • IT
```

## Row 3

### Recent classes

```text
Web Development
IT • 42 students

Cloud Computing
IT • 36 students

Database Systems
IT • 40 students
```

### Recent activity

```text
Added student Nguyen Minh Anh
Updated class Web Development
Created course UI Design
```

---

# 10. Screen — Students

Route:

```text
/students
```

Header:

```text
Students
Manage student information and academic status.
```

Action:

```text
+ Add student
```

## Filters

```text
Search students...
Faculty: All
Class: All
Status: All
```

## Student table

Columns:

```text
Student
Student ID
Class
Faculty
Status
GPA
Actions
```

Sample data:

| Student | ID | Class | Faculty | Status | GPA |
|---|---|---|---|---|---|
| Nguyen Minh Anh | 22110081 | 22DTH1 | Information Technology | Active | 3.42 |
| Tran Gia Bao | 22110124 | 22DTH2 | Business | Active | 3.18 |
| Le Thu Ha | 22110312 | 22TK1 | Design | Pending | 2.94 |
| Pham Duc Long | 22110403 | 22DTH1 | Information Technology | Active | 3.61 |
| Vo Ngoc Linh | 22110555 | 22DTH3 | Information Technology | Inactive | 2.71 |
| Do Hoang Nam | 22110610 | 22QTKD1 | Business | Active | 3.06 |
| Bui Khanh Vy | 22110721 | 22TK2 | Design | Active | 3.77 |
| Nguyen Quoc Huy | 22110833 | 22DTH2 | Information Technology | Pending | 2.88 |

Footer:

```text
Showing 1–8 of 1,248 students
```

Pagination:

```text
‹ 1 2 3 4 … 156 ›
```

### Student table behavior

Click một row:

```text
/students/:id
```

Ví dụ:

```text
/students/22110081
```

---

# 11. Add Student Drawer

Mở từ:

```text
/students
```

khi click:

```text
+ Add student
```

Drawer nằm bên phải.

```text
Width: 460px
Height: 100vh
```

Background phía sau có dim overlay.

Fields:

```text
Student ID
Full name
Email
Faculty
Class
Status
```

Buttons:

```text
Cancel
Create student
```

Drawer title:

```text
Add student
Create a new student record.
```

### Không chuyển route bắt buộc

Có thể:

- dùng local drawer state, hoặc
- dùng query param `/students?action=add`.

Ưu tiên local state đơn giản.

---

# 12. Screen — Student Detail

Route:

```text
/students/:id
```

Top action:

```text
← Back to students
```

## Profile card

```text
Nguyen Minh Anh

Student ID: 22110081
22DTH1
Information Technology

22110081@student.edu.vn

Active
```

Button:

```text
Edit profile
```

## Personal information

```text
Full name: Nguyen Minh Anh
Date of birth: 15/08/2004
Phone: 0901 234 567
Address: Ninh Kieu, Can Tho
Enrollment year: 2022
```

## Academic information

```text
Faculty: Information Technology
Class: 22DTH1
Current GPA: 3.42 / 4.00
Credits earned: 96 / 132
Academic status: Good
```

## Current courses

Columns:

```text
Course
Code
Credits
Lecturer
Grade
```

Rows:

```text
Web Development | CSE3032 | 3 | Nguyen Van A | A-
Cloud Computing | CSE3051 | 3 | Tran Van B | B+
Computer Networks | CSE3024 | 3 | Le Van C | B
Database Systems | CSE3018 | 3 | Pham Thi D | A
```

---

# 13. Edit Student Drawer

Mở khi click:

```text
Edit profile
```

Drawer:

```text
Width: 460px
```

Fields prefilled:

```text
Student ID: 22110081
Full name: Nguyen Minh Anh
Email: 22110081@student.edu.vn
Faculty: Information Technology
Class: 22DTH1
Status: Active
```

Buttons:

```text
Cancel
Save changes
```

---

# 14. Screen — Classes

Route:

```text
/classes
```

Header:

```text
Classes
Manage class information and student rosters.
```

Button:

```text
+ Add class
```

Filters:

```text
Search classes...
Domain: All
Semester: 2026
Sort: Newest
```

Classes dùng CARD GRID, không dùng table.

Card size khoảng:

```text
360 × 196
```

Sample cards:

```text
Web Development
IT
42 students
Nguyen Van A

Cloud Computing
IT
36 students
Tran Van B

Computer Networks
IT
40 students
Le Van C

Database Systems
IT
38 students
Pham Thi D

UI/UX Fundamentals
Design
30 students
Le Thu Trang

Business Analytics
Business
44 students
Vo Minh Duc
```

Click card:

```text
/classes/:id
```

---

# 15. Screen — Class Detail

Route:

```text
/classes/:id
```

Top:

```text
← Back to classes
```

Hero:

```text
Web Development

CSE3032
Information Technology
3 credits

Lecturer: Nguyen Van A
Updated: 04 Oct 2026
```

Button:

```text
Edit class
```

## Metrics

```text
Students: 42
Average GPA: 3.18
Attendance: 91%
Assignments: 8
```

## Student roster

Columns:

```text
Student
Student ID
Status
Attendance
GPA
```

Sample rows:

```text
Nguyen Minh Anh | 22110081 | Active | 96% | 3.42
Tran Gia Bao    | 22110124 | Active | 92% | 3.18
Pham Duc Long   | 22110403 | Active | 88% | 3.61
Vo Ngoc Linh    | 22110555 | Inactive | 74% | 2.71
Nguyen Quoc Huy | 22110833 | Pending | 83% | 2.88
```

---

# 16. Screen — Lecturers

Route:

```text
/lecturers
```

Header:

```text
Lecturers
Manage lecturer profiles, departments and teaching load.
```

Button:

```text
+ Add lecturer
```

Filters:

```text
Search lecturers...
Faculty: All
Status: All
Sort: Name
```

Dùng card grid.

Sample:

```text
Nguyen Van A
Information Technology
Web Development
6 classes
Active

Tran Van B
Information Technology
Cloud Computing
4 classes
Active

Le Van C
Information Technology
Computer Networks
5 classes
Active

Pham Thi D
Information Technology
Database Systems
4 classes
Active

Le Thu Trang
Design
UI/UX Fundamentals
3 classes
Active

Vo Minh Duc
Business
Business Analytics
5 classes
Active
```

Card footer:

```text
View profile →
```

Lecturer Detail chưa bắt buộc ở v1.

---

# 17. Screen — Courses

Route:

```text
/courses
```

Header:

```text
Courses
Manage course catalog, credits and assigned lecturers.
```

Button:

```text
+ Add course
```

Filters:

```text
Search courses...
Faculty: All
Credits: All
Sort: Code
```

## Course table

Columns:

```text
Course
Code
Credits
Faculty
Lecturer
Actions
```

Sample:

```text
Web Development | CSE3032 | 3 | IT | Nguyen Van A
Cloud Computing | CSE3051 | 3 | IT | Tran Van B
Computer Networks | CSE3024 | 3 | IT | Le Van C
Database Systems | CSE3018 | 3 | IT | Pham Thi D
UI/UX Fundamentals | DES2011 | 2 | Design | Le Thu Trang
Business Analytics | BUS3040 | 3 | Business | Vo Minh Duc
Information Security | CSE3044 | 3 | IT | Nguyen Van A
Software Engineering | CSE3010 | 3 | IT | Tran Van B
```

Footer:

```text
Showing 1–8 of 116 courses
```

---

# 18. Component inventory

Phải tạo reusable components.

Recommended:

```text
src/components/
├── layout/
│   ├── AppShell
│   ├── Sidebar
│   └── Topbar
│
├── ui/
│   ├── Button
│   ├── Input
│   ├── SearchInput
│   ├── Select
│   ├── StatusBadge
│   ├── Card
│   ├── StatCard
│   ├── Avatar
│   ├── Drawer
│   ├── Table
│   └── Pagination
│
├── students/
│   ├── StudentTable
│   ├── StudentFormDrawer
│   └── StudentProfileCard
│
├── classes/
│   ├── ClassCard
│   └── ClassRoster
│
├── lecturers/
│   └── LecturerCard
│
└── courses/
    └── CourseTable
```

---

# 19. Suggested frontend structure

Nếu project chưa có frontend riêng:

```text
frontend/
├── src/
│   ├── api/
│   │   ├── client.ts
│   │   ├── auth.api.ts
│   │   ├── class-info.api.ts
│   │   ├── students.api.ts
│   │   ├── lecturers.api.ts
│   │   └── courses.api.ts
│   │
│   ├── components/
│   ├── layouts/
│   ├── pages/
│   │   ├── LoginPage
│   │   ├── DashboardPage
│   │   ├── StudentsPage
│   │   ├── StudentDetailPage
│   │   ├── ClassesPage
│   │   ├── ClassDetailPage
│   │   ├── LecturersPage
│   │   └── CoursesPage
│   │
│   ├── types/
│   ├── mocks/
│   ├── utils/
│   ├── router/
│   ├── App.tsx
│   └── main.tsx
```

---

# 20. Backend hiện tại

Backend hiện tại chủ yếu có `ClassInfo`.

Base path:

```text
/api/class-info
```

Endpoints:

```text
POST    /api/class-info/create
PUT     /api/class-info/update
DELETE  /api/class-info/{id}

GET     /api/class-info/{id}
GET     /api/class-info/name/{name}
GET     /api/class-info/list
GET     /api/class-info/domain/{domain}

GET     /api/class-info/search
GET     /api/class-info/count
```

Search params:

```text
query
lowerLimit
upperLimit
orderBy
orderType
```

Ví dụ:

```text
/api/class-info/search
?query=web
&lowerLimit=0
&upperLimit=10
&orderBy=name
&orderType=ASC
```

---

# 21. Phân quyền backend hiện tại

### STUDENT

Có quyền:

```text
View
Search
Filter
```

Không hiện:

```text
Create
Edit
Delete
```

### LECTURER

Có quyền:

```text
View
Search
Create
Edit
```

Không có delete ClassInfo.

### ADMIN

Có:

```text
View
Search
Create
Edit
Delete
Count
```

Frontend phải ẩn action theo role.

Không chỉ disable nút: nên không render action nếu user không có quyền.

---

# 22. Backend ClassInfo hiện chưa giống Student/Class model chuẩn

Các field đang thấy trong service:

```text
id
appId
name
softwareType
domain
targetOperator
review
reviewCount
installationCount
iconPath
templateDetailId
```

Đây không giống một model lớp học thông thường.

Do đó:

### Không được

```text
- sửa ClassInfo entity tùy tiện
- rename backend field chỉ để UI đẹp
- phá API hiện tại
```

### Nên

Tạo adapter frontend:

```ts
type BackendClassInfo = {
  id?: string;
  appId?: string;
  name?: string;
  softwareType?: string;
  domain?: string;
  targetOperator?: string;
  review?: string;
  reviewCount?: number;
  installationCount?: number;
  iconPath?: string;
  templateDetailId?: string;
};
```

Map sang UI model:

```ts
type ClassViewModel = {
  id: string;
  name: string;
  domain: string;
  type?: string;
  manager?: string;
  studentCount?: number;
};
```

Dữ liệu chưa có:

```text
manager
studentCount
semester
attendance
```

thì dùng mock / fallback.

---

# 23. Backend issues cần lưu ý

Không tự sửa nếu chưa được yêu cầu, nhưng frontend cần biết:

### Search

`ClassInfoDaoImpl.search(...)` hiện có dấu hiệu gọi lại chính `search(...)`.

Có khả năng recursion lỗi.

Nếu `/search` fail:

```text
fallback -> GET /list
```

sau đó:

```text
filter client-side
sort client-side
paginate client-side
```

### Count

`getCount(query)` hiện có thể đang trả tổng toàn bộ và chưa áp dụng `query`.

Không phụ thuộc vào filtered count từ endpoint này cho tới khi backend được fix.

---

# 24. API integration strategy

Tạo một Axios/fetch client:

```text
baseURL = http://localhost:8080/test
```

Theo `application.properties.example`, backend có:

```text
server.servlet.context-path=/test
```

Do đó endpoint đầy đủ có thể là:

```text
http://localhost:8080/test/api/class-info/list
```

Không hardcode ở nhiều file.

Dùng env:

```text
VITE_API_BASE_URL=http://localhost:8080/test
```

---

# 25. Class API service

Ví dụ interface:

```ts
classInfoApi.getAll()
classInfoApi.getById(id)
classInfoApi.getByName(name)
classInfoApi.getByDomain(domain)
classInfoApi.search(params)
classInfoApi.count(query)
classInfoApi.create(payload)
classInfoApi.update(payload)
classInfoApi.delete(id)
```

UI không gọi Axios trực tiếp trong page.

Page:

```text
Page -> hook/service -> API client
```

---

# 26. Mock strategy

Hiện backend chưa có đầy đủ:

```text
Students
Lecturers
Courses
Grades
Attendance
```

Không được bỏ các màn hình này.

Tạo:

```text
src/mocks/students.ts
src/mocks/lecturers.ts
src/mocks/courses.ts
```

Sau này đổi từ mock sang API mà không cần redesign component.

Gợi ý:

```ts
const USE_MOCK_STUDENTS = true;
```

hoặc qua env.

---

# 27. State management

Không cần Redux nếu project chưa phức tạp.

Dùng:

```text
React state
Context cho auth
TanStack Query nếu muốn quản lý API/cache
```

Khuyến nghị:

```text
React Router
Axios
TanStack Query
```

Nếu project cần tối giản:

```text
React Router
Axios
useState/useEffect
```

cũng đủ.

---

# 28. Table behavior

Tables phải hỗ trợ:

```text
loading
empty state
error state
pagination
sorting
filters
row click
```

Sorting:

```text
ASC
DESC
```

Pagination default:

```text
8 hoặc 10 rows/page
```

Không render 1000 rows cùng lúc.

---

# 29. Status badge

### Active

```text
Background: #E9F8F1
Text: #2F9E6F
```

### Pending

```text
Background: #FFF5DC
Text: #B7791F
```

### Inactive

```text
Background: #FDEDEF
Text: #D94F5C
```

---

# 30. Drawer behavior

Drawer mở từ phải.

Desktop:

```text
width: 460px
```

Background overlay:

```text
rgba(15, 17, 30, ~0.28)
```

Close khi:

```text
click Cancel
click X
click outside overlay
successful save
```

Không chuyển sang một page riêng chỉ để edit form.

---

# 31. Responsive behavior

Version đầu ưu tiên desktop.

### >= 1200px

Giữ full sidebar.

### 768px – 1199px

Có thể:

```text
sidebar collapse
main content scroll
table horizontal scroll
```

### Mobile

Không phải ưu tiên v1.

Nếu có thời gian:

```text
sidebar -> drawer
tables -> horizontal scroll
cards -> 1 column
```

Không cần pixel-perfect mobile nếu thầy không yêu cầu.

---

# 32. Loading / empty / error states

### Loading

Dùng skeleton.

Không dùng full-page spinner cho table.

### Empty

Ví dụ:

```text
No students found.
Try changing your filters.
```

### Error

```text
Unable to load students.
Retry
```

---

# 33. Icons

Ưu tiên:

```text
lucide-react
```

Các icon cần:

```text
LayoutDashboard
Users
BookOpen
GraduationCap
Library
Settings
Search
Bell
ChevronDown
MoreHorizontal
Plus
ArrowLeft
Edit
Trash2
```

Không dùng emoji làm icon trong production UI.

---

# 34. Authentication

Backend có Spring Security + JWT nhưng application config hiện có:

```text
spring.autoconfigure.exclude=...
```

và JWT config tồn tại.

Frontend nên chuẩn bị:

```text
AuthContext
token storage
Authorization: Bearer <token>
```

Nhưng nếu login endpoint chưa sẵn sàng:

```text
development fake auth
```

Không để thiếu login API chặn toàn bộ tiến độ frontend.

---

# 35. Role-based UI

Auth user:

```ts
type UserRole = "ADMIN" | "LECTURER" | "STUDENT";
```

Ví dụ:

```text
ADMIN:
- full sidebar
- add/edit/delete

LECTURER:
- dashboard
- students
- classes
- courses
- create/edit
- no class delete

STUDENT:
- dashboard
- classes
- profile
- view only
```

Trong v1 admin dashboard là flow chính.

---

# 36. Required reusable UI

Codex không được tạo button/input/table CSS riêng cho từng page.

Bắt buộc reuse:

```text
Button
Input
SearchInput
Select
StatusBadge
Avatar
Card
StatCard
DataTable
Drawer
Pagination
PageHeader
EmptyState
```

---

# 37. Coding style

Ưu tiên:

```text
TypeScript
Functional components
React hooks
No class components
```

Props phải có type.

Không dùng:

```text
any
```

trừ trường hợp thật sự bắt buộc.

---

# 38. CSS strategy

Có thể dùng:

```text
Tailwind CSS
```

hoặc CSS Modules.

Nếu project chưa có style system, ưu tiên Tailwind vì build dashboard nhanh.

Nhưng phải tạo reusable class/component, không copy 20 utility giống nhau khắp pages nếu có thể gom lại.

---

# 39. Suggested implementation order

Thứ tự làm:

```text
1. Setup React frontend
2. Theme / global CSS
3. AppShell
4. Sidebar
5. Topbar
6. Routing
7. Login
8. Dashboard
9. Students
10. Student Detail
11. Add/Edit Student Drawer
12. Classes
13. Class Detail
14. Lecturers
15. Courses
16. API integration
17. Role-based UI
18. Loading/error/empty states
```

---

# 40. Acceptance criteria

Frontend hoàn thành khi:

### Visual

- Giống layout Figma.
- Sidebar 240px.
- Topbar 80px.
- Font Inter.
- Primary indigo #5B5BD6.
- Card trắng trên nền #F7F8FC.
- Không có gradient rối.
- Không có quá nhiều màu.
- Table rõ, dễ đọc.

### Functional

- Login -> Dashboard.
- Sidebar navigation hoạt động.
- Students -> Student Detail.
- Add Student drawer hoạt động.
- Edit Student drawer hoạt động.
- Classes -> Class Detail.
- Search/filter/pagination UI hoạt động.
- Class API có thể gọi backend hiện tại.
- Students/Lecturers/Courses có mock data nếu API chưa có.

### Code quality

- Component tái sử dụng.
- API tách riêng khỏi page.
- Không hardcode API URL ở nhiều nơi.
- Có type rõ ràng.
- Không sửa backend tùy tiện.
- Không duplicate layout code giữa các pages.

---

# 41. Prototype flow tham chiếu

```text
Login
  ↓
Dashboard
  ├── Students
  │     ├── Add Student Drawer
  │     └── Student Detail
  │            └── Edit Student Drawer
  │
  ├── Classes
  │     └── Class Detail
  │
  ├── Lecturers
  │
  └── Courses
```

---

# 42. Điều Codex KHÔNG được làm

Không:

```text
- redesign toàn bộ UI
- đổi màu theme tùy ý
- bỏ sidebar
- biến dashboard thành landing page
- dùng card grid cho Students table
- tạo page riêng cho Add/Edit Student thay vì drawer
- sửa backend chỉ để UI dễ code
- xóa role-based access
- bỏ sorting/pagination
- nhồi chart vào mọi màn hình
- dùng UI library mặc định mà không custom theo design
```

---

# 43. Điều Codex được phép linh hoạt

Có thể tự quyết:

```text
- tên file/component chi tiết
- dùng Tailwind hay CSS Modules
- Axios hay fetch
- TanStack Query hay useEffect
- chart library
- cách tổ chức hooks
```

Miễn là:

```text
- giao diện đúng spec
- flow đúng
- backend không bị phá
- component reusable
```

---

# 44. Prompt ngắn để giao Codex

Sau khi file này nằm trong repo, có thể nói với Codex:

```text
Đọc frontend-design-spec.md và toàn bộ backend hiện có.

Hãy triển khai frontend React theo đúng spec.

Yêu cầu:
- Không redesign.
- Không sửa backend nếu không thực sự cần thiết.
- Ưu tiên tái sử dụng component.
- ClassInfo phải tích hợp API thật hiện có.
- Student/Lecturer/Course dùng mock data nếu backend chưa hỗ trợ.
- Dùng TypeScript.
- Có React Router.
- Có loading/error/empty states.
- Có role-based UI.
- Hoàn thành từng màn hình theo thứ tự trong spec.
- Sau mỗi phần lớn, tự kiểm tra build/lint trước khi tiếp tục.
```

---

# 45. Ghi chú cuối

Thiết kế hiện tại là một **admin dashboard cho hệ thống quản lý sinh viên**, không phải LMS dành cho sinh viên học online.

Phong cách mong muốn:

```text
clean
modern
academic
administrative
data-focused
compact
easy to scan
```

Từ khóa tham khảo:

```text
EduSync-inspired
school admin dashboard
student management dashboard
education SaaS dashboard
```

Giữ giao diện gọn, nhiều chức năng nhưng không gây cảm giác rối.
