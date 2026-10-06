# Educare — Quản lý sinh viên

Frontend React/TypeScript theo `frontend-design-spec.md`, backend Spring Boot 3.5.3 và MariaDB của XAMPP. Frontend và API được đóng gói trong cùng một file JAR; đường dẫn ứng dụng là `/test`.

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
- Mã/email không được trùng, lớp không được vượt sĩ số; không xóa lớp có sinh viên, môn có điểm/lớp, giảng viên còn được phân công.

Điểm lưu theo thang 10. Quy đổi GPA thang 4: `8.5→4`, `8→3.5`, `7→3`, `6.5→2.5`, `5.5→2`, `5→1.5`, `4→1`, dưới 4 là 0. GPA tính theo tín chỉ, lấy học kỳ mới nhất cho mỗi môn khi học lại; học kỳ theo dạng `YYYY-1`/`YYYY-2`. Tín chỉ đạt chỉ tính một lần cho mỗi môn có điểm >= 4. Không giả định chương trình luôn có 132 tín chỉ.

Điểm danh và bài tập chưa có dữ liệu trong backend/spec v1: giao diện hiển thị `—` thay vì tạo số liệu giả.

## API và tương thích bộ khung cũ

Giữ nguyên các endpoint `/api/class-info` và tên field ban đầu. Đã bổ sung `ClassInfo`, `ClassInfoWrapper`, `HibernateGenericDao` còn thiếu và sửa lỗi đệ quy/đảo tham số trong search để backend hoạt động. Frontend dùng `class-info.api.ts` làm adapter, đọc `/api/class-info/list` và ghép metadata quản lý lớp từ `/api/classes`.

API mới: `/api/auth/*`, `/api/dashboard`, `/api/students`, `/api/classes`, `/api/lecturers`, `/api/courses`, `/api/grades`. Danh sách sinh viên nhận `search`, `faculty`, `classId`, `status`, `page`, `size`, `sort`, `direction`. CRUD dùng POST/PUT/DELETE; `/api/auth/login` nhận form `username`/`password`. API `/api/auth/csrf` cấp token cần gửi kèm các yêu cầu ghi dữ liệu.

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
