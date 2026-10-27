package com.example.thuvien.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thuvien.viewmodel.LibraryViewModel

@Composable
fun QuanLyScreen(vm: LibraryViewModel) {
    val student = vm.currentStudent
    val borrowed = vm.borrowedBooks
    val available = vm.availableBooks
    var showDialog by remember { mutableStateOf(false) }
    var nameText by remember(student?.id) { mutableStateOf(student?.name ?: "") }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Hệ thống\nQuản lý Thư viện",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        if (student == null) {
            Text("Chưa có sinh viên nào. Hãy thêm ở tab Sinh viên.")
        } else {
            Text("Sinh viên", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { vm.renameCurrentStudent(nameText) },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy)
                ) { Text("Thay đổi") }
            }

            Spacer(Modifier.height(20.dp))
            Text("Danh sách sách", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrayBox)
                    .padding(12.dp)
            ) {
                if (borrowed.isEmpty()) {
                    Text("Chưa mượn sách nào", color = Color.DarkGray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(borrowed, key = { it.id }) { book ->
                            BorrowedRow(
                                title = book.title,
                                info = book.getInfo(), // đa hình: mỗi loại sách trả về nội dung khác nhau
                                onReturn = { vm.giveBack(book) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.6f).height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) { Text("Thêm") }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chọn sách để mượn") },
            text = {
                if (available.isEmpty()) {
                    Text("Không còn sách trống")
                } else {
                    LazyColumn(Modifier.heightIn(max = 240.dp)) {
                        items(available, key = { it.id }) { book ->
                            TextButton(onClick = { vm.borrow(book); showDialog = false }) {
                                Text(book.getInfo())
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Đóng") } }
        )
    }
}

@Composable
private fun BorrowedRow(title: String, info: String, onReturn: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = true,
            onCheckedChange = { onReturn() }, // bỏ tick = trả sách
            colors = CheckboxDefaults.colors(checkedColor = Maroon)
        )
        Column {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(info, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
