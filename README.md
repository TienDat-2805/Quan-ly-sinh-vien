# Educare — Quản lý sinh viên

Frontend React/TypeScript theo thiết kế và đặc tả giao diện được cung cấp, backend Spring Boot 3.5.3 và MariaDB của XAMPP. Frontend và API được đóng gói trong cùng một file JAR; đường dẫn ứng dụng là `/test`.

## Yêu cầu

- JDK 21 trở lên (đã kiểm tra với JDK 24).
- Node.js 22.12 trở lên và npm.
- XAMPP/MariaDB đang chạy. Không cần Apache để chạy Spring Boot; chỉ bật Apache nếu dùng phpMyAdmin.
- Không cần cài Maven riêng: repo có `mvnw.cmd` và `mvnw`.

## Chạy trên Windows

1. Bật **MySQL** trong XAMPP.
2. Chạy nội dung [database/create-database.sql](database/create-database.sql) trong tab SQL của phpMyAdmin. Database mới `quan_ly_sinh_vien` được tạo riêng; database `educaze` cũ không bị sửa.
3. Nếu tài khoản database khác mặc định `root`/mật khẩu trống, cấu hình biến môi trường trong PowerShell:

   ```powershell
   $env:DB_USERNAME = 'your_database_user'
   $env:DB_PASSWORD = 'your_database_password'
   # Nếu database ở cổng khác:
   # $env:DB_URL = 'jdbc:mariadb://127.0.0.1:3307/quan_ly_sinh_vien'
   ```

4. Từ thư mục gốc dự án, chạy:

   ```powershell
   .\start.cmd
   ```

   Có thể nhấp đúp `start.cmd` trong File Explorer. Lệnh tự build nếu chưa có JAR, chạy backend và mở trình duyệt tại **http://127.0.0.1:8080/test/** khi ứng dụng sẵn sàng. Nếu ứng dụng đang chạy, lệnh chỉ mở lại trang web. Giữ terminal chạy ứng dụng; nhấn Ctrl+C để dừng.

5. Sau khi sửa code, dừng ứng dụng bằng Ctrl+C rồi build lại bằng `.\start.cmd -Build`. Để chạy mà không tự mở trình duyệt, dùng `powershell -ExecutionPolicy Bypass -File .\scripts\start.ps1`.

Tài khoản phát triển mặc định: **admin@educare.edu.vn / admin123**. Có thể đổi mật khẩu trong Settings. Để tạo tài khoản quản trị với thông tin khác ngay lần chạy đầu, đặt `ADMIN_USERNAME` và `ADMIN_PASSWORD` trước khi khởi động; thay đổi biến môi trường không đặt lại mật khẩu của tài khoản đã tồn tại.

Schema được khởi tạo từ `src/main/resources/schema.sql`, sau đó Hibernate kiểm tra schema bằng `ddl-auto=validate`. Lần chạy đầu trong database mới sẽ thêm dữ liệu mẫu (16 sinh viên, 6 lớp, 6 giảng viên, 8 môn, 64 điểm) vào SQL. Dữ liệu mẫu dùng email thuộc miền `example.edu.vn`. Đặt `$env:DEMO_ENABLED = 'false'` trước lần chạy đầu nếu muốn database trống. Không tự đặt lại dữ liệu mỗi lần khởi động.

## Phát triển frontend

Chạy backend trước, sau đó trong terminal khác:

```powershell
cd frontend
npm.cmd ci
npm.cmd run dev
```

Mở `http://127.0.0.1:5173/test/`. Vite proxy `/test/api` đến backend, tránh cấu hình CORS thủ công. `VITE_API_BASE_URL` tập trung trong API client, mặc định `/test`; frontend có thể dùng `frontend/.env.local` để đổi giá trị này. Có thay đổi frontend thì chạy lại build để cập nhật giao diện trong JAR.

## Các chức năng đã nối SQL

