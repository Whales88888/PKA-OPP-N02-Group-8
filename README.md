# Hệ Thống Quản Lý Thư Viện - PKA-OPP-N02-Group-8

## Mô tả dự án

- **Fullstack**: Backend Spring Boot 3.2.1 (Java 17, Maven), Frontend ReactJS (TypeScript, Vite)
- **Chức năng chính**:
  - Quản lý sách, độc giả
  - Quản lý mượn/trả sách, quá hạn
  - Thống kê, lịch sử mượn trả
  - Giao diện hiện đại, thông báo tiếng Việt, thao tác mượt mà
- **Cơ sở dữ liệu**: MySQL (Cloud hoặc Local)

---

## Cấu trúc thư mục

```
PKA-OPP-N02-Group-8/
├── SpringBoot/
│   ├── src/main/java/com/phenikaa/library/...   # Backend Spring Boot (Java)
│   ├── client/                                 # Frontend React (root FE)
│   └── ...
├── README.md
└── ...
```

## Yêu cầu môi trường
- Java 17+ (chạy backend)
- MySQL 8+ (database)
- Node.js 18+ (chỉ để phát triển/chạy frontend React)
- (Tùy chọn) Docker, Docker Compose

---

## Hướng dẫn cài đặt & chạy

### 1. Cài đặt database
- Tạo database MySQL, ví dụ: `library_db`
- Sửa file `SpringBoot/src/main/resources/application.properties`:
  ```
  spring.datasource.url=jdbc:mysql://switchback.proxy.rlwy.net:47428/library_db
  spring.datasource.username=root
  spring.datasource.password=... # điền mật khẩu thực tế
  ```

### 2. Backend (Spring Boot - Java)
```bash
cd SpringBoot
./mvnw clean install
./mvnw spring-boot:run
# hoặc mvn clean install && mvn spring-boot:run nếu đã cài Maven
```
- Backend chạy tại: http://localhost:8080

### 3. Frontend (React + Vite)
```bash
npm install
npm run dev
# hoặc yarn && yarn dev
```
- Frontend chạy tại: http://localhost:5000
- (FE root là thư mục `SpringBoot/client/`, đã cấu hình trong `vite.config.ts`)

### 4. (Tùy chọn) Chạy bằng Docker
- Nếu có file `docker-compose.yml`:
```bash
docker-compose up --build
```

---

## Tài khoản mặc định
- Đăng ký tài khoản mới trên giao diện frontend.
- Nếu có tài khoản admin mặc định, sẽ được ghi chú riêng hoặc gửi kèm khi bàn giao.

---

## Một số lệnh hữu ích
- Build backend: `./mvnw clean install`
- Chạy backend: `./mvnw spring-boot:run`
- Build frontend: `npm run build`
- Chạy frontend dev: `npm run dev`

---

## Tính năng nổi bật
- Quản lý sách, độc giả, mượn/trả, quá hạn, thống kê
- Thông báo, xác nhận, lỗi đều bằng tiếng Việt
- Giao diện hiện đại, responsive, dễ sử dụng
- Thống kê, lịch sử mượn trả rõ ràng
- Xử lý lỗi backend/frontend chuẩn hóa, không crash UI

---

## Liên hệ & hỗ trợ
- Nếu gặp lỗi khi cài đặt, vận hành, hoặc cần hướng dẫn chi tiết hơn, vui lòng liên hệ nhóm phát triển.




