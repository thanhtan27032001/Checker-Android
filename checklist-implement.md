# CHECKLIST TRIỂN KHAI
## Hệ thống chấm công SaaS — com.gaden.checkin

Đánh dấu `[x]` khi hoàn thành. Cập nhật file này sau mỗi buổi code để dễ track tiến độ giữa các lần làm việc.

**Trạng thái tổng quan:** 🟢 Đã xong · 🟡 Đang làm · ⚪ Chưa bắt đầu

---

## Phase 0 — Setup nền tảng

- [x] 🟢 Tạo project Android Compose, package `com.gaden.checkin`
- [x] 🟢 Hiểu state hoisting, phân biệt Composable stateful/stateless
- [x] 🟢 Thiết kế Design System: màu (`Color.kt`), typography (`Type.kt`), spacing (`Dimens.kt`), theme tổng (`Theme.kt`)
- [x] 🟢 Mở rộng theme cho semantic color nghiệp vụ (`AttendanceColors.kt` — onTime/late/absent/onLeave)
- [ ] ⚪ Setup Version Catalog (`libs.versions.toml`) đầy đủ: Hilt, Retrofit, Room, Navigation, Coroutines
- [ ] ⚪ Setup package structure chuẩn: `domain/`, `data/`, `presentation/`, `di/`
- [ ] ⚪ Tải + tích hợp font Be Vietnam Pro vào `res/font/`
- [ ] ⚪ Setup Git repo, `.gitignore` chuẩn Android, branch strategy (main/develop)

## Phase 1 — Backend: Auth đa vai trò (Spring Boot)

- [ ] ⚪ Setup Spring Boot project, PostgreSQL, Flyway
- [ ] ⚪ Viết migration script theo ERD đã thiết kế (Company, Department, Employee, WorkSchedule, AttendanceRecord, LeaveRequest)
- [ ] ⚪ Endpoint `POST /auth/register-company` (onboarding công ty mới)
- [ ] ⚪ Endpoint `POST /auth/login` — JWT chứa `companyId` + `role` trong claims
- [ ] ⚪ Middleware/Interceptor kiểm tra tenant isolation (chặn truy cập chéo company)
- [ ] ⚪ Unit test cho auth flow + tenant isolation (test kỹ, đây là rủi ro bảo mật lớn nhất)

## Phase 2 — Mobile: Check-in flow (Button)

- [x] 🟢 Domain model + `CheckInStrategy` interface (Strategy Pattern, mở rộng được GPS/QR/Face)
- [x] 🟢 `AttendanceRepository` interface + `ButtonCheckInStrategy`
- [x] 🟢 `CheckInViewModel` với `UiState` sealed interface + `StateFlow`
- [x] 🟢 `CheckInScreen` Composable (tách stateful/stateless đúng chuẩn)
- [ ] ⚪ Setup Hilt DI: `@HiltAndroidApp`, `AppModule` cung cấp `AttendanceRepository`, `CheckInStrategy`
- [ ] ⚪ Viết `FakeAttendanceRepository` để xem UI trước khi có backend thật
- [ ] ⚪ Compose Preview cho `CheckInContent` (test nhanh UI không cần chạy app)
- [ ] ⚪ Nối `AttendanceRepositoryImpl` thật qua Retrofit khi Phase 1 backend xong
- [ ] ⚪ Xử lý lỗi: Snackbar khi check-in thất bại (network lỗi, đã check-in rồi...)
- [ ] ⚪ Offline support: lưu Room khi mất mạng lúc check-in, `WorkManager` đồng bộ lại
- [ ] ⚪ Unit test `CheckInViewModel` (mock `CheckInStrategy`, test đủ case Loading/Ready/Error)

## Phase 3 — Mobile: Lịch sử & báo cáo cá nhân (Staff)

- [ ] ⚪ Backend: endpoint lấy lịch sử chấm công theo tháng
- [ ] ⚪ Mobile: màn hình Calendar view (`LazyVerticalGrid`), highlight màu theo `AttendanceTheme.colors`
- [ ] ⚪ Chi tiết 1 ngày khi tap vào ô lịch
- [ ] ⚪ Navigation Compose: nối `CheckInScreen` ↔ `HistoryScreen`

## Phase 4 — Backend + Mobile: Đơn nghỉ phép

- [ ] ⚪ Backend: endpoint tạo đơn (`POST /leave-requests`), duyệt/từ chối (`PATCH /leave-requests/{id}`)
- [ ] ⚪ Mobile: form xin nghỉ (loại nghỉ, khoảng ngày, lý do)
- [ ] ⚪ Mobile: danh sách đơn đã gửi + trạng thái (badge màu theo `AttendanceTheme`)
- [ ] ⚪ Push notification (FCM) khi đơn được duyệt/từ chối

## Phase 5 — Dashboard Admin/Manager (Mobile hoặc Web riêng)

- [ ] ⚪ Backend: endpoint CRUD nhân viên, báo cáo tổng hợp công ty
- [ ] ⚪ UI: danh sách nhân viên (Paging 3), filter theo phòng ban
- [ ] ⚪ UI: dashboard real-time — ai đang check-in hôm nay
- [ ] ⚪ UI: duyệt đơn nghỉ phép (Manager/Admin)
- [ ] ⚪ Quyết định: làm Admin dashboard trong cùng app mobile (nested nav graph theo role) hay tách web riêng (Next.js)

## Phase 6 — Multi-tenant onboarding & gói dịch vụ

- [ ] ⚪ Flow đăng ký công ty mới (self-service)
- [ ] ⚪ API nội bộ (Super Admin only) đổi `subscriptionTier` — nâng cấp thủ công theo quyết định đã chốt
- [ ] ⚪ Giới hạn số nhân viên theo gói Free (validate ở backend, không chỉ ở UI)

## Phase 7 — Production polish

- [ ] ⚪ Testing: unit test Repository, đặc biệt logic offline-sync
- [ ] ⚪ Testing: UI test Compose cho luồng check-in chính
- [ ] ⚪ Crashlytics + Firebase Analytics
- [ ] ⚪ Security review: test kỹ tenant isolation bằng JWT của company khác
- [ ] ⚪ Proguard/R8 rules cho release build
- [ ] ⚪ CI/CD: GitHub Actions build + test (mobile + backend)

## Phase 8 — Go-live

- [ ] ⚪ Backend deploy Railway/Render
- [ ] ⚪ Keystore ký release build (lưu an toàn, backup)
- [ ] ⚪ Privacy Policy + Data Safety form (Play Console)
- [ ] ⚪ Store listing: mô tả, screenshot, feature graphic
- [ ] ⚪ Internal testing → Closed testing → Staged rollout

---

## Phase mở rộng (làm sau MVP)

- [ ] ⚪ GPS check-in: `FusedLocationProviderClient` + geofencing đơn giản
- [ ] ⚪ QR check-in: CameraX + ML Kit Barcode Scanning
- [ ] ⚪ Face check-in: ML Kit Face Detection
- [ ] ⚪ Tính năng quản lý quota phép năm
- [ ] ⚪ Tự động hóa thanh toán subscription

---

## Ghi chú làm việc

*Dùng phần này để ghi lại quyết định/vướng mắc giữa các buổi code, tránh quên context.*

-