- Đăng nhập thật, phiên đăng nhập HttpOnly, CSRF, Remember me và đổi mật khẩu.
- Dashboard đếm bản ghi thật, biểu đồ khoa, sinh viên/lớp gần đây và lịch sử hoạt động.
- Sinh viên: tìm kiếm, lọc khoa/lớp/trạng thái, sắp xếp, phân trang 8 dòng, chi tiết, thêm/sửa/xóa.
- Lớp: lưới card, tìm kiếm/lọc/sắp xếp, chi tiết, danh sách sinh viên, thêm/sửa/xóa.
- Giảng viên: card, tìm kiếm/lọc/sắp xếp, hồ sơ và thêm/sửa/xóa.
- Môn học: bảng, lọc/sắp xếp/phân trang và thêm/sửa/xóa.
- Điểm: thêm/sửa/xóa trong trang chi tiết sinh viên; GPA tính từ điểm và tín chỉ.
- Tài liệu sinh viên: upload nhiều file, download, replace tăng version và soft delete trong Student Detail; nội dung file nằm trong MinIO, metadata nằm trong SQL.
- Mã/email không được trùng, lớp không được vượt sĩ số; không xóa lớp có sinh viên, môn có điểm/lớp, giảng viên còn được phân công.

Điểm lưu theo thang 10. Quy đổi GPA thang 4: `8.5→4`, `8→3.5`, `7→3`, `6.5→2.5`, `5.5→2`, `5→1.5`, `4→1`, dưới 4 là 0. GPA tính theo tín chỉ, lấy học kỳ mới nhất cho mỗi môn khi học lại; học kỳ theo dạng `YYYY-1`/`YYYY-2`. Tín chỉ đạt chỉ tính một lần cho mỗi môn có điểm >= 4. Không giả định chương trình luôn có 132 tín chỉ.

Điểm danh và bài tập chưa có dữ liệu trong backend/spec v1: giao diện hiển thị `—` thay vì tạo số liệu giả.

## API và tương thích bộ khung cũ

Giữ nguyên các endpoint `/api/class-info` và tên field ban đầu. Đã bổ sung `ClassInfo`, `ClassInfoWrapper`, `HibernateGenericDao` còn thiếu và sửa lỗi đệ quy/đảo tham số trong search để backend hoạt động. Frontend dùng `class-info.api.ts` làm adapter, đọc `/api/class-info/list` và ghép metadata quản lý lớp từ `/api/classes`.

API mới: `/api/auth/*`, `/api/dashboard`, `/api/students`, `/api/classes`, `/api/lecturers`, `/api/courses`, `/api/grades`. Danh sách sinh viên nhận `search`, `faculty`, `classId`, `status`, `page`, `size`, `sort`, `direction`. CRUD dùng POST/PUT/DELETE; `/api/auth/login` nhận form `username`/`password`. API `/api/auth/csrf` cấp token cần gửi kèm các yêu cầu ghi dữ liệu.

## Module Document — bài tập tuần này

### Chạy MinIO cục bộ

Trong một terminal riêng tại thư mục gốc:

```powershell
.\minio.cmd
```

