package library.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// Lớp Book dùng cho kiểm thử
class Book {
    private final String id;
    private String title;
    private String author;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    @Override
    public String toString() {
        return id + " - " + title + " - " + author;
    }
}

// Lớp Borrower dùng cho kiểm thử
class Borrower {
    private final String id;
    private String borrowerName;
    private String phone;

    public Borrower(String id, String borrowerName, String phone) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return id + " - " + borrowerName + " - " + phone;
    }
}

// Lớp BorrowingSlip dùng cho kiểm thử
class BorrowingSlip {
    private final String slipID;
    private Borrower borrower;
    private Book book;
    private Date borrowDate;
    private Date dueDate;

    public BorrowingSlip(String slipID, Borrower borrower, Book book, Date borrowDate, Date dueDate) {
        this.slipID = slipID;
        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return slipID + " | " + borrower + " | " + book + " | " + borrowDate + " | " + dueDate;
    }
}

// Lớp CrudManager dùng cho kiểm thử
class CrudManager<T> {
    private List<T> items = new ArrayList<>();

    public CrudManager(String filename) {
        // Không sử dụng file trong kiểm thử này
    }

    public List<T> getAll() {
        return items;
    }

    public void add(T item) {
        items.add(item);
    }
}

// Lớp kiểm thử chính
public class TestBorrowingSlipManager {
    private static final CrudManager<BorrowingSlip> crud = new CrudManager<>("slips_test.dat");

    public static void createSlip() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Nhập mã phiếu mượn: ");
            String slipID = scanner.nextLine();

            System.out.print("Nhập mã người mượn: ");
            String borrowerID = scanner.nextLine();
            System.out.print("Nhập tên người mượn: ");
            String borrowerName = scanner.nextLine();
            System.out.print("Nhập số điện thoại: ");
            String phone = scanner.nextLine();
            Borrower borrower = new Borrower(borrowerID, borrowerName, phone);

            System.out.print("Nhập mã sách: ");
            String bookID = scanner.nextLine();
            System.out.print("Nhập tên sách: ");
            String bookName = scanner.nextLine();
            System.out.print("Nhập tác giả: ");
            String author = scanner.nextLine();
            Book book = new Book(bookID, bookName, author);

            Date borrowDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(borrowDate);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date dueDate = cal.getTime();

            BorrowingSlip slip = new BorrowingSlip(slipID, borrower, book, borrowDate, dueDate);
            crud.add(slip);
            System.out.println(" Thêm phiếu mượn thành công.");
            System.out.println("Danh sách phiếu mượn hiện tại:");
            for (BorrowingSlip s : crud.getAll()) {
                System.out.println(s);
            }
            // Không đóng scanner khi dùng với System.in
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Kết thúc kiểm thử createSlip.");
        }
    }

    public static void main(String[] args) {
        try {
            crud.getAll().clear();
            createSlip();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Kết thúc kiểm thử TestBorrowingSlipManager.");
        }
    }
}