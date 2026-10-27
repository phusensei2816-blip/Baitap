package com.example.thuvien.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.thuvien.model.Book
import com.example.thuvien.model.Library
import com.example.thuvien.model.Novel
import com.example.thuvien.model.Student
import com.example.thuvien.model.TextBook

class LibraryViewModel : ViewModel() {

    private val library = Library()

    // Mỗi lần dữ liệu đổi thì tăng version để Compose vẽ lại
    private var version by mutableIntStateOf(0)

    private fun <T> read(block: () -> T): T {
        version // đọc state để Compose biết nơi gọi phụ thuộc vào dữ liệu
        return block()
    }

    private fun notifyChanged() {
        version++
    }

    var currentStudent by mutableStateOf<Student?>(null)
        private set

    val books: List<Book> get() = read { library.getBooks() }
    val students: List<Student> get() = read { library.getStudents() }
    val availableBooks: List<Book> get() = read { library.availableBooks() }
    val borrowedBooks: List<Book> get() = read { currentStudent?.borrowedBooks ?: emptyList() }

    init {
        val a = Student("SV001", "Nguyen Van A")
        val b = Student("SV002", "Tran Thi B")
        library.addStudent(a)
        library.addStudent(b)

        val b1 = TextBook("B01", "Sách 01", "Tác giả A", "Lập trình")
        val b2 = Novel("B02", "Sách 02", "Tác giả B", "Trinh thám")
        val b3 = TextBook("B03", "Sách 03", "Tác giả C", "Cơ sở dữ liệu")
        val b4 = Novel("B04", "Sách 04", "Tác giả D", "Khoa học viễn tưởng")
        listOf(b1, b2, b3, b4).forEach { library.addBook(it) }

        library.borrowBook(a, b1)
        library.borrowBook(a, b2)
        currentStudent = a
    }

    fun selectStudent(student: Student) {
        currentStudent = student
        notifyChanged()
    }

    fun renameCurrentStudent(newName: String) {
        if (currentStudent?.rename(newName) == true) notifyChanged()
    }

    fun addStudent(name: String, code: String) {
        if (name.isBlank() || code.isBlank()) return
        val s = Student(code.trim(), name)
        library.addStudent(s)
        if (currentStudent == null) currentStudent = s
        notifyChanged()
    }

    fun addBook(title: String, author: String, extra: String, isTextBook: Boolean) {
        val id = "B%02d".format(library.getBooks().size + 1)
        // Biến kiểu Book nhưng giữ đối tượng con: đa hình
        val book: Book = if (isTextBook) {
            TextBook(id, title.trim(), author.trim(), extra.trim())
        } else {
            Novel(id, title.trim(), author.trim(), extra.trim())
        }
        library.addBook(book)
        notifyChanged()
    }

    fun borrow(book: Book) {
        val s = currentStudent ?: return
        if (library.borrowBook(s, book)) notifyChanged()
    }

    fun giveBack(book: Book) {
        val s = currentStudent ?: return
        if (library.returnBook(s, book)) notifyChanged()
    }
}
