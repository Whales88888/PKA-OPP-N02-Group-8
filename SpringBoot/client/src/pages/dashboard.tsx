import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useLocation } from "wouter";
import { 
  Book, 
  Users, 
  ArrowRightLeft, 
  AlertTriangle,
  TrendingUp,
  Plus,
  Undo,
  BookOpen,
  UserPlus
} from "lucide-react";
import { DashboardStats, RecentActivities } from "../types";
import { apiService } from "../services/api";

export default function Dashboard() {
  const [, setLocation] = useLocation();
  const queryClient = useQueryClient();
  const [refreshKey, setRefreshKey] = useState(0);

  const { data: stats, error, isLoading, refetch: refetchStats } = useQuery({
    queryKey: ['stats', refreshKey],
    queryFn: () => apiService.getDashboardStats(),
    refetchOnWindowFocus: true,
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const { data: recentActivities, refetch: refetchActivities } = useQuery({
    queryKey: ["recent-activities", refreshKey],
    queryFn: () => apiService.getRecentActivities(),
    refetchOnWindowFocus: true,
    refetchOnMount: 'always',
    staleTime: 0,
  });

  // Manually refetch and update refreshKey when navigating to Dashboard
  const [location] = useLocation();
  useEffect(() => {
    if (location === "/" || location === "/dashboard") {
      if (localStorage.getItem('dashboardNeedsRefresh') === '1') {
        refetchStats();
        refetchActivities();
        localStorage.removeItem('dashboardNeedsRefresh');
      }
      setTimeout(() => {
        setRefreshKey((k) => k + 1);
      }, 200); // 200ms delay to ensure backend commit
    }
  }, [location]);

  // Always use an array for rendering recent activities
  const recentBorrowingsArray = Array.isArray(recentActivities?.recentBorrowings) ? recentActivities.recentBorrowings : [];

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

  const statsCards = [
    {
      title: "Tổng số Sách",
      value: stats?.totalBooks || 0,
      icon: <Book className="w-6 h-6 text-blue-600" />,
      bgColor: "bg-blue-100",
      change: "+25%",
      changeType: "positive" as const,
    },
    {
      title: "Độc giả hoạt động",
      value: stats?.activeReaders || 0,
      icon: <Users className="w-6 h-6 text-green-600" />,
      bgColor: "bg-green-100",
      change: "+25%",
      changeType: "positive" as const,
    },
    {
      title: "Sách đang mượn",
      value: stats?.currentBorrowings || 0,
      icon: <ArrowRightLeft className="w-6 h-6 text-yellow-600" />,
      bgColor: "bg-yellow-100",
      change: "-10%",
      changeType: "negative" as const,
    },
    {
      title: "Sách quá hạn",
      value: stats?.overdueBorrowings || 0,
      icon: <AlertTriangle className="w-6 h-6 text-red-600" />,
      bgColor: "bg-red-100",
      change: "Cần xử lý",
      changeType: "warning" as const,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statsCards.map((card, index) => (
          <Card key={index} className="hover:shadow-md transition-shadow duration-200">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-slate-600 text-sm font-medium">{card.title}</p>
                  <p className="text-3xl font-bold text-slate-800 mt-1">
                    {card.value.toLocaleString()}
                  </p>
                </div>
                <div className={`w-12 h-12 ${card.bgColor} rounded-lg flex items-center justify-center`}>
                  {card.icon}
                </div>
              </div>
              <div className="mt-4 flex items-center">
                <span className={`text-sm font-medium ${
                  card.changeType === "positive" ? "text-green-500" : 
                  card.changeType === "negative" ? "text-red-500" : 
                  "text-red-500"
                }`}>
                  {card.change}
                </span>
                {card.changeType !== "warning" && (
                  <span className="text-slate-600 text-sm ml-1">từ tháng trước</span>
                )}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Activities */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Hoạt động gần đây</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentBorrowingsArray.length > 0 ? (
                  recentBorrowingsArray.slice(0, 5).map((borrowing: any, index: number) => (
                    <div key={index} className="flex items-start space-x-4">
                      <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0">
                        <Book className="w-4 h-4 text-green-600" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm text-slate-800">
                          <span className="font-medium">{borrowing.readerName}</span>
                          {borrowing.returnDate ? " đã trả sách " : " đã mượn sách "}
                          <span className="font-medium">"{borrowing.bookTitle}"</span>
                        </p>
                        <p className="text-xs text-slate-500 mt-1">
                          {new Date(borrowing.createdAt).toLocaleString("vi-VN")}
                        </p>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center py-8 text-slate-500">
                    Chưa có hoạt động nào
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Quick Actions */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>Thao tác nhanh</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <Button 
                className="w-full btn-primary flex items-center justify-center space-x-2"
                onClick={() => setLocation("/borrowing?tab=borrow")}
              >
                <Plus className="w-4 h-4" />
                <span>Cho mượn sách</span>
              </Button>
              
              <Button 
                className="w-full btn-success flex items-center justify-center space-x-2"
                onClick={() => setLocation("/borrowing?tab=return")}
              >
                <Undo className="w-4 h-4" />
                <span>Trả sách</span>
              </Button>
              
              <Button 
                variant="secondary"
                className="w-full flex items-center justify-center space-x-2"
                onClick={() => setLocation("/books?action=add")}
              >
                <BookOpen className="w-4 h-4" />
                <span>Thêm sách mới</span>
              </Button>
              
              <Button 
                variant="outline"
                className="w-full flex items-center justify-center space-x-2"
                onClick={() => setLocation("/readers?action=add")}
              >
                <UserPlus className="w-4 h-4" />
                <span>Thêm độc giả</span>
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
