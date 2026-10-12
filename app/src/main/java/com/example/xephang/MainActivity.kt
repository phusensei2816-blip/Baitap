package com.example.xephang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xephang.ui.theme.XephangTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XephangTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RankingScore(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
data class Xeploai(
    val hocluc: String,
    val mau: Color
)
@Preview(showBackground = true)
@Composable
fun RankingScore(modifier: Modifier= Modifier){
    //de 'diem' kieu du lieu string de co the nhap tu TextField()
    var diem by remember { mutableStateOf("8.5") }
//column( text->card(text->textf-textxephang)->text->card(column(text)))
    Column(modifier= Modifier.fillMaxWidth().padding(10.dp).background(color = Color(0xFFf3f2f7))) {
        Text(text="Xep hang hoc luc",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier= Modifier.fillMaxWidth().background(color = Color(0xFF2e4476)).padding(start = 16.dp,
                end = 16.dp,top=30.dp,bottom=8.dp)
        )
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEEEEEE)
            )) {
            Column(modifier=  Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text="Diem Trung Binh",
                    color = Color.Gray)
                TextField(
                    value = diem,
                    onValueChange ={
                        diem = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    maxLines = 1
                )
                val diemso = diem.toDoubleOrNull()
                if (diemso != null){
                    val xeploaidiem = XepHang(diemso)
                    Text(
                        xeploaidiem.hocluc,
                        color = xeploaidiem.mau,
                    )
                }
            }
        }
        Text(text = "Kiem Tra Them",
            color = Color.Gray)
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEEEEEE)
            )) {
            Column(modifier = Modifier.padding(10.dp),
                ) {
                Row(modifier = Modifier.fillMaxWidth()
                    ,horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    var xephangr = XepHang(9.0)
                    Text(text = "Diem 9")
                    Text(xephangr.hocluc,
                        color = xephangr.mau)
                }
                Row(modifier = Modifier.fillMaxWidth()
                    ,horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    var xephangr = XepHang(7.0)
                    Text(text = "Diem 7")
                    Text(xephangr.hocluc,
                        color = xephangr.mau)
                }
                Row(modifier = Modifier.fillMaxWidth()
                    ,horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    var xephangr = XepHang(5.5)
                    Text(text = "Diem 5.5")
                    Text(xephangr.hocluc,
                        color = xephangr.mau)
                }
                Row(modifier = Modifier.fillMaxWidth()
                    ,horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    var xephangr = XepHang(3.0)
                    Text(text = "Diem 3")
                    Text(xephangr.hocluc,
                        color = xephangr.mau)
                }
                Row(modifier = Modifier.fillMaxWidth()
                    ,horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    var xephangr = XepHang(11.0)
                    Text(text = "Diem 11")
                    Text(xephangr.hocluc,
                        color = xephangr.mau)
                }
            }
        }
    }
}
//ham xeploai
fun XepHang(diem: Double): Xeploai{
    return if(diem<0 || diem>10) {
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