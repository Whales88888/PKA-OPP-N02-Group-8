import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Lớp Borrower dùng cho kiểm thử
class Borrower {
    private final String id;
    private String borrowerName;
    private String phone;

    public Borrower(String id, String borrowerName, String phone) {
        try {
            this.id = id;
            this.borrowerName = borrowerName;
            this.phone = phone;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi khởi tạo Borrower: " + e.getMessage());
        }
    }

    public String getBorrowerName() {
        try {
            return borrowerName;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy tên người mượn: " + e.getMessage());
            return "";
        }
    }

    @Override
    public String toString() {
        try {
            return id + " - " + borrowerName + " - " + phone;
        } catch (Exception e) {
            return "Lỗi khi hiển thị Borrower: " + e.getMessage();
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
public class TestBorrowerManager {
    private static final CrudManager<Borrower> crud = new CrudManager<>("borrowers_test.dat");

    public static void searchBorrowerByName() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            System.out.print("Nhập tên người mượn cần tìm: ");
            String name = scanner.nextLine();

            boolean found = false;
            for (Borrower b : crud.getAll()) {
                if (b.getBorrowerName().toLowerCase().contains(name.toLowerCase())) {
                    System.out.println(b);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("❌ Không tìm thấy người mượn này.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm người mượn: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
                System.out.println("Đã đóng scanner.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            crud.getAll().clear();
            crud.add(new Borrower("BR001", "Nguyễn Văn Tịnh", "0123456789"));
            crud.add(new Borrower("BR002", "Trần Thị Bảy", "0987654321"));
            crud.add(new Borrower("BR003", "Nguyễn Văn Hinh", "0111222333"));

            searchBorrowerByName();
        } catch (Exception e) {
            System.out.println("Lỗi trong chương trình chính: " + e.getMessage());
        } finally {
            System.out.println("Chương trình kết thúc.");
        }
    }
} 