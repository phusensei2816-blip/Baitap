package com.example.thuvien.model

/** Kế thừa Person. */
class Student(id: String, name: String) : Person(id, name) {

    // Đóng gói: danh sách thật là private, bên ngoài chỉ nhận bản sao
    private val _borrowedBooks = mutableListOf<Book>()
    val borrowedBooks: List<Book> get() = _borrowedBooks.toList()

    override fun getRole(): String = "Sinh viên"

    override fun getInfo(): String = "${super.getInfo()} ($id)"

    fun borrow(book: Book): Boolean {
        if (!book.borrow()) return false
        _borrowedBooks.add(book)
        return true
    }

    fun giveBack(book: Book): Boolean {
        if (!_borrowedBooks.remove(book)) return false
        book.giveBack()
        return true
    }
}
