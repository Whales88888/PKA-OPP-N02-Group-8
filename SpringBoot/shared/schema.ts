import { mysqlTable, varchar, int, boolean, date, timestamp } from "drizzle-orm/mysql-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const books = mysqlTable("books", {
  id: int("id").primaryKey().autoincrement(),
  title: varchar("title", { length: 255 }).notNull(),
  author: varchar("author", { length: 255 }).notNull(),
  isbn: varchar("isbn", { length: 50 }),
  category: varchar("category", { length: 100 }).notNull(),
  publishYear: int("publish_year"),
  publisher: varchar("publisher", { length: 255 }),
  description: varchar("description", { length: 255 }),
  quantity: int("quantity").notNull().default(1),
  availableQuantity: int("available_quantity").notNull().default(1),
  createdAt: timestamp("created_at").defaultNow(),
});

export const readers = mysqlTable("readers", {
  id: int("id").primaryKey().autoincrement(),
  name: varchar("name", { length: 255 }).notNull(),
  email: varchar("email", { length: 255 }).notNull().unique(),
  phone: varchar("phone", { length: 20 }),
  address: varchar("address", { length: 255 }),
  registrationDate: timestamp("registration_date").defaultNow(),
  isActive: boolean("is_active").notNull().default(true),
});

export const borrowings = mysqlTable("borrowings", {
  id: int("id").primaryKey().autoincrement(),
  bookId: int("book_id").notNull().references(() => books.id),
  readerId: int("reader_id").notNull().references(() => readers.id),
  borrowDate: date("borrow_date").notNull(),
  dueDate: date("due_date").notNull(),
  returnDate: date("return_date"),
  status: varchar("status", { length: 20 }).notNull().default("borrowed"), // borrowed, returned, overdue
  bookCondition: varchar("book_condition", { length: 20 }), // good, fair, damaged
  createdAt: timestamp("created_at").defaultNow(),
});

export const insertBookSchema = createInsertSchema(books).omit({
  id: true,
  availableQuantity: true,
  createdAt: true,
});

export const insertReaderSchema = createInsertSchema(readers).omit({
  id: true,
  registrationDate: true,
});

export const insertBorrowingSchema = createInsertSchema(borrowings).omit({
  id: true,
  createdAt: true,
});

export type InsertBook = z.infer<typeof insertBookSchema>;
export type Book = typeof books.$inferSelect;
export type InsertReader = z.infer<typeof insertReaderSchema>;
export type Reader = typeof readers.$inferSelect;
export type InsertBorrowing = z.infer<typeof insertBorrowingSchema>;
export type Borrowing = typeof borrowings.$inferSelect;

// Extended types for UI
export type BorrowingWithDetails = Borrowing & {
  bookTitle: string;
  bookAuthor: string;
  readerName: string;
  readerEmail: string;
};

export type BookStats = {
  totalBooks: number;
  availableBooks: number;
  borrowedBooks: number;
};

export type ReaderStats = {
  totalReaders: number;
  activeReaders: number;
};

export type BorrowingStats = {
  totalBorrowings: number;
  currentBorrowings: number;
  overdueBooks: number;
};

export type DashboardStats = {
  books: BookStats;
  readers: ReaderStats;
  borrowings: BorrowingStats;
};
