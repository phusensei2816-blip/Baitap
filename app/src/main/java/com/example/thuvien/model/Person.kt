package com.example.thuvien.model

/** Lớp trừu tượng cho con người. */
abstract class Person(val id: String, name: String) {

    // Đóng gói: bên ngoài chỉ đọc, muốn đổi phải gọi rename()
    var name: String = name.trim()
        private set

    fun rename(newName: String): Boolean {
        if (newName.isBlank()) return false
        name = newName.trim()
        return true
    }

    abstract fun getRole(): String

    open fun getInfo(): String = "${getRole()}: $name"
}
