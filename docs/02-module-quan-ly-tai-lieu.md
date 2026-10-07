# Giai đoạn 2 — Bổ sung quản lý tài liệu sinh viên

## Mục tiêu

Bổ sung chức năng quản lý tài liệu vào hồ sơ sinh viên theo yêu cầu được giao. Mỗi tài liệu phải thuộc một sinh viên cụ thể, có thông tin quản lý trong cơ sở dữ liệu và nội dung file được lưu ở hệ thống lưu trữ riêng.

Chức năng được mở rộng trên website đã có, tiếp tục sử dụng giao diện, cơ chế đăng nhập và phân quyền hiện tại.

## Các công việc đã hoàn thành

| Công việc | Cách thực hiện | Kết quả |
| --- | --- | --- |
| Gắn tài liệu với sinh viên | Thiết lập quan hệ bắt buộc và khóa ngoại trong cơ sở dữ liệu. | Không thể lưu tài liệu gắn với sinh viên không tồn tại. |
| Quản lý thông tin tài liệu | Lưu tên file, người cập nhật, phiên bản, thời gian, kích thước và trạng thái. | Theo dõi được thông tin của tài liệu hiện tại. |
| Lưu nội dung file | Tích hợp hệ thống lưu trữ file riêng, kết nối thông tin file với dữ liệu quản lý. | Nội dung file và thông tin quản lý được lưu đúng vai trò của từng hệ thống. |
| Tải lên nhiều file | Cho phép chọn hoặc kéo thả nhiều file, kiểm tra trước khi lưu. | Có thể bổ sung nhiều tài liệu cho một sinh viên trong cùng lần thao tác. |
| Kiểm tra file | Kiểm tra định dạng, tên, kích thước và file trống ở cả giao diện và backend. | Từ chối file không hợp lệ khi tải lên hoặc thay thế. |
| Tải xuống | Tra cứu tài liệu theo định danh và kiểm tra quyền trước khi trả nội dung. | Người có quyền tải được file đúng nội dung và tên hiển thị. |
| Thay thế tài liệu | Ghi file mới, cập nhật thông tin và tăng phiên bản. | Thay thế thành công làm tăng phiên bản; thao tác lỗi giữ lại tài liệu cũ. |
| Xóa mềm | Đánh dấu tài liệu đã xóa và ngừng hiển thị trong luồng sử dụng thông thường. | Giữ thông tin lưu trữ, đồng thời ngăn tải xuống hoặc thay thế tài liệu đã xóa. |
| Áp dụng mẫu xử lý được yêu cầu | Dùng cấu trúc Map để truyền định danh vào bước kiểm tra và xóa tài liệu. | Phần xử lý backend thể hiện được cách làm theo yêu cầu bài tập. |
| Bổ sung giao diện | Thêm khu vực tài liệu trong hồ sơ sinh viên, bảng danh sách và các biểu mẫu thao tác. | Người dùng thực hiện các thao tác tài liệu ngay tại hồ sơ sinh viên. |
| Phân quyền | Kiểm tra quyền ở backend và điều chỉnh nút thao tác trên giao diện. | Quản trị viên và giảng viên được sử dụng các thao tác phù hợp; chỉ quản trị viên được xóa. |
| Kiểm thử | Kiểm tra tự động, build và thử luồng với cơ sở dữ liệu cùng hệ thống lưu trữ thật. | Các kiểm tra đã thực hiện đạt; các chức năng quản lý trước đó tiếp tục vượt qua kiểm thử. |

## Luồng sử dụng đã xây dựng

1. Mở hồ sơ sinh viên và xem khu vực tài liệu.
2. Chọn hoặc kéo thả file để tải lên.
3. Xem danh sách tài liệu cùng người cập nhật, phiên bản và thời gian sửa.
4. Tải xuống tài liệu khi cần.
5. Thay thế tài liệu; sau khi thành công, danh sách hiển thị phiên bản mới.
6. Xác nhận xóa nếu có quyền; tài liệu được loại khỏi danh sách sử dụng.

Giao diện có thông báo lỗi, trạng thái đang xử lý, kiểm tra file và xác nhận trước khi xóa. Các thành phần được dùng lại từ giao diện hiện có để giữ sự thống nhất.

## Quy tắc xử lý đã hoàn thiện

- Chấp nhận các định dạng PDF, XLSX, XLS, DOC, DOCX, JPG và JPEG, không phân biệt chữ hoa hoặc chữ thường.
- Kiểm tra định dạng trong cả thao tác tải lên và thay thế.
- Từ chối file trống, tên không hợp lệ và file vượt giới hạn tải lên.
- Kiểm tra toàn bộ nhóm file trước khi bắt đầu lưu.
- Lấy người cập nhật từ tài khoản đã đăng nhập.
- Chỉ truy xuất file thông qua tài liệu đã được kiểm tra quyền.
- Tăng phiên bản khi thay thế thành công và cập nhật thời gian sửa.
- Giữ bản ghi và nội dung hiện tại khi xóa mềm; không cho tiếp tục tải xuống hoặc thay thế tài liệu đã xóa.
- Ngăn xóa vật lý sinh viên có lịch sử tài liệu để bảo toàn liên kết dữ liệu.
- Khi lưu thất bại, xử lý thu hồi file mới; chỉ dọn file cũ sau khi cập nhật dữ liệu thành công.

## Kết quả kiểm tra

### Kiểm thử tự động

Có 14 bài kiểm thử cho phần tài liệu, kiểm tra tải lên, định dạng, liên kết sinh viên, tải xuống, thay thế, phiên bản, xóa mềm, quyền truy cập và các tình huống lưu thất bại.

Tổng cộng 20 bài kiểm thử backend đạt, gồm 14 bài mới và 6 bài của phần quản lý trước đó. Frontend build và quá trình đóng gói ứng dụng cũng đạt.

### Kiểm tra với hệ thống thật

Đã kiểm tra trên môi trường thử nghiệm riêng với cơ sở dữ liệu SQL và hệ thống lưu trữ file thật:

- Tải lên nhiều tài liệu trong cùng lần thao tác thành công.
- Tải xuống nhận đúng nội dung file.
- Thay thế bằng định dạng không hợp lệ bị từ chối.
- Thay thế bằng file hợp lệ thành công và tăng phiên bản.
- Xóa mềm giữ bản ghi, loại khỏi danh sách và ngăn tải xuống.
- Dữ liệu sinh viên hiện có được giữ nguyên tại thời điểm kiểm tra.

## Phạm vi đã hoàn thành và phần còn lại

Đã triển khai đầy đủ các thao tác quản lý tài liệu được giao: tải lên nhiều file, kiểm tra định dạng, liệt kê, tải xuống, thay thế tăng phiên bản, xóa mềm, liên kết sinh viên, phân quyền và giao diện thao tác.

Chưa có chức năng xem hoặc tải lại nội dung các phiên bản cũ. Kiểm tra file hiện tại chưa bao gồm quét virus hay kiểm chứng toàn bộ nội dung định dạng. Việc dọn file dư khi hệ thống lưu trữ gặp sự cố chưa có tác vụ tự động đối soát. Chưa xác nhận đầy đủ thao tác giao diện bằng kiểm thử trực quan trong trình duyệt.
