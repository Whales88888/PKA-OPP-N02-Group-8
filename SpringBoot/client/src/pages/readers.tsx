import { useState, useEffect } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { useToast } from "@/hooks/use-toast";
import { queryClient } from "@/lib/queryClient";
import { apiRequest } from "@/lib/queryClient";
import ReaderModal from "@/components/modals/reader-modal";
import { getInitials, formatDate } from "@/lib/utils";
import { UserPlus, Search, Edit, Trash2, Eye, Book } from "lucide-react";
import type { Reader } from "@shared/schema";
import { API_BASE } from "@/config/api";

// Map backend fields to frontend camelCase
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

export default function Readers() {
  const [searchQuery, setSearchQuery] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedReader, setSelectedReader] = useState<Reader | null>(null);
  const { toast } = useToast();

  const { data: readers, error, isLoading } = useQuery({
    queryKey: ['readers'],
    queryFn: () =>
      fetch(`${API_BASE}/api/readers`).then(r => {
        if (!r.ok) throw new Error('Failed to load readers');
        return r.json();
      }).catch(console.error)
  });

  const addReaderMutation = useMutation({
    mutationFn: async (reader: Partial<Reader>) => {
      return fetch(`${API_BASE}/api/readers`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reader),
      })
        .then(res => res.json())
        .catch(console.error);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["readers"] });
      setIsModalOpen(false);
      toast({ title: "Success", description: "Reader added successfully" });
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to add reader", variant: "destructive" });
    },
  });

  const editReaderMutation = useMutation({
    mutationFn: async (reader: Reader) => {
      return fetch(`${API_BASE}/api/readers/${reader.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reader),
      })
        .then(res => res.json())
        .catch(console.error);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["readers"] });
      setIsModalOpen(false);
      toast({ title: "Success", description: "Reader updated successfully" });
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to update reader", variant: "destructive" });
    },
  });

  const deleteReaderMutation = useMutation({
    mutationFn: async (id: number) => {
      return fetch(`${API_BASE}/api/readers/${id}`, { method: "DELETE" })
        .then(res => res.json())
        .catch(console.error);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["readers"] });
      toast({ title: "Success", description: "Reader deleted successfully" });
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to delete reader", variant: "destructive" });
    },
  });

  const handleEdit = (reader: Reader) => {
    setSelectedReader(reader);
    setIsModalOpen(true);
  };

  const handleDelete = async (reader: Reader) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa độc giả "${reader.name}"?`)) {
      deleteReaderMutation.mutate(reader.id);
    }
  };

  const handleAddNew = () => {
    setSelectedReader(null);
    setIsModalOpen(true);
  };

  const handleViewBorrowedBooks = async (readerId: number) => {
    // This would typically open a modal or navigate to a detailed view
    toast({
      title: "Đang phát triển",
      description: "Chức năng xem sách đang mượn đang được phát triển",
    });
  };

  // Generate a random color for each reader avatar
  const getAvatarColor = (id: number) => {
    const colors = [
      "bg-blue-500",
      "bg-green-500", 
      "bg-purple-500",
      "bg-pink-500",
      "bg-indigo-500",
      "bg-yellow-500",
      "bg-red-500",
      "bg-orange-500"
    ];
    return colors[id % colors.length];
  };

  // Always use an array for rendering, whether API returns array or Page object
  const readersArray = (Array.isArray(readers) ? readers : readers?.content || []).map(mapReaderApi);
  const safeReadersArray = Array.isArray(readersArray) ? readersArray : [];

  if (isLoading) {
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
  if (error) return <div className="p-4 bg-red-100 text-red-700 rounded mb-4">Lỗi: {error.message}</div>;

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
          <CardTitle>Quản lý Độc giả</CardTitle>
          <Button onClick={handleAddNew} className="btn-primary">
            <UserPlus className="w-4 h-4 mr-2" />
            Thêm độc giả
          </Button>
        </CardHeader>
        
        {/* Search */}
        <div className="px-6 py-4 border-b border-slate-200 bg-slate-50">
          <div className="flex space-x-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
              <Input
                type="text"
                placeholder="Tìm kiếm theo tên, email..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Button variant="secondary">
              Tìm kiếm
            </Button>
          </div>
        </div>

        {/* Readers Grid */}
        <CardContent className="p-6">
          {safeReadersArray.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {safeReadersArray.map((reader: Reader) => (
                <Card key={reader.id} className="border border-slate-200 hover:shadow-md transition-shadow duration-200">
                  <CardContent className="p-6">
                    <div className="flex items-start space-x-4">
                      <div className={`w-12 h-12 ${getAvatarColor(reader.id)} rounded-full flex items-center justify-center text-white font-semibold`}>
                        <span>{getInitials(reader.name)}</span>
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="font-semibold text-slate-800 truncate">{reader.name}</h4>
                        <p className="text-sm text-slate-600">ID: RD{reader.id.toString().padStart(3, '0')}</p>
                        <p className="text-sm text-slate-600 truncate">{reader.email}</p>
                        {reader.phone && (
                          <p className="text-sm text-slate-600">{reader.phone}</p>
                        )}
                        <div className="mt-3 flex items-center space-x-4 text-xs text-slate-500">
                          <Badge variant={reader.isActive ? "default" : "secondary"}>
                            {reader.isActive ? "Hoạt động" : "Không hoạt động"}
                          </Badge>
                          <span>Tham gia: {formatDate(reader.registrationDate || "")}</span>
                        </div>
                      </div>
                    </div>
                    <div className="mt-4 flex space-x-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        className="flex-1 text-xs"
                        onClick={() => handleViewBorrowedBooks(reader.id)}
                      >
                        <Book className="w-3 h-3 mr-1" />
                        Xem sách đang mượn
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleEdit(reader)}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(reader)}
                        className="text-red-600 hover:text-red-800"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-slate-500">
              {searchQuery ? (
                <div>
                  <p>Không tìm thấy độc giả nào phù hợp với tiêu chí tìm kiếm</p>
                  <Button 
                    variant="link" 
                    onClick={() => setSearchQuery("")}
                  >
                    Xóa bộ lọc
                  </Button>
                </div>
              ) : (
                <div>
                  <p>Chưa có độc giả nào đăng ký</p>
                  <Button 
                    variant="link" 
                    onClick={handleAddNew}
                  >
                    Thêm độc giả đầu tiên
                  </Button>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      <ReaderModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedReader(null);
        }}
        reader={selectedReader}
        onSuccess={() => {
          queryClient.invalidateQueries({ queryKey: ["readers"] });
        }}
      />
    </div>
  );
}
