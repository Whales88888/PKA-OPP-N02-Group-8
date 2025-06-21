package library.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Lớp Book dùng cho kiểm thử
class Book {
    private final String id;
    private String title;
    private String author;

    public Book(String id, String title, String author) {
        try {
            this.id = id;
            this.title = title;
            this.author = author;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi khởi tạo Book: " + e.getMessage());
        }
    }

    public String getAuthor() {
        try {
            return author;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy tác giả: " + e.getMessage());
            return "";
        }
    }

    @Override
    public String toString() {
        try {
            return id + " - " + title + " - " + author;
        } catch (Exception e) {
            return "Lỗi khi hiển thị Book: " + e.getMessage();
        }
    }
}

// Lớp CrudManager dùng cho kiểm thử
class CrudManager<T> {
    private List<T> items = new ArrayList<>();

    public CrudManager(String filename) {
        // Không sử dụng file trong kiểm thử này
    }

    public List<T> getAll() {
        try {
            return items;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void add(T item) {
        try {
            items.add(item);
        } catch (Exception e) {
            System.out.println("Lỗi khi thêm phần tử: " + e.getMessage());
        }
    }
}

// Lớp kiểm thử chính
public class TestBookManager {
    private static final CrudManager<Book> crud = new CrudManager<>("books_test.dat");

    // Hàm lọc và hiển thị sách theo tác giả
    public static void filterBooksByAuthor() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            System.out.print("Nhập tên tác giả cần lọc: ");
            String author = scanner.nextLine();

            boolean found = false;
            for (Book book : crud.getAll()) {
                if (book.getAuthor().equalsIgnoreCase(author)) {
                    System.out.println(book);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("❌ Không tìm thấy sách của tác giả này.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi lọc sách theo tác giả: " + e.getMessage());
        } finally {
            // Không đóng scanner khi dùng với System.in
            // if (scanner != null) {
            //     scanner.close();
            //     System.out.println("Đã đóng scanner.");
            // }
        }
    }

    public static void main(String[] args) {
        try {
            // Xóa dữ liệu cũ và thêm dữ liệu mẫu để test
            crud.getAll().clear();
            crud.add(new Book("B001", "Lập trình Java", "Nguyễn Văn Anh"));
            crud.add(new Book("B002", "Cấu trúc dữ liệu", "Nguyễn Văn Bình"));
            crud.add(new Book("B003", "Giải thuật", "Nguyễn Văn Công"));

            filterBooksByAuthor();
        } catch (Exception e) {
            System.out.println("Lỗi trong chương trình chính: " + e.getMessage());
        } finally {
            System.out.println("Chương trình kết thúc.");
        }
    }
}