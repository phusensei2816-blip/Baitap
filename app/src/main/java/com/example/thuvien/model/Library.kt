package com.example.thuvien.model

/** Lớp quản lý chung, giấu 2 danh sách bên trong (đóng gói). */
class Library {
    private val books = mutableListOf<Book>()
    private val students = mutableListOf<Student>()

    fun addBook(book: Book) { books.add(book) }
    fun addStudent(student: Student) { students.add(student) }

    fun getBooks(): List<Book> = books.toList()
    fun getStudents(): List<Student> = students.toList()
    fun availableBooks(): List<Book> = books.filter { it.isAvailable() }

    fun borrowBook(student: Student, book: Book): Boolean = student.borrow(book)
    fun returnBook(student: Student, book: Book): Boolean = student.giveBack(book)
}
