# UTH Auth – Firebase Authentication + Jetpack Compose

## Cấu hình Firebase (bắt buộc)
1. Firebase Console → tạo project → thêm app Android, package `com.uth.uthauth`.
2. Thêm SHA-1 (và SHA-256) của debug keystore: `./gradlew signingReport`.
3. Authentication → Sign-in method → bật **Google** và **Anonymous**.
4. Tải `google-services.json` → đặt vào `app/google-services.json`.
5. Sync Gradle → Run (cần emulator/thiết bị có Google Play Services + tài khoản Google).

## Cấu trúc
- `data/auth`  : AuthRepository, FirebaseAuthRepository, GoogleCredentialProvider, AuthErrorMapper
- `data/task`  : TaskRepository + InMemoryTaskRepository (dữ liệu mẫu)
- `domain/model`: AuthUser, TodoTask, TaskSummary, DayStat
- `di`         : AppContainer (manual DI)
- `ui/auth`    : LoginScreen, LoginViewModel, LoginUiState/Event, LoginComponents
- `ui/home`    : HomeScreen, HomeViewModel, components/
- `ui/profile` : ProfileScreen, ProfileViewModel, components/
- `ui/navigation`, `ui/theme`, `ui/components`

## Cloud Firestore
- Firebase Console → Build → Firestore Database → Create database (production mode).
- Tab **Rules** → dán nội dung file `firestore.rules` → Publish.
- Dữ liệu lưu tại `users/{uid}/tasks/{taskId}`; mỗi tài khoản có dữ liệu riêng, đồng bộ realtime.

## Các dịch vụ Firebase đã tích hợp
| Dịch vụ | Vị trí trong code |
|---|---|
| Authentication | `data/auth/` |
| Cloud Firestore | `data/task/FirestoreTaskRepository.kt` |
| Realtime Database | `data/presence/UserPresenceRepository.kt` |
| Analytics + Crashlytics | `data/telemetry/AppTelemetry.kt` |
| Cloud Messaging | `push/` |
| Remote Config | `data/config/RemoteConfigRepository.kt` (keys: `welcome_message`, `show_ads`) |
| AdMob (ID thử nghiệm) | `ui/components/AdBanner.kt` |
| Hosting | thư mục `hosting/` + `firebase.json` |
| Test Lab | chạy trên Console bằng APK debug |


## Tuần 04 – Flow Trang chủ (UI + API)
- API: `GET {API_BASE_URL}mock/teach/tasks` (header `Authorization: Bearer <API_TOKEN>`), trả về mảng JSON
  `id, title, note, category (study|life), date, time, priority, remind, done, subtasks`.
- Cấu hình trong `local.properties` (xem `local.properties.example`).
- Lớp liên quan: `data/remote/` (Retrofit), `data/task/ApiTaskRepository.kt`, `domain/model/LoadState.kt`, `ui/home/`.
- Trạng thái UI: Loading (skeleton) -> Success (danh sách + thống kê) / Error (200 vs 500, mất mạng, 401) + nút Thử lại + kéo để làm mới.

## Nâng cao 01 – Xin quyền + đa ngôn ngữ
- Luồng xin quyền lần đầu: Vị trí -> Thông báo -> Máy ảnh (`ui/permissions/`), có nút "Để sau".
- Đổi ngôn ngữ VI/EN (`ui/components/LanguageToggle.kt`, `util/LanguageManager.kt`), chuỗi ở `res/values` (VI) và `res/values-en` (EN).

## Tuần 04 – Flow Danh sách & Chi tiết (UI + API)
| Màn hình | API | Thư mục |
|---|---|---|
| Danh sách (tìm kiếm, lọc Tất cả/Học tập/Cuộc sống, nhóm CẦN LÀM / ĐÃ XONG) | `GET /mock/teach/tasks` khi mở màn hình; `PATCH /tasks/{id}` khi tick | `ui/list/` |
| Chi tiết (hạn chót, nhắc nhở, ghi chú, việc nhỏ, hoàn thành, xóa) | `GET /tasks/{id}` khi bấm một việc; `PATCH /tasks/{id}` khi sửa/tick/thêm việc nhỏ; `DELETE /tasks/{id}` | `ui/detail/` |

- Tầng dữ liệu: `data/remote/TaskApi.kt`, `data/task/ApiTaskRepository.kt` (cập nhật lạc quan, lỗi thì hoàn tác).
- API mock luôn trả mẫu cố định nên repository giữ lớp phủ thay đổi trong phiên chạy (xem chú thích trong code).
