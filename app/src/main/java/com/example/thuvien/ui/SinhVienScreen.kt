package com.example.thuvien.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thuvien.viewmodel.LibraryViewModel

@Composable
fun SinhVienScreen(vm: LibraryViewModel) {
    val students = vm.students
    val currentId = vm.currentStudent?.id
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Danh sách sinh viên",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(students, key = { it.id }) { s ->
            StudentRow(
                info = s.getInfo(),
                borrowedCount = s.borrowedBooks.size,
                selected = s.id == currentId,
                onClick = { vm.selectStudent(s) }
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Thêm sinh viên", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                name, { name = it }, label = { Text("Họ tên") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                code, { code = it }, label = { Text("Mã sinh viên") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { vm.addStudent(name, code); name = ""; code = "" },
                enabled = name.isNotBlank() && code.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) { Text("Thêm sinh viên") }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StudentRow(info: String, borrowedCount: Int, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Navy)
            )
            Column {
                Text(info, fontSize = 14.sp)
                Text("Đang mượn: $borrowedCount cuốn", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