Lần đầu, script tải Go từ `go.dev`, kiểm tra SHA-256 rồi build MinIO từ nguồn cộng đồng chính thức `github.com/minio/minio`. Cần Internet; bước tải/build có thể mất vài phút và chạy lại sẽ tận dụng file tải dở/cache. Go, MinIO, dữ liệu file và credentials chỉ nằm trong `.tools/minio/`, được Git ignore. Không cài hoặc đổi PATH toàn hệ thống. Nguồn MinIO cộng đồng hiện được lưu trữ tại [repo chính thức](https://github.com/minio/minio); không phụ thuộc đường dẫn tải binary cũ.

MinIO dùng API `http://127.0.0.1:9000`, Console `http://127.0.0.1:9001`, bucket `educare-documents`. Bucket được backend tạo khi upload lần đầu và không đặt public. Credentials ngẫu nhiên được tạo một lần trong `.tools/minio/credentials.json`, không in secret ra output của script.

Giữ terminal MinIO đang chạy, rồi chạy trong terminal thứ hai:

```powershell
.\start.cmd -Build
```

Script chạy ứng dụng tự đọc credentials cục bộ nếu bạn chưa đặt `MINIO_ACCESS_KEY`/`MINIO_SECRET_KEY`. Sau khi đã build, dùng `.\start.cmd` như bình thường. Nếu dùng MinIO khác, cấu hình trước khi khởi động:

```powershell
$env:MINIO_ENDPOINT = 'http://127.0.0.1:9000'
$env:MINIO_ACCESS_KEY = 'your_access_key'
$env:MINIO_SECRET_KEY = 'your_secret_key'
$env:MINIO_BUCKET = 'educare-documents'
```

Không đưa credentials thật vào Git. Các chức năng đăng nhập/CRUD cũ vẫn hoạt động nếu MinIO chưa chạy hoặc chưa cấu hình; thao tác đọc/ghi binary trả `503` với thông báo rõ ràng khi storage không sẵn sàng. Danh sách tài liệu vẫn đọc từ SQL.

### Schema và foreign key

`schema.sql` tự thêm bảng `documents` khi khởi động backend. Bảng có `ID` UUID, `APP_DETAIL_FK` BIGINT, `NAME`, `PATH`, `OWNER`, `VERSION` DECIMAL(10,2), `DELETED`, `ACTIVE`, `CREATED_TIME`, `MODIFIED_TIME`, `CONTENT_TYPE`, `SIZE_BYTES`.

```sql
CONSTRAINT fk_document_student
  FOREIGN KEY (APP_DETAIL_FK) REFERENCES students(id)
```

Đây là foreign key thực: DB từ chối tài liệu trỏ đến sinh viên không tồn tại. Sinh viên đã có lịch sử tài liệu, kể cả soft delete, được giữ lại; API xóa sinh viên trả `409` để bảo toàn FK và lịch sử. Sinh viên chưa có tài liệu vẫn xóa được theo luồng cũ. Không dùng `ON DELETE CASCADE` cho tài liệu và không lưu binary/blob trong MariaDB.

### API tài liệu

Các đường dẫn dưới đây nằm sau context `/test`:

| Method | Endpoint | Input / kết quả |
| --- | --- | --- |
| GET | `/api/students/{studentId}/documents` | List metadata đang active, chưa deleted |
| POST | `/api/students/{studentId}/documents` | multipart `files` (nhiều file), trả metadata, `201` |
| PUT | `/api/documents/{id}` | multipart `file`, thay binary và tăng version |
| GET | `/api/documents/{id}/download` | Binary với Content-Type, Length và Content-Disposition attachment |
| DELETE | `/api/documents/{id}` | Soft delete (`deleted=true`, `active=false`), `204` |

ADMIN/LECTURER được list/upload/replace/download; chỉ ADMIN được delete. Các request ghi vẫn cần CSRF. Client gửi document ID; object key được tra từ DB và không nhận arbitrary storage path. OWNER lấy từ principal đã xác thực, không nhận từ multipart body. Service dùng `fetchAndValidateDocumentById(Map<String,String>)` và `delete(Map<String,String>)` với khóa `ID` như mẫu thầy.

Extension được kiểm tra ở backend cho cả create/replace: **PDF, XLSX, XLS, DOC, DOCX, JPG, JPEG**, không phân biệt hoa/thường. File trống, filename không hợp lệ và batch ngoài 1–20 file bị từ chối. Mặc định **20 MB/file**, **50 MB/request**; có thể đổi bằng `MAX_FILE_SIZE`/`MAX_REQUEST_SIZE` (frontend hướng dẫn theo giới hạn mặc định). Kiểm tra hiện tại là extension và kích thước; chưa có antivirus hoặc kiểm chứng toàn bộ nội dung định dạng.

Object key gồm student ID, document UUID và UUID riêng cho mỗi lần ghi, tránh collision/path traversal. Replace khóa bản ghi trong transaction, tăng version từ 1.0 lên 2.0, cập nhật owner/name/path/time. Nếu DB rollback, backend dọn file mới; file cũ chỉ được dọn sau commit. Soft delete giữ metadata và binary hiện tại; API không list/download/replace tài liệu đã xóa. Version là bộ đếm; chưa có UI tải lại các phiên bản cũ.

DB và MinIO là hai hệ thống riêng, không có transaction phân tán. Nếu MinIO đồng thời mất kết nối trong bước cleanup, object không tham chiếu có thể còn lại; backend ghi log để dọn/retry thủ công, không xóa file cũ trước khi metadata commit.

### Luồng UI và kiểm thử

Mở Students → Student Detail → **Documents**. Upload Drawer cho chọn/thả nhiều file, Replace Drawer hiển thị version mới, Download dùng blob với phiên đăng nhập, Delete xác nhận soft delete. Component dùng lại Card, DataTable, Drawer, Button, ConfirmDrawer; không thêm mục sidebar.

`DocumentIntegrationTest` dùng H2 riêng và storage mock, kiểm tra upload batch, extension hoa/thường, FK không hợp lệ, download/headers, replace/version/time, soft delete, quyền, CSRF, rollback MinIO/SQL, filename dài và bảo toàn file cũ. Chạy `mvnw.cmd test` để chạy cả 14 test Document và 6 test quản lý cũ; `npm.cmd run build` kiểm tra frontend TypeScript và bundle.

Đã kiểm tra ngày 07/10/2026: **20/20 backend tests đạt**, frontend build đạt, API client FormData/CSRF/cookie/blob đạt. Với MariaDB và MinIO native thật trong database/bucket QA riêng, đã xác minh upload PDF/DOCX cùng batch, byte download và header, reject replace EXE, replace XLSX tăng version 2.0, soft delete giữ hàng SQL (`DELETED=1`, `ACTIVE=0`) và download trả 404. Dữ liệu 16 sinh viên hiện có giữ nguyên. Kiểm tra trực quan và thao tác click trong trình duyệt chưa thực hiện được vì phiên làm việc không có trình duyệt kết nối.

ADMIN được thêm/sửa/xóa; LECTURER được xem/thêm/sửa nhưng không xóa. Các API quản lý mới dành cho ADMIN/LECTURER; API ClassInfo giữ các quyền STUDENT/LECTURER/ADMIN theo bộ khung ban đầu. V1 tập trung vào quản trị viên; chưa có portal cá nhân hoặc cơ chế gắn tài khoản STUDENT với sinh viên. Không tạo tài khoản STUDENT mẫu có quyền xem dữ liệu quản trị.

## Kiểm thử

```powershell
.\mvnw.cmd test
cd frontend
npm.cmd run build
```

Kiểm thử Java dùng H2 riêng, không sửa database XAMPP. Bao gồm phiên đăng nhập, CSRF, CRUD sinh viên, tìm kiếm, mã/email trùng, sĩ số, ràng buộc xóa, phân quyền, GPA theo tín chỉ/học lại và API ClassInfo cũ.

## Cấu trúc

```text
frontend/src/
  api/                 API client và adapter
  components/          layout và UI dùng chung, bảng, drawer
  context/             AuthContext và quyền UI
  hooks/               tải dữ liệu, loading/error và debounce
  pages/               các route theo đặc tả
  types/               kiểu dữ liệu TypeScript
src/main/java/HD/educaze/
  config/              bảo mật, dữ liệu mẫu
  dao/, service/, rest/ bộ khung và API mới
  model/, repository/  entity và truy cập SQL
  dto/                 request/response, validation
src/main/resources/    cấu hình chung và schema SQL
database/              script tạo database
scripts/               build/chạy ứng dụng
```

Không commit `target/`, `frontend/dist/`, `node_modules/`, `.tools/`, logs hoặc cấu hình chứa mật khẩu. Cấu hình chung không có thông tin bí mật nằm trong `application.yml`; cấu hình riêng có thể đặt trong `application.properties` (được ignore) theo file `.example`.
