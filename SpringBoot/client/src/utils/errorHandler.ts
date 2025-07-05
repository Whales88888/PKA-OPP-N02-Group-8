import { toast } from '../hooks/use-toast';

export interface ApiError {
  code: string;
  message: string;
}

export const handleApiError = (error: unknown, fallbackMessage = 'Có lỗi xảy ra'): void => {
  let errorMessage = fallbackMessage;
  
  if (error instanceof Error) {
    errorMessage = error.message;
  } else if (typeof error === 'string') {
    errorMessage = error;
  } else if (error && typeof error === 'object' && 'message' in error) {
    errorMessage = String(error.message);
  }
  
  // Log error for debugging (without sensitive info)
  console.warn('API Error:', errorMessage);
  
  // Show user-friendly toast
  toast({
    title: 'Lỗi',
    description: errorMessage,
    variant: 'destructive',
  });
};

export const handleNetworkError = (error: unknown): void => {
  handleApiError(error, 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.');
};

export const handleValidationError = (error: unknown): void => {
  handleApiError(error, 'Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin.');
};

export const handleNotFoundError = (error: unknown): void => {
  handleApiError(error, 'Không tìm thấy dữ liệu yêu cầu.');
};

export const handleUnauthorizedError = (error: unknown): void => {
  handleApiError(error, 'Bạn không có quyền truy cập. Vui lòng đăng nhập lại.');
};

export const handleServerError = (error: unknown): void => {
  handleApiError(error, 'Lỗi máy chủ. Vui lòng thử lại sau.');
}; 