package com.example.thuvien.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thuvien.viewmodel.LibraryViewModel

@Composable
fun DanhSachSachScreen(vm: LibraryViewModel) {
    val books = vm.books
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var extra by remember { mutableStateOf("") }
    var isTextBook by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Danh sách sách",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(books, key = { it.id }) { book ->
            BookCard(
                type = book.getType(),
                info = book.getInfo(), // đa hình
                available = book.isAvailable()
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Thêm sách mới", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                title, { title = it }, label = { Text("Tên sách") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                author, { author = it }, label = { Text("Tác giả") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isTextBook, onClick = { isTextBook = true }, label = { Text("Giáo trình") })
                FilterChip(selected = !isTextBook, onClick = { isTextBook = false }, label = { Text("Tiểu thuyết") })
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                extra, { extra = it },
                label = { Text(if (isTextBook) "Môn học" else "Thể loại") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    vm.addBook(title, author, extra, isTextBook)
                    title = ""; author = ""; extra = ""
                },
                enabled = title.isNotBlank() && author.isNotBlank() && extra.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) { Text("Thêm sách") }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BookCard(type: String, info: String, available: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(type, fontSize = 11.sp, color = Navy, fontWeight = FontWeight.Bold)
            Text(info, fontSize = 14.sp)
            Text(
                if (available) "Còn sách" else "Đã được mượn",
                fontSize = 12.sp,
                color = if (available) Color(0xFF2E7D32) else Maroon
            )
        }
    }
}
