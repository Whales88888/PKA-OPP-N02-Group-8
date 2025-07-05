// Book types
export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  category: string;
  publisher: string;
  publishYear: number;
  quantity: number;
  availableQuantity: number;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface InsertBook {
  title: string;
  author: string;
  isbn: string;
  category: string;
  publisher: string;
  publishYear: number;
  quantity: number;
  description: string;
}

// Reader types
export interface Reader {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  studentId: string;
  dateOfBirth: string;
  readerType: 'STUDENT' | 'TEACHER' | 'STAFF' | 'OTHER';
  status: 'ACTIVE' | 'SUSPENDED' | 'EXPIRED';
  expiryDate: string;
  maxBorrowBooks: number;
  currentBorrowedBooks: number;
  notes: string;
  createdAt: string;
  updatedAt: string;
}

export interface InsertReader {
  name: string;
  email: string;
  phone: string;
  address: string;
  studentId: string;
  dateOfBirth: string;
  readerType: 'STUDENT' | 'TEACHER' | 'STAFF' | 'OTHER';
  maxBorrowBooks: number;
  notes: string;
}

// Borrowing types
export interface Borrowing {
  id: number;
  bookId: number;
  readerId: number;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE';
  bookConditionBorrowed: string;
  bookConditionReturned?: string;
  fineAmount?: number;
  createdAt: string;
  updatedAt: string;
}

export interface BorrowingWithDetails {
  id: number;
  book: Book;
  reader: Reader;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: string;
  bookCondition?: string;
  createdAt: string;
}

export interface InsertBorrowing {
  bookId: number;
  readerId: number;
  borrowDate?: string;
  dueDate?: string;
}

// Dashboard types
export interface DashboardStats {
  totalBooks: number;
  availableBooks: number;
  borrowedBooks: number;
  totalReaders: number;
  activeReaders: number;
  readersWithBorrowedBooks: number;
  totalBorrowings: number;
  currentBorrowings: number;
  overdueBorrowings: number;
  returnedBorrowings: number;
}

export interface RecentActivities {
  recentBooks: Book[];
  recentReaders: Reader[];
  recentBorrowings: BorrowingWithDetails[];
  overdueBorrowings: BorrowingWithDetails[];
  dueSoonBorrowings: BorrowingWithDetails[];
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
  };
  timestamp?: string;
}

// Form error types
export interface FormErrors {
  [key: string]: string | undefined;
}

// Modal props
export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
} 