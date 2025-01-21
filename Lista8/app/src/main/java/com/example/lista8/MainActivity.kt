package com.example.lista8

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lista8.ui.theme.Lista8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lista8Theme {


            }
        }
    }
}
@Preview
@Composable
fun Main(){
    GradeScreen()
}


@Composable
fun GradeScreen(){
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
            .padding(15.dp)
    ){
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Moje Oceny",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(15.dp))
        LazyColumn {

        }


        Spacer(Modifier.weight(1F))
        Row (
            Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .padding(15.dp)
        ){
            Text(
                text = "średnia Ocen: ",
                fontSize = 25.sp,
                modifier = Modifier.weight(1F)
            )

            Text("todo", fontSize = 25.sp)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Nowy") }
    }
}

@Composable
fun GradeItemScreen(){
    Spacer(Modifier.height(10.dp))
    Row (
        Modifier
            .background(Color.Gray)
            .fillMaxWidth()
            .padding(15.dp)
    ){
        Text(
            text = "przedmiot: ",
            fontSize = 25.sp,
            modifier = Modifier.weight(1F)
        )

        Text("ocena", fontSize = 25.sp)
    }
}

@Composable
fun EditScreen(){
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
            .padding(15.dp)
    ){
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Edytuj",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold
            )
        }

        var nametext by remember { mutableStateOf(TextFieldValue("")) }
        Spacer(Modifier.height(20.dp))
        TextField(
            value = nametext,
            onValueChange = {newText -> nametext = newText},
            modifier = Modifier.fillMaxWidth()

        )
        var gradetext by remember { mutableStateOf(TextFieldValue("")) }
        Spacer(Modifier.height(20.dp))
        TextField(
            value = gradetext,
            onValueChange = {newText -> gradetext = newText},
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(Modifier.weight(1F))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Usuń") }
    }
}

@Composable
fun AddScreen(){
    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
            .padding(15.dp)
    ){
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Dodaj Nowy",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold
            )
        }

        var nametext by remember { mutableStateOf(TextFieldValue("")) }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Nazwa Przedmiotu",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        TextField(
            value = nametext,
            onValueChange = {newText -> nametext = newText},
            modifier = Modifier.fillMaxWidth()
        )
        var gradetext by remember { mutableStateOf(TextFieldValue("")) }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Ocena",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        TextField(
            value = gradetext,
            onValueChange = {newText -> gradetext = newText},
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(Modifier.weight(1F))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Dodaj") }
    }


}