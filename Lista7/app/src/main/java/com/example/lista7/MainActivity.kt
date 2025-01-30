package com.example.lista7

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lista7.ui.theme.Lista7Theme

sealed class Screens(val route: String){
    data object StudentListScreen : Screens("student_list")
    data object StudentDetailScreen : Screens("student_detail")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            Lista7Theme {
                Main()
            }
        }
    }
}
@Preview
@Composable
fun Main(){
    Navigation()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(vm: MainViewModel = viewModel()){
    val navController = rememberNavController()
    Scaffold(
        content = {NavGraph(navController = navController, vm = vm)}
    )
}

@Composable
fun NavGraph(navController : NavHostController, vm: MainViewModel){
    NavHost(
        navController = navController,
        startDestination = Screens.StudentListScreen.route
    ){
        composable(route = Screens.StudentListScreen.route){ StudentListScreen(navController = navController, vm = vm) }
        composable(route = Screens.StudentDetailScreen.route){ StudentDetailScreen(vm = vm) }


    }
}


@Composable
fun StudentDetailScreen(vm: MainViewModel){
    val index = vm.selectedStudent
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
            .padding(10.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Detale Studenta",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Nr. Indexu: " + vm.studentList[index].index.toString(),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Imię: " + vm.studentList[index].firstName,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Nazwisko: " + vm.studentList[index].surname,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Rok rozpoczęcia studiów: " + vm.studentList[index].year.toString(),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Średnia ocen: " + vm.studentList[index].mean.toString(),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )}


}


@Composable
fun StudentListScreen(vm : MainViewModel, navController: NavHostController){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
            .padding(10.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Lista Studentów",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))
        LazyColumn {
            items(vm.studentList.size){ index ->
                StudentListItem(vm.studentList[index],index, navController = navController, vm = vm)
            }


        }
    }

}

@Composable
fun StudentListItem(student: Student, index : Int, vm : MainViewModel, navController: NavHostController){
    Spacer(Modifier.height(10.dp))
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(Color.Gray).padding(2.dp).clickable {
            vm.setSelectedStudent(index = index)
            navController.navigate("student_detail")
        }
    ){
        Text(
            text = vm.studentList[index].index.toString(),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1F)
        )
        Text(
            text = vm.studentList[index].firstName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1F)
        )
        Text(
            text = vm.studentList[index].surname,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1F)
        )
    }

}


