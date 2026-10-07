# Giai đoạn 1 — Xây dựng website quản lý sinh viên

## Mục tiêu

Phát triển bộ khung backend ban đầu thành một website quản lý sinh viên có giao diện, xử lý nghiệp vụ và lưu trữ dữ liệu. Các thao tác trên giao diện được kết nối với backend và cơ sở dữ liệu.

## Các công việc đã hoàn thành

| Công việc | Cách thực hiện | Kết quả |
| --- | --- | --- |
| Tổ chức bộ khung dự án | Sắp xếp các thành phần theo cấu trúc dự án Spring Boot, phân chia phần truy cập dữ liệu, xử lý nghiệp vụ và tiếp nhận yêu cầu. | Bộ khung được tổ chức để thuận tiện phát triển và bảo trì; nội dung code được giữ nguyên trong bước sắp xếp ban đầu. |
| Hoàn thiện backend | Bổ sung các thành phần còn thiếu, xử lý nghiệp vụ quản lý và phản hồi lỗi. | Backend phục vụ được các chức năng của website, đồng thời tiếp tục hỗ trợ chức năng từ khung mẫu. |
| Xây dựng frontend | Tạo các màn hình quản lý theo thiết kế được cung cấp, sử dụng React và TypeScript. | Có giao diện đăng nhập, tổng quan, quản lý dữ liệu và xem chi tiết. |
| Chuẩn hóa thành phần giao diện | Dùng chung bảng, thẻ thông tin, nút thao tác, biểu mẫu và hộp xác nhận. | Các màn hình có cách trình bày và thao tác thống nhất. |
| Kết nối cơ sở dữ liệu | Thiết lập lưu trữ bằng SQL và liên kết dữ liệu theo nghiệp vụ. | Thao tác thêm, sửa, xóa và tìm kiếm làm việc với dữ liệu được lưu trong cơ sở dữ liệu. |
| Kết nối frontend với backend | Gửi yêu cầu từ giao diện, hiển thị kết quả và phản hồi lỗi từ backend. | Giao diện và backend hoạt động cùng nhau trong các luồng quản lý. |
| Bổ sung đăng nhập và phân quyền | Xác thực tài khoản, quản lý phiên và kiểm tra quyền trước khi thực hiện thao tác. | Người dùng sử dụng chức năng theo quyền được cấp. |
| Đơn giản hóa việc khởi chạy | Bổ sung cơ chế build, đóng gói và mở ứng dụng. | Việc chạy website và cập nhật bản build thuận tiện hơn. |
| Kiểm thử | Kiểm tra nghiệp vụ backend và quá trình build frontend. | Các bài kiểm thử đã thực hiện đạt; ứng dụng được đóng gói thành công. |

## Chức năng đã triển khai

### Đăng nhập và tài khoản

Đã triển khai đăng nhập, ghi nhớ đăng nhập, đăng xuất và đổi mật khẩu. Backend kiểm tra phiên và quyền truy cập khi xử lý yêu cầu.

### Trang tổng quan

Hiển thị số lượng dữ liệu quản lý, thống kê theo khoa, sinh viên và lớp gần đây, cùng lịch sử hoạt động. Thông tin được lấy từ dữ liệu đã lưu.

### Quản lý sinh viên

Có danh sách, tìm kiếm, lọc theo khoa, lớp và trạng thái, sắp xếp và phân trang. Người có quyền có thể thêm, sửa, xóa sinh viên và xem hồ sơ chi tiết.

### Quản lý lớp học

Có danh sách lớp, tìm kiếm, lọc, sắp xếp và thao tác thêm, sửa, xóa. Trang chi tiết hiển thị thông tin lớp và các sinh viên thuộc lớp.

### Quản lý giảng viên và môn học

Có danh sách, tìm kiếm hoặc lọc, xem thông tin và thao tác thêm, sửa, xóa theo quyền. Dữ liệu được liên kết với lớp và môn học theo nghiệp vụ.

### Quản lý điểm

Có thao tác thêm, sửa, xóa điểm trong hồ sơ sinh viên. Điểm trung bình được tính theo tín chỉ và xử lý trường hợp học lại; tín chỉ đạt được tổng hợp theo kết quả học tập.

## Quy tắc nghiệp vụ đã bổ sung

- Kiểm tra dữ liệu nhập và ngăn mã hoặc email bị trùng.
- Kiểm soát sĩ số khi đưa sinh viên vào lớp.
- Ngăn xóa lớp đang có sinh viên hoặc dữ liệu còn được nghiệp vụ khác tham chiếu.
- Kiểm tra quyền đối với thao tác quản lý và bảo vệ yêu cầu thay đổi dữ liệu.
- Ghi nhận lịch sử hoạt động để theo dõi các thao tác quản lý.

## Kết quả kiểm tra

- Có 6 bài kiểm thử tích hợp backend, bao phủ các nhóm nghiệp vụ đăng nhập, quyền truy cập, quản lý dữ liệu, ràng buộc và tính điểm.
- Cả 6 bài kiểm thử tiếp tục đạt trong lần kiểm tra sau khi mở rộng chức năng tài liệu.
- Frontend build thành công; backend build và đóng gói thành công.
- Các màn hình quản lý đã được nối với dữ liệu SQL.

## Phạm vi đã hoàn thành và phần còn lại

Đã hoàn thành nền tảng website quản lý sinh viên với frontend, backend và cơ sở dữ liệu, bao gồm các chức năng quản lý nêu trên.

Điểm danh, bài tập và cổng sử dụng riêng cho sinh viên chưa thuộc phần đã triển khai. Chưa xác nhận đầy đủ thao tác giao diện bằng kiểm thử trực quan trong trình duyệt.
