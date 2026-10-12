package com.example.simplecalculator

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager

@Composable

fun Calculator(modifier: Modifier = Modifier){
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Surface(

    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Text(
                    text= "Thuc hanh 3",
                    color = Color(0xFF141414),
                    textAlign = TextAlign.Center,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                TextField(
                    value = number1,
                    onValueChange = {
                        number1=it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    visualTransformation = VisualTransformation.None,
                    maxLines = 1,
                )
                Row() {
                    modifier.padding(10.dp)
                    Button(onClick ={result=(number1.toDouble() + number2.toDouble()).toString()} ) {
                        Text(text = "+")
                    }
                    modifier.padding(10.dp)
                    Button(onClick ={result=(number1.toDouble() - number2.toDouble()).toString()} ) {
                        Text(text = "-")
                    }
                    modifier.padding(10.dp)
                    Button(onClick ={result=(number1.toDouble() * number2.toDouble()).toString()} ) {
                        Text(text = "*")
                    }
                    modifier.padding(10.dp)
                    Button(onClick ={result=(number1.toDouble() / number2.toDouble()).toString()} ) {
                        Text(text = "/")
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                TextField(
                    value = number2,
                    onValueChange = {
                        number2=it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    visualTransformation = VisualTransformation.None,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                TextField(
                    value = result,
                    onValueChange = {

                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    visualTransformation = VisualTransformation.None,
                    label = {Text(text="Ket qua: ")},
                    maxLines = 1,
                )
            }
        }
    }
}