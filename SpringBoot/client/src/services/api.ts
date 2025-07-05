import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { API_BASE, API_ENDPOINTS } from '../config/api';
import { 
  Book, 
  Reader, 
  Borrowing, 
  BorrowingWithDetails, 
  DashboardStats, 
  RecentActivities,
  InsertBook,
  InsertReader,
  InsertBorrowing,
  ApiResponse 
} from '../types';
import { handleApiError, handleNetworkError } from '../utils/errorHandler';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Response interceptor for error handling
    this.api.interceptors.response.use(
      (response: AxiosResponse) => response,
      (error) => {
        if (error.response) {
          // Server responded with error status
          const { status, data } = error.response;
          if (data?.error?.message) {
            handleApiError(data.error.message);
          } else {
            handleApiError(`HTTP ${status}: ${error.message}`);
          }
        } else if (error.request) {
          // Network error
          handleNetworkError(error);
        } else {
          // Other error
          handleApiError(error.message);
        }
        return Promise.reject(error);
      }
    );
  }

  // Dashboard APIs
  async getDashboardStats(): Promise<DashboardStats> {
    try {
      const response = await this.api.get<DashboardStats>(API_ENDPOINTS.DASHBOARD_STATS);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async getRecentActivities(): Promise<RecentActivities> {
    try {
      const response = await this.api.get<RecentActivities>(API_ENDPOINTS.DASHBOARD_ACTIVITIES);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  // Books APIs
  async getBooks(search?: string, category?: string, page: number = 0, size: number = 20): Promise<any> {
    try {
      const params = new URLSearchParams();
      if (search) params.append('search', search);
      if (category) params.append('category', category);
      params.append('page', page.toString());
      params.append('size', size.toString());
      const response = await this.api.get(API_ENDPOINTS.BOOKS, { params });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async getBook(id: number): Promise<Book> {
    try {
      const response = await this.api.get<Book>(API_ENDPOINTS.BOOK_BY_ID(id));
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async createBook(bookData: InsertBook): Promise<Book> {
    try {
      const response = await this.api.post<Book>(API_ENDPOINTS.BOOKS, bookData);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async updateBook(id: number, bookData: Partial<InsertBook>): Promise<Book> {
    try {
      const response = await this.api.put<Book>(API_ENDPOINTS.BOOK_BY_ID(id), bookData);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async deleteBook(id: number): Promise<void> {
    try {
      await this.api.delete(API_ENDPOINTS.BOOK_BY_ID(id));
    } catch (error) {
      throw error;
    }
  }

  // Readers APIs
  async getReaders(search?: string, page: number = 0, size: number = 20): Promise<any> {
    try {
      const params = new URLSearchParams();
      if (search) params.append('search', search);
      params.append('page', page.toString());
      params.append('size', size.toString());
      const response = await this.api.get(API_ENDPOINTS.READERS, { params });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async getReader(id: number): Promise<Reader> {
    try {
      const response = await this.api.get<Reader>(API_ENDPOINTS.READER_BY_ID(id));
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async createReader(readerData: InsertReader): Promise<Reader> {
    try {
      const response = await this.api.post<Reader>(API_ENDPOINTS.READERS, readerData);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async updateReader(id: number, readerData: Partial<InsertReader>): Promise<Reader> {
    try {
      const response = await this.api.put<Reader>(API_ENDPOINTS.READER_BY_ID(id), readerData);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async deleteReader(id: number): Promise<void> {
    try {
      await this.api.delete(API_ENDPOINTS.READER_BY_ID(id));
    } catch (error) {
      throw error;
    }
  }

  // Borrowings APIs
  async getBorrowings(type?: 'active' | 'overdue' | 'history', readerId?: number, bookId?: number): Promise<BorrowingWithDetails[]> {
    try {
      let endpoint: string = API_ENDPOINTS.BORROWINGS;
      
      // Use specific endpoints for active/overdue/history
      if (type === 'active') {
        endpoint = API_ENDPOINTS.BORROWINGS_CURRENT;
      } else if (type === 'overdue') {
        endpoint = API_ENDPOINTS.BORROWINGS_OVERDUE;
      } else if (type === 'history') {
        endpoint = API_ENDPOINTS.BORROWINGS_HISTORY;
      }
      
      const response = await this.api.get<BorrowingWithDetails[]>(endpoint);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async getBorrowing(id: number): Promise<BorrowingWithDetails> {
    try {
      const response = await this.api.get<BorrowingWithDetails>(API_ENDPOINTS.BORROWING_BY_ID(id));
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async createBorrowing(borrowingData: InsertBorrowing): Promise<BorrowingWithDetails> {
    try {
      const response = await this.api.post<BorrowingWithDetails>(API_ENDPOINTS.BORROWINGS_CREATE, {
        bookId: borrowingData.bookId,
        readerId: borrowingData.readerId,
        borrowDate: borrowingData.borrowDate,
        dueDate: borrowingData.dueDate,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async returnBook(id: number, bookCondition: string): Promise<BorrowingWithDetails> {
    try {
      const response = await this.api.post<BorrowingWithDetails>(API_ENDPOINTS.BORROWINGS_RETURN(id), {
        bookCondition: bookCondition,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async renewBorrowing(id: number, additionalDays: number): Promise<BorrowingWithDetails> {
    try {
      const params = new URLSearchParams();
      params.append('additionalDays', additionalDays.toString());
      
      const response = await this.api.post<BorrowingWithDetails>(API_ENDPOINTS.BORROWINGS_RENEW(id), params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  // Utility APIs
  async getCategories(): Promise<string[]> {
    try {
      const response = await this.api.get<string[]>(API_ENDPOINTS.CATEGORIES);
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async getPublishers(): Promise<string[]> {
    try {
      const response = await this.api.get<string[]>(API_ENDPOINTS.PUBLISHERS);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
}

export const apiService = new ApiService(); 