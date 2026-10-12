package com.example.bangdiemlop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Student(
    val name: String,
    val diem: Double?
)
data class Xeploai(
    val kq: String,
    val mau: Color
)
val students = listOf(
    Student("Nguyễn Văn An", 9.0),
    Student("Trần Thị Bình", 7.2),
    Student("Lê Minh Chi", null),
    Student("Phạm Quốc Dũng", 4.5),
    Student("Võ Thanh Em", 5.8)
)
@Preview(showBackground = true)
@Composable
fun Bangdiem(modifier: Modifier = Modifier){
    var tong = 0.0
    var NguoiCoDiem = 0
    var SinhVienGioi = 0
    var NguoiChuaCoDiem = 0
    var max = 0.0
    for (student in students){
        val diem = student.diem
        if (diem == null){
            NguoiChuaCoDiem++
        }else {
            tong = tong+diem
            NguoiCoDiem++
            if (diem >= 8.0) {
                SinhVienGioi++
            }

            if (diem > max|| NguoiCoDiem == 1) {
                max = diem
            }
        }
    }
    val diemTrungBinh = if (NguoiCoDiem > 0) {
        tong / NguoiCoDiem
    } else {
        0.0
    }
    Column (
        modifier = Modifier.padding(16.dp)
            .fillMaxSize()
            .background(Color(0xFFF3F2F7))

    ){
        Text(text="Bang diem lop",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier= Modifier.fillMaxWidth().background(color = Color(0xFF2e4476)).padding(start = 16.dp,
                end = 16.dp,top=30.dp,bottom=8.dp)
        )
        Card (
            modifier = Modifier
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF304878)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Điểm trung bình lớp",
                    color = Color.White
                )

                Text(
                    diemTrungBinh.toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Row (
                    modifier = Modifier.padding(15.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column(modifier= Modifier.weight(0.5f)) {
                            Text(text = "Gioi",color= Color.White)
                            Text(SinhVienGioi.toString(),color= Color.White,fontWeight = FontWeight.Bold)
                        }
                        Column(modifier= Modifier.padding(10.dp).weight(2f)) {
                            Text(text = "Chua co diem",color= Color.White)
                            Text(NguoiChuaCoDiem.toString(),color= Color.White,fontWeight = FontWeight.Bold)
                        }
                        Column(modifier= Modifier.padding(10.dp).weight(1f)) {
                            Text(text = "Cao nhat",color= Color.White)
                            Text(max.toString(),color= Color.White,fontWeight = FontWeight.Bold)
                        }
                    }

                }
            }
        }
        Text(
            text = "Danh sách-5 sinh viên",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF80828d),
            modifier = Modifier.padding(vertical = 12.dp)
        )
        for (student in students) {
            val diem = student.diem
            Row (
                modifier = Modifier.padding(15.dp)
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(modifier= Modifier.weight(1f)) {
                        Text(student.name,fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (diem != null) {
                            val xepLoai = XepHang(diem)
                            Text(
                                text = xepLoai.kq,
                                color = xepLoai.mau,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Chua co diem",
                                color = Color.Gray
                            )
                        }
                    }
                    Column(modifier= Modifier.padding(10.dp).weight(1f),
                        horizontalAlignment = Alignment.End) {
                        Text(student.diem?.toString() ?: "-",fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                }
            }
        }
    }
}
fun XepHang(diem: Double): Xeploai{
    return if(diem<0 || diem>10 ) {
        Xeploai("Khong hop le", Color(0xFF838991))
    }else if (diem>=8.0 ){
        Xeploai("Gioi", Color(0xFF708874))
    }else if(diem>=6.5){
        Xeploai("Kha", Color(0xFF6a8fcf))
    }else if(diem>=5.0){
        Xeploai("Trung Binh", Color(0xFFad6f3e))
    } else{
        Xeploai("Yeu", Color(0xFFaa5143))
    }
}
