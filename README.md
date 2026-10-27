# Hệ thống Quản lý Thư viện (Jetpack Compose + Kotlin)

Bài tập về nhà tuần 4 (BT2): quản lý sách, sinh viên, cho phép sinh viên mượn/trả sách.

## 4 đặc trưng OOP

| Đặc trưng | Chỗ thể hiện |
|---|---|
| Trừu tượng | `abstract class LibraryItem`, `abstract class Person`, `interface Borrowable` |
| Kế thừa | `Book : LibraryItem`, `TextBook : Book`, `Novel : Book`, `Student : Person` |
| Đa hình | `getInfo()` / `getType()` được ghi đè ở `TextBook`, `Novel`; UI gọi trên kiểu `Book` |
| Đóng gói | `private var borrowed` (Book), `name` có `private set` + `rename()` (Person), `_borrowedBooks` private (Student), danh sách private (Library) |

## Cấu trúc

```
model/       Borrowable, LibraryItem, Book (TextBook, Novel), Person, Student, Library
viewmodel/   LibraryViewModel
navigation/  Screen (3 tab)
ui/          MainScreen, QuanLyScreen, DanhSachSachScreen, SinhVienScreen
```
