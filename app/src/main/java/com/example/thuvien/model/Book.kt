package com.example.thuvien.model

/** Kế thừa LibraryItem, thực hiện interface Borrowable. */
open class Book(
    id: String,
    title: String,
    val author: String
) : LibraryItem(id, title), Borrowable {

    // Đóng gói: chỉ đổi được qua borrow() / giveBack()
    private var borrowed = false

    override fun isAvailable(): Boolean = !borrowed

    override fun borrow(): Boolean {
        if (borrowed) return false
        borrowed = true
        return true
    }

    override fun giveBack() {
        borrowed = false
    }

    override fun getType(): String = "Sách"

    override fun getInfo(): String = "${super.getInfo()} - Tác giả: $author"
}

/** Kế thừa Book, ghi đè getType() và getInfo(): đa hình. */
class TextBook(
    id: String,
    title: String,
    author: String,
    val subject: String
) : Book(id, title, author) {

    override fun getType(): String = "Giáo trình"

    override fun getInfo(): String = "${super.getInfo()} - Môn: $subject"
}

class Novel(
    id: String,
    title: String,
    author: String,
    val genre: String
) : Book(id, title, author) {

    override fun getType(): String = "Tiểu thuyết"

    override fun getInfo(): String = "${super.getInfo()} - Thể loại: $genre"
}
