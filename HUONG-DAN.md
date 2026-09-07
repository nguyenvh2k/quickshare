# Bada — bản thử nghiệm ô bật/tắt nhận Quick Share

## Tình trạng bàn giao

Đây là mã nguồn đã chỉnh sửa, **chưa có APK build thành công**.
Môi trường thực hiện đã cài JDK 17 và Android SDK 36, nhưng Gradle không tải
được plugin `com.android.application:8.7.3` từ các kho cấu hình. Vì vậy chưa
chạy được kiểm tra biên dịch, lint hoặc unit test. Chưa kiểm thử trên điện thoại.
Không coi khả năng gửi/nhận trên ROM cụ thể là đã được xác nhận.

Nền mã nguồn: Bada, commit `733abbc789a5389415efc0d0e211c6b560834126`.
Phiên bản custom: `20260830.01-tile.1`; debug package `dev.bluehouse.bada.debug`.
Android tối thiểu: 7.0/API 24. Giao thức gửi/nhận dùng triển khai của Bada,
không phụ thuộc Google Play Services cho giao thức.

## Phần đã chỉnh

- Ô Bada trên Quick Settings bật/tắt dịch vụ nhận.
- Khi bật từ ô, một activity ngắn khởi chạy dịch vụ foreground từ trạng thái
  hiển thị, rồi đóng. Chế độ luôn hiển thị được giữ trong phiên chạy.
- Khi tắt, dừng dịch vụ nhận và dọn kết nối qua lifecycle hiện có.
  Không tắt khi đang nhận nếu muốn giữ nguyên phiên truyền file.
- Ô theo dõi trạng thái chạy của dịch vụ, kể cả dịch vụ được bật bằng app.
- Khi máy khóa, phải mở khóa trước khi thay đổi qua ô.
- Nhấn giữ ô vẫn mở app qua intent đã có của Bada.
- Menu app có “Thêm ô cài đặt nhanh”; Android 13+ gọi hộp thoại thêm ô của
  hệ thống, bản cũ hướng dẫn thêm thủ công.
- Nhãn ô và hướng dẫn thêm ô có tiếng Việt; phần còn lại giữ ngôn ngữ upstream.
- Không cần root hoặc radio-helper cho luồng bật ô này. Tự bật Wi-Fi/Bluetooth.

**Lưu ý hành vi:** mở app chính vẫn khởi động bộ nhận ở chế độ mặc định của Bada
(hiển thị khi có thiết bị quét nếu chưa bật Always visible). Do đó mở app sau
khi tắt ô có thể bật lại dịch vụ. Trạng thái phiên không lưu qua process death
hoặc reboot; bật lại từ ô nếu ROM dừng app. Bản này chưa thay thế dịch vụ hệ thống.

## Build trên Windows

1. Cài Android Studio và JDK 17. Trong SDK Manager cài Android SDK Platform 36
   và Build Tools 35.0.0. Máy build cần Internet để lấy Gradle/Maven dependencies.
2. Giải nén, mở thư mục `bada-tile` bằng Android Studio. Để IDE tạo
   `local.properties` trỏ tới Android SDK, hoặc tự tạo, ví dụ:
   `sdk.dir=C:/Users/TEN_USER/AppData/Local/Android/Sdk`
3. Dùng JDK 17 cho Gradle/JAVA_HOME rồi chạy `BUILD-WINDOWS.cmd`.
   Hoặc trong terminal ở thư mục dự án:

   ```powershell
   .\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :service-android:testDebugUnitTest :core-protocol:test
   ```

4. APK khi build thành công nằm ở `app/build/outputs/apk/debug/app-debug.apk`.
   Đây là APK debug có chữ ký debug, dùng thử nội bộ. Giữ nguyên keystore debug
   trên máy build nếu muốn các bản sau cài đè; không dùng làm khóa phát hành.

## Build bằng GitHub Actions

Đưa thư mục dự án vào repository của bạn, giữ `.github/workflows`.
Trong Actions chọn **Build custom receive tile APK → Run workflow**.
Khi job thành công, tải artifact **Bada-Tile-debug-APK**, giải nén để lấy APK.
Workflow chỉ được chuẩn bị trong mã nguồn; chưa được chạy hoặc đăng lên GitHub.
Các runner mới có thể tạo khóa debug khác nhau, nên APK từ các lần chạy có thể
không cài đè được nhau. Muốn phát hành ổn định cần cấu hình khóa ký riêng được
lưu bền vững bằng secret của bạn.

## Cài và thêm ô sau khi đã có APK

1. Chép APK vào điện thoại, cho phép trình quản lý file cài ứng dụng từ nguồn này.
2. Mở Bada, cấp quyền thiết bị ở gần/Bluetooth/Wi-Fi và thông báo theo yêu cầu.
3. Bật Wi-Fi và Bluetooth. Thử hai máy cùng mạng Wi-Fi trước.
4. Menu góc trên → “Thêm ô cài đặt nhanh”. Hoặc kéo Quick Settings xuống → Sửa
   → kéo ô Bada vào bảng.
5. Bấm ô để bật/tắt. Máy bên kia dùng Quick Share, chọn tên thiết bị và đối chiếu
   PIN trước khi chấp nhận. Gửi từ máy này qua menu Chia sẻ → Bada/Send via Quick Share.
6. Trên ROM nội địa, cho phép app chạy nền/tự khởi động và điều chỉnh tiết kiệm pin
   nếu ROM dừng nhận. Tên mục tùy hãng; không cần bật gỡ lỗi không dây.

## Kiểm thử trên máy thật còn cần làm

- Bật/tắt ô khi app đã đóng; thiếu quyền; màn hình khóa; xoay màn hình.
- Nhấn giữ mở app; thêm ô trên Android 13+ và thêm thủ công trên bản cũ.
- Dừng dịch vụ: ô phải tắt và máy khác không bắt đầu phiên nhận mới được.
- Gửi/nhận hai chiều với Quick Share stock, cùng Wi-Fi rồi khác mạng qua Wi-Fi Direct.
- File nhỏ, file lớn, nhiều file; đối chiếu kích thước và SHA-256 file nhận.
- Từ chối PIN, hủy truyền, tắt Wi-Fi giữa chừng, ROM dừng tiến trình.
- Mở lại app sau khi tắt ô: xác nhận chế độ hiển thị theo quét của upstream.

README.md và docs/testing của upstream cung cấp thêm thông tin tương thích.
