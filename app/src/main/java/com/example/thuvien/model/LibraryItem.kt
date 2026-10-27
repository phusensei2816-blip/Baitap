package com.example.thuvien.model

/** Lớp trừu tượng: lớp cha chung cho mọi thứ thư viện quản lý. */
abstract class LibraryItem(val id: String, val title: String) {

    // Lớp con bắt buộc tự định nghĩa
    abstract fun getType(): String

    open fun getInfo(): String = "[$id] $title"
}
