package com.example.stilldan

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class Student(
    val name: String,
    var email: String?,
    val sdt: String?
)

@Preview(showBackground = false)
@Composable
fun Student(modifier: Modifier= Modifier){
    val students = listOf(
        Student("Nguyễn Văn An", "an@sv.edu.vn", "0901 234 567"),
        Student("Trần Thị Bình", null, "0912 888 999"),
        Student("Lê Minh Chi", "chi@sv.edu.vn", null),
        Student("Phạm Quốc Dũng", null, null),
        Student("Võ Thanh Em", "em@sv.edu.vn", "0933 111 222")
    )
    var emailMat = 0
    for (student in students){
        if (student.email==null){
            emailMat = emailMat+1
        }
    }
    Box(modifier= Modifier.fillMaxSize().background(color = Color(0xFFf3f2f7))){
        Column(modifier= Modifier.fillMaxSize()) {
            Text(text="Danh sách sinh viên",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier= Modifier.fillMaxWidth().background(color = Color(0xFF2e4476)).padding(start = 16.dp,
                    end = 16.dp,top=30.dp,bottom=8.dp)
                )
            for(student in students){
                Cardstudent(Getstudents=student)
            }
            Row(
                    modifier = Modifier.padding(horizontal = 12.dp).height(50.dp).fillMaxWidth().background(Color(0xFF5CBEFF),
                        RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text="Tổng : 5 sinh viên")
                    Row() {
                        Text(text="Thiếu email: ")
                        Text(text= emailMat.toString(), color = Color.Red)
                    }
            }
        }
    }
}
@Composable
fun Cardstudent(modifier: Modifier= Modifier,Getstudents: Student){
        Card(
            modifier = Modifier
                        .fillMaxWidth().height(100.dp).padding(vertical = 4.dp, horizontal = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors= CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
            ) {
            Column(modifier= Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                Text(Getstudents.name,
                    color = Color(0xFF141414),
                    fontWeight = FontWeight.Bold)
                Text(text="Email: "+ (Getstudents.email ?:"Chưa cập nhật"),
                    color = Color.Gray)
                Text(text="SĐT: "+ (Getstudents.sdt ?:"Chưa cập nhật"),
                    color = Color.Gray)
            }
    }
}
