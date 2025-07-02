# HỆ THỐNG QUẢN LÝ THƯ VIỆN - PKA-OPP-N02-Group-8

# Mục tiêu dự án / Project Goals

Xây dựng hệ thống quản lý thư viện hiện đại cho trường đại học, hỗ trợ quản lý sách, độc giả, giao dịch mượn/trả, cảnh báo quá hạn, báo cáo thống kê, và các chức năng liên quan.

Build a modern library management system for universities, supporting book management, reader management, borrow/return transactions, overdue alerts, reporting, and related features.

---

# Tính năng chính / Main Features

- Quản lý sách: Thêm, sửa, xóa, tìm kiếm, phân loại, theo dõi lịch sử mượn.
- Quản lý độc giả: Đăng ký, cập nhật, phân loại, quản lý trạng thái thẻ, theo dõi lịch sử mượn.
- Quản lý giao dịch mượn/trả: Tạo giao dịch, trả/gia hạn sách, cảnh báo quá hạn.
- Báo cáo thống kê: Sách mượn nhiều, độc giả tích cực, tình trạng sách.
- Giao diện web hiện đại, dễ sử dụng.

---

# Công nghệ sử dụng / Technology Stack

- Frontend: React, TypeScript, TailwindCSS, Vite
- Backend: Node.js (Express), TypeScript
- Database:*PostgreSQL (qua Drizzle ORM)
- Khác: Spring Boot (Java) cho các module mẫu, Docker (nếu cần)

---

# Hướng dẫn cài đặt & chạy / Setup & Run Instructions

 1. Backend (Node.js)

```bash
cd SpringBoot
npm install
npm run dev
```
- Mặc định chạy tại: http://localhost:5000

 2. Frontend (React)

```bash
cd SpringBoot/client
npm install
npm run dev
```
- Mặc định chạy tại: http://localhost:5173

3. Database
- Sử dụng PostgreSQL.
- Cấu hình kết nối qua biến môi trường `DATABASE_URL` trong file `.env` hoặc qua `drizzle.config.ts`.
- Khởi tạo bảng với script SQL trong `SpringBoot/database/init.sql` nếu cần.

---

# Sử dụng / Usage

- Truy cập giao diện web để quản lý sách, độc giả, mượn/trả, xem báo cáo.
- Đăng nhập bằng tài khoản quản trị (nếu có).
- Thực hiện các thao tác CRUD trên sách, độc giả, giao dịch.

---

# Kiểm thử / Testing

- Chạy test backend: `npm test` trong thư mục `SpringBoot`.
- Chạy test frontend: `npm test` trong thư mục `SpringBoot/client` (nếu có cấu hình).

---

# API

- Các endpoint chính:
  - `GET /api/books` - Lấy danh sách sách
  - `POST /api/books` - Thêm sách mới
  - `GET /api/readers` - Lấy danh sách độc giả
  - `POST /api/borrowings` - Tạo giao dịch mượn
  - ...
- Xem chi tiết trong mã nguồn `SpringBoot/server/routes.ts`

---

# Đóng góp / Contribution

1. Fork dự án, tạo branch mới.
2. Commit thay đổi, tạo pull request.
3. Mô tả rõ ràng tính năng/sửa lỗi bạn đóng góp.

---

# Bản quyền / License

- Dự án thuộc sở hữu nhóm PKA-OPP-N02-Group-8.
- License: MIT (hoặc ghi rõ nếu khác)

---

# Liên hệ / Contact

- Email: [your-email@example.com]
- Nhóm phát triển: PKA-OPP-N02-Group-8


1. Thành viên
- Nguyễn Thị Minh Hằng
- Bùi Văn Khoa
- Đỗ Vân Khánh

2. Tiêu đề
*Quản lý thư viện*

3. Đối tượng quản lý
- Book (Sách): Mã sách, tên sách, tác giả
- Borrower (Người mượn): Mã người mượn, tên, số điện thoại, danh sách sách đang mượn
- BorrowingSlip (Phiếu mượn): Mã phiếu, người mượn, sách, ngày mượn, hạn trả

