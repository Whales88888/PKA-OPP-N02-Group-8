import { useState } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { useToast } from "@/hooks/use-toast";
import { queryClient } from "@/lib/queryClient";
import { apiRequest } from "@/lib/queryClient";
import BookModal from "@/components/modals/book-modal";
import { getCategoryColor, getCategoryText, getStatusColor, getStatusText } from "@/lib/utils";
import { Plus, Search, Edit, Trash2, Eye } from "lucide-react";
import type { Book } from "@shared/schema";

export default function Books() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string>("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const { toast } = useToast();

  const { data: books, isLoading } = useQuery<Book[]>({
    queryKey: ["/api/books", { search: searchQuery, category: selectedCategory === "all" ? "" : selectedCategory }],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (searchQuery) params.append("search", searchQuery);
      if (selectedCategory && selectedCategory !== "all") params.append("category", selectedCategory);
      
      const response = await fetch(`/api/books?${params.toString()}`);
      if (!response.ok) throw new Error("Failed to fetch books");
      return response.json();
    },
  });

  const deleteBookMutation = useMutation({
    mutationFn: async (id: number) => {
      await apiRequest("DELETE", `/api/books/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/books"] });
      toast({
        title: "Thành công",
        description: "Đã xóa sách thành công",
      });
    },
    onError: () => {
      toast({
        title: "Lỗi",
        description: "Không thể xóa sách",
        variant: "destructive",
      });
    },
  });

  const handleEdit = (book: Book) => {
    setSelectedBook(book);
    setIsModalOpen(true);
  };

  const handleDelete = async (book: Book) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa sách "${book.title}"?`)) {
      deleteBookMutation.mutate(book.id);
    }
  };

  const handleAddNew = () => {
    setSelectedBook(null);
    setIsModalOpen(true);
  };

  const categories = [
    { value: "technology", label: "Công nghệ" },
    { value: "literature", label: "Văn học" },
    { value: "science", label: "Khoa học" },
    { value: "history", label: "Lịch sử" },
    { value: "business", label: "Kinh doanh" },
  ];

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
          <CardTitle>Quản lý Sách</CardTitle>
          <Button onClick={handleAddNew} className="btn-primary">
            <Plus className="w-4 h-4 mr-2" />
            Thêm sách mới
          </Button>
        </CardHeader>
        
        {/* Search and Filter */}
        <div className="px-6 py-4 border-b border-slate-200 bg-slate-50">
          <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
              <Input
                type="text"
                placeholder="Tìm kiếm theo tên sách, tác giả..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <div className="sm:w-48">
              <Select value={selectedCategory} onValueChange={setSelectedCategory}>
                <SelectTrigger>
                  <SelectValue placeholder="Tất cả thể loại" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tất cả thể loại</SelectItem>
                  {categories.map((category) => (
                    <SelectItem key={category.value} value={category.value}>
                      {category.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <Button variant="secondary">
              Tìm kiếm
            </Button>
          </div>
        </div>

        {/* Books Table */}
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Mã sách</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Tên sách</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Tác giả</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Thể loại</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Trạng thái</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Số lượng</th>
                  <th className="text-left py-4 px-6 font-medium text-slate-700">Thao tác</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {isLoading ? (
                  Array.from({ length: 5 }).map((_, index) => (
                    <tr key={index}>
                      {Array.from({ length: 7 }).map((_, colIndex) => (
                        <td key={colIndex} className="py-4 px-6">
                          <Skeleton className="h-4 w-full" />
                        </td>
                      ))}
                    </tr>
                  ))
                ) : books && books.length > 0 ? (
                  books.map((book) => (
                    <tr key={book.id} className="table-hover">
                      <td className="py-4 px-6 text-slate-800">BK{book.id.toString().padStart(3, '0')}</td>
                      <td className="py-4 px-6">
                        <div className="font-medium text-slate-800">{book.title}</div>
                        {book.isbn && (
                          <div className="text-sm text-slate-500">ISBN: {book.isbn}</div>
                        )}
                      </td>
                      <td className="py-4 px-6 text-slate-700">{book.author}</td>
                      <td className="py-4 px-6">
                        <Badge className={getCategoryColor(book.category)}>
                          {getCategoryText(book.category)}
                        </Badge>
                      </td>
                      <td className="py-4 px-6">
                        <Badge className={getStatusColor(book.availableQuantity > 0 ? "available" : "borrowed")}>
                          {getStatusText(book.availableQuantity > 0 ? "available" : "borrowed")}
                        </Badge>
                      </td>
                      <td className="py-4 px-6">
                        <div className="text-sm">
                          <div>Tổng: {book.quantity}</div>
                          <div className="text-slate-500">Còn: {book.availableQuantity}</div>
                        </div>
                      </td>
                      <td className="py-4 px-6">
                        <div className="flex space-x-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleEdit(book)}
                            className="text-blue-600 hover:text-blue-800"
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleDelete(book)}
                            className="text-red-600 hover:text-red-800"
                          >
                            <Trash2 className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-green-600 hover:text-green-800"
                          >
                            <Eye className="w-4 h-4" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={7} className="py-12 text-center text-slate-500">
                      {searchQuery || selectedCategory ? (
                        <div>
                          <p>Không tìm thấy sách nào phù hợp với tiêu chí tìm kiếm</p>
                          <Button 
                            variant="link" 
                            onClick={() => {
                              setSearchQuery("");
                              setSelectedCategory("all");
                            }}
                          >
                            Xóa bộ lọc
                          </Button>
                        </div>
                      ) : (
                        <div>
                          <p>Chưa có sách nào trong thư viện</p>
                          <Button 
                            variant="link" 
                            onClick={handleAddNew}
                          >
                            Thêm sách đầu tiên
                          </Button>
                        </div>
                      )}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {books && books.length > 0 && (
            <div className="p-6 border-t border-slate-200 flex items-center justify-between">
              <div className="text-sm text-slate-600">
                Hiển thị {books.length} kết quả
              </div>
              <div className="flex space-x-2">
                <Button variant="outline" size="sm" disabled>
                  Trước
                </Button>
                <Button size="sm" className="btn-primary">
                  1
                </Button>
                <Button variant="outline" size="sm" disabled>
                  Tiếp
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <BookModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedBook(null);
        }}
        book={selectedBook}
        onSuccess={() => {
          queryClient.invalidateQueries({ queryKey: ["/api/books"] });
        }}
      />
    </div>
  );
}
