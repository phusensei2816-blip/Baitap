package com.example.thuvien.model

/** Interface (trừu tượng): hợp đồng cho thứ có thể mượn. */
interface Borrowable {
    fun isAvailable(): Boolean
    fun borrow(): Boolean
    fun giveBack()
}