4. Cấu trúc thư mục Project
```
PKA-OPP-N02-Group-8/
├── SpringBoot/
│   ├── client/           # Frontend React app
│   ├── server/           # Node.js backend API
│   ├── database/         # DB scripts (init.sql)
│   ├── src/              # Java Spring Boot backend (main/resources, main/java/com/phenikaa/library)
│   ├── shared/           # Shared code/schema
│   └── ...               # Other config files
├── src/                  # Old Java OOP code (library management, test classes)
├── gs-serving-web-content-main/ # Sample Spring Boot project (not part of main app)
├── README.md             # Main project readme
├── settings.json
└── .vscode/
```

5. Chức năng chính
- Quản lý sách: Thêm, sửa, xóa, liệt kê, tìm kiếm, lọc sách.
- Quản lý người mượn: Thêm, sửa, xóa, liệt kê người mượn.
- Quản lý phiếu mượn: Thêm, sửa, xóa, liệt kê phiếu mượn, gán sách cho phiếu mượn, liên kết người mượn với phiếu mượn.
- Lưu trữ dữ liệu: Dữ liệu được lưu trữ bằng các Collection như ArrayList, lưu file nhị phân.
- Chức năng mở rộng: Thống kê, cảnh báo trễ hạn, tìm sách mượn nhiều nhất.

6. Sơ đồ UML
- Class Diagram: [Xem tại đây](https://drive.google.com/file/d/1nDPU4V0jP4qDLwa3WDVNuQa_KLGO881w/view?usp=sharing)
- Sequence Diagram: [Xem tại đây](https://drive.google.com/file/d/1nDPU4V0jP4qDLwa3WDVNuQa_KLGO881w/view?usp=sharing)
- Activity Diagram: (Bổ sung nếu có)

7. Hướng dẫn chạy chương trình
- Biên dịch toàn bộ mã nguồn:
  ```bash
  javac -d out src/main/java/library/*.java
  ```
- Chạy chương trình quản lý:
  ```bash
  java -cp out library.QuanLi
  ```

8. CRUD cho 3 đối tượng
- Book: CRUD trong `BookManager.java`
- Borrower: CRUD trong `BorrowerManager.java`
- BorrowingSlip: CRUD trong `BorrowingSlipManager.java`

9. Kiểm thử/Test
- Các file kiểm thử: `TestBook.java`, `TestBorrower.java`, `TestBorrowingSlip.java`
- Chạy kiểm thử:
  ```bash
  java -cp out library.TestBook
  java -cp out library.TestBorrower
  java -cp out library.TestBorrowingSlip
  ```

10. Lưu đồ thuật toán cảnh báo sách gần đến hạn trả
```
Bắt đầu
   |
Nhập mã bạn đọc
   |
Lấy danh sách phiếu mượn của bạn đọc
   |
Kiểm tra từng phiếu mượn:
   |-- Nếu ngày trả còn ≤ 3 ngày --> Thêm vào danh sách cảnh báo
   |-- Ngược lại --> Bỏ qua
   |
In danh sách cảnh báo
   |
Kết thúc
```

11. Chức năng cảnh báo sách gần đến hạn trả
- Thông báo danh sách sách đã mượn bởi bạn đọc, gần đến ngày phải trả.

12. Phân chia công việc
- Nguyễn Thị Minh Hằng: Viết hàm lấy danh sách phiếu mượn
- Bùi Văn Khoa: Viết hàm kiểm tra gần đến hạn trả
- Đỗ Vân Khánh: Viết hàm tổng hợp và in cảnh báo

13. Ảnh giao diện chức năng cảnh báo sách gần đến hạn trả
- [Ảnh chạy chương trình, giao diện cảnh báo sách gần đến hạn trả](https://drive.google.com/drive/folders/1br1l1kf0BAGJCVGVEAaElXbE0fobXPOs?usp=sharing)

DATABASE_URL="mysql://username:password@localhost:3306/library_db"




