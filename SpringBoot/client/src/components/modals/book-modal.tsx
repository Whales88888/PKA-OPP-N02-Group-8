import { useState, useEffect } from "react";
import { useMutation } from "@tanstack/react-query";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";
import { apiRequest } from "@/lib/queryClient";
import type { Book } from "../../types";
import type { InsertBook } from "../../types";

interface BookModalProps {
  isOpen: boolean;
  onClose: () => void;
  book?: Book | null;
  onSuccess: () => void;
}

export default function BookModal({ isOpen, onClose, book, onSuccess }: BookModalProps) {
  const [formData, setFormData] = useState<InsertBook>({
    title: "",
    author: "",
    isbn: "",
    category: "",
    publishYear: new Date().getFullYear(),
    publisher: "",
    description: "",
    quantity: 1,
  });

  const { toast } = useToast();

  useEffect(() => {
    if (book) {
      setFormData({
        title: book.title,
        author: book.author,
        isbn: book.isbn || "",
        category: book.category,
        publishYear: book.publishYear || new Date().getFullYear(),
        publisher: book.publisher || "",
        description: book.description || "",
        quantity: book.quantity,
      });
    } else {
      setFormData({
        title: "",
        author: "",
        isbn: "",
        category: "",
        publishYear: new Date().getFullYear(),
        publisher: "",
        description: "",
        quantity: 1,
      });
    }
  }, [book, isOpen]);

  const mutation = useMutation({
    mutationFn: async (data: InsertBook) => {
      if (book) {
        return await apiRequest("PUT", `/api/books/${book.id}`, data);
      } else {
        return await apiRequest("POST", "/api/books", data);
      }
    },
    onSuccess: () => {
      onSuccess();
      onClose();
      toast({
        title: "Thành công",
        description: book ? "Đã cập nhật sách thành công" : "Đã thêm sách mới thành công",
      });
    },
    onError: (error: any) => {
      toast({
        title: "Lỗi",
        description: error?.message || "Không thể lưu thông tin sách",
        variant: "destructive",
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title || !formData.author || !formData.category) {
      toast({
        title: "Lỗi",
        description: "Vui lòng điền đầy đủ thông tin bắt buộc",
        variant: "destructive",
      });
      return;
    }

    mutation.mutate(formData);
  };

  const handleInputChange = (field: keyof InsertBook, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const categories = [
    { value: "technology", label: "Công nghệ" },
    { value: "literature", label: "Văn học" },
    { value: "science", label: "Khoa học" },
    { value: "history", label: "Lịch sử" },
    { value: "business", label: "Kinh doanh" },
  ];

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {book ? "Chỉnh sửa sách" : "Thêm sách mới"}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="form-field">
              <Label className="form-label">Tên sách *</Label>
              <Input
                value={formData.title}
                onChange={(e) => handleInputChange("title", e.target.value)}
                placeholder="Nhập tên sách..."
                className="form-input"
                required
              />
            </div>
            <div className="form-field">
              <Label className="form-label">Tác giả *</Label>
              <Input
                value={formData.author}
                onChange={(e) => handleInputChange("author", e.target.value)}
                placeholder="Nhập tên tác giả..."
                className="form-input"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="form-field">
              <Label className="form-label">ISBN</Label>
              <Input
                value={formData.isbn}
                onChange={(e) => handleInputChange("isbn", e.target.value)}
                placeholder="Nhập mã ISBN..."
                className="form-input"
              />
            </div>
            <div className="form-field">
              <Label className="form-label">Thể loại *</Label>
              <Select 
                value={formData.category} 
                onValueChange={(value) => handleInputChange("category", value)}
              >
                <SelectTrigger className="form-select">
                  <SelectValue placeholder="Chọn thể loại" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((category) => (
                    <SelectItem key={category.value} value={category.value}>
                      {category.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="form-field">
              <Label className="form-label">Năm xuất bản</Label>
              <Input
                type="number"
                value={formData.publishYear || ""}
                onChange={(e) => handleInputChange("publishYear", parseInt(e.target.value) || undefined)}
                placeholder="Năm xuất bản..."
                min="1000"
                max={new Date().getFullYear() + 10}
                className="form-input"
              />
            </div>
            <div className="form-field">
              <Label className="form-label">Số lượng *</Label>
              <Input
                type="number"
                value={formData.quantity}
                onChange={(e) => handleInputChange("quantity", parseInt(e.target.value) || 1)}
                placeholder="Số lượng sách..."
                min="1"
                className="form-input"
                required
              />
            </div>
          </div>

          <div className="form-field">
            <Label className="form-label">Nhà xuất bản</Label>
            <Input
              value={formData.publisher}
              onChange={(e) => handleInputChange("publisher", e.target.value)}
              placeholder="Nhập tên nhà xuất bản..."
              className="form-input"
            />
          </div>

          <div className="form-field">
            <Label className="form-label">Mô tả</Label>
            <Textarea
              value={formData.description}
              onChange={(e) => handleInputChange("description", e.target.value)}
              placeholder="Nhập mô tả về sách..."
              rows={3}
              className="form-textarea"
            />
          </div>

          <div className="flex space-x-4 pt-4">
            <Button
              type="submit"
              className="flex-1 btn-primary"
              disabled={mutation.isPending}
            >
              {mutation.isPending ? "Đang lưu..." : (book ? "Cập nhật sách" : "Thêm sách")}
            </Button>
            <Button
              type="button"
              variant="outline"
              className="flex-1"
              onClick={onClose}
              disabled={mutation.isPending}
            >
              Hủy
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
