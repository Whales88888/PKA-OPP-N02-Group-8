// API base URL for Spring Boot backend
// Use VITE_API_BASE from environment if set, otherwise default to Spring Boot port
export const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

// API endpoints for Spring Boot backend
export const API_ENDPOINTS = {
  // Dashboard
  DASHBOARD_STATS: '/dashboard/api/dashboard/stats',
  DASHBOARD_ACTIVITIES: '/dashboard/api/dashboard/recent-activities',
  DASHBOARD_NOTIFICATIONS: '/dashboard/api/dashboard/notifications',
  
  // Books
  BOOKS: '/api/books',
  BOOK_BY_ID: (id: number) => `/api/books/${id}`,
  BOOKS_SEARCH: '/api/books/search',
  
  // Readers
  READERS: '/api/readers',
  READER_BY_ID: (id: number) => `/api/readers/${id}`,
  READERS_SEARCH: '/api/readers/search',
  
  // Borrowings
  BORROWINGS: '/api/borrowings',
  BORROWING_BY_ID: (id: number) => `/api/borrowings/${id}`,
  BORROWINGS_CREATE: '/api/borrowings',
  BORROWINGS_RETURN: (id: number) => `/api/borrowings/${id}/return`,
  BORROWINGS_RENEW: (id: number) => `/api/borrowings/${id}/renew`,
  BORROWINGS_CURRENT: '/api/borrowings/current',
  BORROWINGS_OVERDUE: '/api/borrowings/overdue',
  BORROWINGS_HISTORY: '/api/borrowings/all',
  BORROWINGS_DUE_SOON: '/api/borrowings/due-soon',
  BORROWINGS_STATISTICS: '/api/borrowings/statistics',
  
  // Categories and other data
  CATEGORIES: '/api/categories',
  PUBLISHERS: '/api/publishers',
} as const; 