import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Skeleton } from "@/components/ui/skeleton";
import { useToast } from "@/hooks/use-toast";
import { queryClient } from "@/lib/queryClient";
import { apiRequest } from "@/lib/queryClient";
import { formatDate, isOverdue, calculateDaysUntilDue, getStatusColor, getStatusText } from "@/lib/utils";
import { BookOpen, Undo, AlertTriangle, History } from "lucide-react";
import type { BorrowingWithDetails, Book, Reader } from "@shared/schema";
import { API_BASE } from "@/config/api";

export default function Borrowing() {
  const [borrowForm, setBorrowForm] = useState({
    readerId: "",
    bookId: "",
    borrowDate: new Date().toISOString().split('T')[0],
    dueDate: "",
  });
  const [returnForm, setReturnForm] = useState({
    borrowingId: "",
    condition: "good",
  });

  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Calculate default due date (14 days from borrow date)
  const calculateDueDate = (borrowDate: string) => {
    const date = new Date(borrowDate);
    date.setDate(date.getDate() + 14);
    return date.toISOString().split('T')[0];
  };

  // Update due date when borrow date changes
  const handleBorrowDateChange = (date: string) => {
    setBorrowForm(prev => ({
      ...prev,
      borrowDate: date,
      dueDate: calculateDueDate(date)
    }));
  };

  // Fetch borrowings
  const { data: borrowings, error: borrowingsError, isLoading: borrowingsLoading } = useQuery({
    queryKey: ['borrowings', { type: 'all' }],
    queryFn: () =>
      fetch(`${API_BASE}/api/borrowings`).then(r => {
        if (!r.ok) throw new Error('Failed to load borrowings');
        return r.json();
      }).catch(console.error)
  });

  // Fetch books
  const { data: books } = useQuery({
    queryKey: ["books"],
    queryFn: async () => {
      return fetch(`${API_BASE}/api/books`)
        .then(res => res.json())
        .catch(console.error);
    },
  });

  // Fetch readers
  const { data: readers } = useQuery({
    queryKey: ["readers"],
    queryFn: async () => {
      return fetch(`${API_BASE}/api/readers`)
        .then(res => res.json())
        .catch(console.error);
    },
  });

  // Create borrowing
  const createBorrowingMutation = useMutation({
    mutationFn: async (data: any) => {
      // The backend expects bookId and readerId as query params, not JSON body
      const params = new URLSearchParams({
        bookId: data.bookId,
        readerId: data.readerId
      });
      return fetch(`${API_BASE}/api/borrowings?${params.toString()}`, {
        method: "POST"
      })
        .then(res => res.json())
        .catch(console.error);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["borrowings"] });
      queryClient.invalidateQueries({ queryKey: ["books"] });
      toast({ title: "Success", description: "Borrowing created successfully" });
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to create borrowing", variant: "destructive" });
    },
  });

  // Return borrowing
  const returnBookMutation = useMutation({
    mutationFn: async (borrowingId: number) => {
      // The backend expects bookCondition as a query param for return
      return fetch(`${API_BASE}/api/borrowings/${borrowingId}/return?bookCondition=good`, {
        method: "POST"
      })
        .then(res => res.json())
        .catch(console.error);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['borrowings'] });
      queryClient.invalidateQueries({ queryKey: ['stats'] });
      toast({ title: "Success", description: "Book returned successfully" });
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to return book", variant: "destructive" });
    },
  });

  const { data: activeBorrowings } = useQuery({
    queryKey: ['borrowings', { type: 'active' }],
    queryFn: () =>
      fetch(`${API_BASE}/api/borrowings/current`).then(r => {
        if (!r.ok) throw new Error('Failed to load active borrowings');
        return r.json();
      }).catch(console.error)
  });

  const { data: overdueBorrowings } = useQuery({
    queryKey: ['borrowings', { type: 'overdue' }],
    queryFn: () =>
      fetch(`${API_BASE}/api/borrowings/overdue`).then(r => {
        if (!r.ok) throw new Error('Failed to load overdue borrowings');
        return r.json();
      }).catch(console.error)
  });

  // Map backend fields to frontend camelCase
  function mapBookApi(book: any): Book {
    return {
      ...book,
      availableQuantity: book.availableQuantity ?? book.available_quantity,
      publishYear: book.publishYear ?? book.publication_year,
      // add more mappings as needed
    };
  }
  function mapReaderApi(reader: any): Reader {
    return {
      ...reader,
      registrationDate: reader.registrationDate ?? reader.registration_date,
      expiryDate: reader.expiryDate ?? reader.expiry_date,
      readerType: reader.readerType ?? reader.reader_type,
      isActive: reader.isActive ?? (reader.status === 'ACTIVE' || reader.status === true),
      // add more mappings as needed
    };
  }
  function mapBorrowingApi(borrowing: any): BorrowingWithDetails {
    return {
      ...borrowing,
      borrowDate: borrowing.borrowDate ?? borrowing.borrow_date,
      dueDate: borrowing.dueDate ?? borrowing.due_date,
      returnDate: borrowing.returnDate ?? borrowing.return_date,
      bookTitle: borrowing.bookTitle ?? borrowing.book_title,
      bookAuthor: borrowing.bookAuthor ?? borrowing.book_author,
      readerName: borrowing.readerName ?? borrowing.reader_name,
      readerEmail: borrowing.readerEmail ?? borrowing.reader_email,
      // add more mappings as needed
    };
  }
  const borrowingsArray = (Array.isArray(borrowings) ? borrowings : borrowings?.content || []).map(mapBorrowingApi);
  const booksArray = (Array.isArray(books) ? books : books?.content || []).map(mapBookApi);
  const readersArray = (Array.isArray(readers) ? readers : readers?.content || []).map(mapReaderApi);

  const availableBooks = booksArray?.filter((book: Book) => book.availableQuantity > 0) || [];
  const activeBooksForReturn: BorrowingWithDetails[] = Array.isArray(activeBorrowings) ? activeBorrowings : [];

  // Initialize due date on first render
  if (!borrowForm.dueDate && borrowForm.borrowDate) {
    setBorrowForm(prev => ({ ...prev, dueDate: calculateDueDate(prev.borrowDate) }));
  }

  // Utility to check if a borrowing is overdue (not returned and dueDate < now)
  function isBorrowingOverdue(borrowing: BorrowingWithDetails) {
    if (borrowing.returnDate) return false;
    return new Date(borrowing.dueDate) < new Date();
  }

  // Utility to calculate overdue days (only if not returned)
  function getOverdueDays(borrowing: BorrowingWithDetails) {
    if (borrowing.returnDate) return 0;
    const due = new Date(borrowing.dueDate);
    const now = new Date();
    if (isNaN(due.getTime())) return 0;
    if (now <= due) return 0;
    return Math.ceil((now.getTime() - due.getTime()) / (1000 * 60 * 60 * 24));
  }

  function handleBorrowSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!borrowForm.readerId || !borrowForm.bookId) {
      toast({
        title: "Lỗi",
        description: "Vui lòng chọn độc giả và sách.",
        variant: "destructive",
      });
      return;
    }
    createBorrowingMutation.mutate({
      readerId: parseInt(borrowForm.readerId),
      bookId: parseInt(borrowForm.bookId),
      borrowDate: borrowForm.borrowDate,
      dueDate: borrowForm.dueDate,
      status: "borrowed",
    });
  }

  // Add the missing handler
  const handleReturnSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!returnForm.borrowingId) return;
    await fetch(`${API_BASE}/api/borrowings/${returnForm.borrowingId}/return?bookCondition=${encodeURIComponent(returnForm.condition)}`, {
      method: 'POST'
    });
    queryClient.invalidateQueries({ queryKey: ['borrowings'] });
    queryClient.invalidateQueries({ queryKey: ['stats'] });
  };

  // Defensive fallback for availableBooks and overdueBorrowings
  const safeAvailableBooks = Array.isArray(availableBooks) ? availableBooks : [];
  const safeOverdueBorrowings = Array.isArray(overdueBorrowings) ? overdueBorrowings : [];

  if (borrowingsLoading) {
    return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {Array.from({ length: 4 }).map((_, i) => (
            <Card key={i} className="animate-pulse">
              <CardContent className="p-6">
                <div className="h-4 bg-slate-200 rounded w-24 mb-2"></div>
                <div className="h-8 bg-slate-200 rounded w-16 mb-4"></div>
                <div className="h-4 bg-slate-200 rounded w-32"></div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }
  if (borrowingsError) return <div className="p-4 bg-red-100 text-red-700 rounded mb-4">Lỗi: {borrowingsError.message}</div>;

  return (
    <div className="space-y-6">
      <Tabs defaultValue="borrow" className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="borrow" className="flex items-center space-x-2">
            <BookOpen className="w-4 h-4" />
            <span>Cho mượn</span>
          </TabsTrigger>
          <TabsTrigger value="return" className="flex items-center space-x-2">
            <Undo className="w-4 h-4" />
            <span>Trả sách</span>
          </TabsTrigger>
          <TabsTrigger value="overdue" className="flex items-center space-x-2">
            <AlertTriangle className="w-4 h-4" />
            <span>Quá hạn</span>
          </TabsTrigger>
          <TabsTrigger value="history" className="flex items-center space-x-2">
            <History className="w-4 h-4" />
            <span>Lịch sử</span>
          </TabsTrigger>
        </TabsList>

        <TabsContent value="borrow" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Borrow Form */}
            <Card>
              <CardHeader>
                <CardTitle>Cho mượn sách</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleBorrowSubmit} className="space-y-4">
                  <div className="form-field">
                    <Label className="form-label">Độc giả *</Label>
                    <Select 
                      value={borrowForm.readerId} 
                      onValueChange={(value) => setBorrowForm(prev => ({ ...prev, readerId: value }))}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Chọn độc giả..." />
                      </SelectTrigger>
                      <SelectContent>
                        {readersArray?.map((reader: Reader) => (
                          <SelectItem key={reader.id} value={reader.id.toString()}>
                            {reader.name} - {reader.email}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="form-field">
                    <Label className="form-label">Sách *</Label>
                    <Select 
                      value={borrowForm.bookId} 
                      onValueChange={(value) => setBorrowForm(prev => ({ ...prev, bookId: value }))}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Chọn sách..." />
                      </SelectTrigger>
                      <SelectContent>
                        {safeAvailableBooks.map((book: Book) => (
                          <SelectItem key={book.id} value={book.id.toString()}>
                            {book.title} - {book.author} (Còn: {book.availableQuantity})
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="form-field">
                    <Label className="form-label">Ngày mượn *</Label>
                    <Input
                      type="date"
                      value={borrowForm.borrowDate}
                      onChange={(e) => handleBorrowDateChange(e.target.value)}
                      className="form-input"
                    />
                  </div>

                  <div className="form-field">
                    <Label className="form-label">Ngày hẹn trả *</Label>
                    <Input
                      type="date"
                      value={borrowForm.dueDate}
                      onChange={(e) => setBorrowForm(prev => ({ ...prev, dueDate: e.target.value }))}
                      className="form-input"
                    />
                  </div>

                  <Button 
                    type="submit" 
                    className="w-full btn-primary"
                    disabled={createBorrowingMutation.isPending}
                  >
                    {createBorrowingMutation.isPending ? "Đang xử lý..." : "Xác nhận cho mượn"}
                  </Button>
                </form>
              </CardContent>
            </Card>

            {/* Statistics */}
            <Card>
              <CardHeader>
                <CardTitle>Thống kê mượn/trả</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-blue-50 p-4 rounded-lg">
                    <p className="text-sm text-blue-600 font-medium">Đang mượn</p>
                    <p className="text-2xl font-bold text-blue-800">
                      {activeBorrowings?.length || 0}
                    </p>
                  </div>
                  <div className="bg-red-50 p-4 rounded-lg">
                    <p className="text-sm text-red-600 font-medium">Quá hạn</p>
                    <p className="text-2xl font-bold text-red-800">
                      {safeOverdueBorrowings.length || 0}
                    </p>
                  </div>
                </div>
                
                <div className="space-y-2">
                  <h4 className="font-medium">Sách có sẵn</h4>
                  <div className="max-h-48 overflow-y-auto space-y-1">
                    {safeAvailableBooks.slice(0, 5).map((book: Book) => (
                      <div key={book.id} className="flex justify-between text-sm">
                        <span className="truncate">{book.title}</span>
                        <span className="text-slate-500">{book.availableQuantity}</span>
                      </div>
                    ))}
                    {safeAvailableBooks.length > 5 && (
                      <p className="text-xs text-slate-500">
                        ...và {safeAvailableBooks.length - 5} sách khác
                      </p>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="return" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Return Form */}
            <Card>
              <CardHeader>
                <CardTitle>Trả sách</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleReturnSubmit} className="space-y-4">
                  <div className="form-field">
                    <Label className="form-label">Sách cần trả *</Label>
                    <Select 
                      value={returnForm.borrowingId} 
                      onValueChange={(value) => setReturnForm(prev => ({ ...prev, borrowingId: value }))}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Chọn sách cần trả..." />
                      </SelectTrigger>
                      <SelectContent>
                        {activeBooksForReturn.map((borrowing: BorrowingWithDetails) => (
                          <SelectItem key={borrowing.id} value={borrowing.id.toString()}>
                            {borrowing.bookTitle} - {borrowing.readerName}
                            {isBorrowingOverdue(borrowing) && " (QUÁ HẠN)"}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  {returnForm.borrowingId && (
                    <div className="bg-slate-50 rounded-lg p-4 space-y-2">
                      {(() => {
                        const selectedBorrowing = activeBooksForReturn.find(
                          b => b.id.toString() === returnForm.borrowingId
                        );
                        if (!selectedBorrowing) return null;
                        
                        return (
                          <>
                            <div className="flex justify-between text-sm">
                              <span className="text-slate-600">Tên sách:</span>
                              <span className="font-medium">{selectedBorrowing.bookTitle}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                              <span className="text-slate-600">Độc giả:</span>
                              <span className="font-medium">{selectedBorrowing.readerName}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                              <span className="text-slate-600">Ngày mượn:</span>
                              <span className="font-medium">{selectedBorrowing.borrowDate ? formatDate(selectedBorrowing.borrowDate) : '-'}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                              <span className="text-slate-600">Hạn trả:</span>
                              <span className={`font-medium ${isBorrowingOverdue(selectedBorrowing) ? 'text-red-600' : ''}`}>
                                {selectedBorrowing.dueDate ? formatDate(selectedBorrowing.dueDate) : '-'}
                                {isBorrowingOverdue(selectedBorrowing) && " (QUÁ HẠN)"}
                              </span>
                            </div>
                          </>
                        );
                      })()}
                    </div>
                  )}

                  <div className="form-field">
                    <Label className="form-label">Tình trạng sách khi trả</Label>
                    <Select 
                      value={returnForm.condition} 
                      onValueChange={(value) => setReturnForm(prev => ({ ...prev, condition: value }))}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="good">Tốt</SelectItem>
                        <SelectItem value="fair">Khá</SelectItem>
                        <SelectItem value="damaged">Hư hỏng nhẹ</SelectItem>
                        <SelectItem value="severely_damaged">Hư hỏng nặng</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <Button 
                    type="submit" 
                    className="w-full btn-success"
                    disabled={returnBookMutation.isPending}
                  >
                    {returnBookMutation.isPending ? "Đang xử lý..." : "Xác nhận trả sách"}
                  </Button>
                </form>
              </CardContent>
            </Card>

            {/* Active Borrowings Summary */}
            <Card>
              <CardHeader>
                <CardTitle>Sách đang mượn gần đây</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 max-h-96 overflow-y-auto">
                  {activeBooksForReturn?.slice(0, 10).map((borrowing: BorrowingWithDetails) => (
                    <div key={borrowing.id} className="border rounded-lg p-3">
                      <div className="font-medium text-sm">{borrowing.bookTitle}</div>
                      <div className="text-xs text-slate-600">{borrowing.readerName}</div>
                      <div className="flex justify-between items-center mt-2">
                        <span className="text-xs text-slate-500">
                          Hạn: {borrowing.dueDate ? formatDate(borrowing.dueDate) : '-'}
                        </span>
                        <Badge className={getStatusColor(isBorrowingOverdue(borrowing) ? "overdue" : "borrowed")}>
                          {isBorrowingOverdue(borrowing) ? "Quá hạn" : `Còn ${calculateDaysUntilDue(borrowing.dueDate)} ngày`}
                        </Badge>
                      </div>
                    </div>
                  )) || (
                    <div className="text-center text-slate-500 py-8">
                      Không có sách nào đang được mượn
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="overdue" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <AlertTriangle className="w-5 h-5 text-red-600" />
                <span>Sách quá hạn</span>
              </CardTitle>
            </CardHeader>
            <CardContent>
              {safeOverdueBorrowings.length > 0 ? (
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-slate-50 border-b border-slate-200">
                      <tr>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Sách</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Độc giả</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Ngày mượn</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Hạn trả</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Quá hạn</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Thao tác</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {safeOverdueBorrowings.map((borrowing: BorrowingWithDetails) => (
                        <tr key={borrowing.id} className="table-hover">
                          <td className="py-3 px-4">
                            <div className="font-medium">{borrowing.bookTitle}</div>
                            <div className="text-sm text-slate-500">{borrowing.bookAuthor}</div>
                          </td>
                          <td className="py-3 px-4">
                            <div>{borrowing.readerName}</div>
                            <div className="text-sm text-slate-500">{borrowing.readerEmail}</div>
                          </td>
                          <td className="py-3 px-4">{borrowing.borrowDate ? formatDate(borrowing.borrowDate) : '-'}</td>
                          <td className="py-3 px-4 text-red-600">{borrowing.dueDate ? formatDate(borrowing.dueDate) : '-'}</td>
                          <td className="py-3 px-4">
                            <Badge className="status-overdue">
                              {getOverdueDays(borrowing)} ngày
                            </Badge>
                          </td>
                          <td className="py-3 px-4">
                            {borrowing.returnDate ? (
                              <Button size="sm" className="btn-disabled" disabled>Đã trả</Button>
                            ) : (
                              <Button
                                size="sm"
                                className="btn-success"
                                onClick={() => {
                                  setReturnForm(prev => ({ ...prev, borrowingId: borrowing.id.toString() }));
                                }}
                              >
                                <Undo className="w-3 h-3 mr-1" />
                                Trả sách
                              </Button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="text-center py-12 text-slate-500">
                  <AlertTriangle className="w-12 h-12 mx-auto mb-4 text-green-500" />
                  <p>Tuyệt vời! Không có sách nào quá hạn</p>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="history" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Lịch sử mượn/trả</CardTitle>
            </CardHeader>
            <CardContent>
              {borrowingsLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 5 }).map((_, index) => (
                    <div key={index} className="flex space-x-4">
                      <Skeleton className="h-4 w-48" />
                      <Skeleton className="h-4 w-32" />
                      <Skeleton className="h-4 w-24" />
                      <Skeleton className="h-4 w-20" />
                    </div>
                  ))}
                </div>
              ) : borrowingsArray.length > 0 ? (
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-slate-50 border-b border-slate-200">
                      <tr>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Mã mượn</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Sách</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Độc giả</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Ngày mượn</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Hạn trả</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Ngày trả</th>
                        <th className="text-left py-3 px-4 font-medium text-slate-700">Trạng thái</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {borrowingsArray.map((borrowing: BorrowingWithDetails) => (
                        <tr key={borrowing.id} className="table-hover">
                          <td className="py-3 px-4">BR{borrowing.id.toString().padStart(3, '0')}</td>
                          <td className="py-3 px-4">
                            <div className="font-medium">{borrowing.bookTitle}</div>
                            <div className="text-sm text-slate-500">{borrowing.bookAuthor}</div>
                          </td>
                          <td className="py-3 px-4">
                            <div>{borrowing.readerName}</div>
                            <div className="text-sm text-slate-500">{borrowing.readerEmail}</div>
                          </td>
                          <td className="py-3 px-4">{borrowing.borrowDate ? formatDate(borrowing.borrowDate) : '-'}</td>
                          <td className="py-3 px-4">{borrowing.dueDate ? formatDate(borrowing.dueDate) : '-'}</td>
                          <td className="py-3 px-4">{borrowing.returnDate ? formatDate(borrowing.returnDate) : '-'}</td>
                          <td className="py-3 px-4">
                            <Badge className={getStatusColor(borrowing.status)}>
                              {getStatusText(borrowing.status)}
                            </Badge>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="text-center py-12 text-slate-500">
                  <History className="w-12 h-12 mx-auto mb-4" />
                  <p>Chưa có lịch sử mượn/trả nào</p>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
