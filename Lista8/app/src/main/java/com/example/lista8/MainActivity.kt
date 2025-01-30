package com.example.lista8

import android.annotation.SuppressLint
import android.app.Application
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lista8.ui.theme.Lista8Theme
import kotlin.math.round

sealed class Screens(val route : String){
    data object AddScreen : Screens("Add")
    data object GradeScreen : Screens("Grades")
    data object EditScreen : Screens("Edit")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lista8Theme {

                Main()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(vm : MainViewModel){
    val navController = rememberNavController()
    Scaffold(
        content = { NavGraph(navController = navController, vm = vm)}
    )
}

@Composable
fun NavGraph(navController: NavHostController, vm : MainViewModel){
    NavHost(
        navController = navController,
        startDestination = Screens.GradeScreen.route
    ){
        composable(route = Screens.GradeScreen.route){ GradeScreen(navController = navController, vm = vm)}
        composable(route = Screens.AddScreen.route){ AddScreen(navController = navController, vm = vm) }
        composable(
            route = "${Screens.EditScreen.route}/{id}",
            arguments = listOf(navArgument("id") {type = NavType.IntType})
        ){ backStackEntry ->
            val gradeID = backStackEntry.arguments?.getInt("id") ?: 0
            EditScreen(navController = navController, vm = vm, gradeID = gradeID)
        }
    }


}




@Preview
@Composable
fun Main(){
    val viewModel : MainViewModel = viewModel(
        LocalViewModelStoreOwner.current!!,
        "MainViewModel",
        MainViewModelFactory(LocalContext.current.applicationContext as Application)
    )


    Navigation(viewModel)
}


@Composable
fun GradeScreen(navController: NavHostController, vm : MainViewModel){
    val grades by vm.gradesState.collectAsStateWithLifecycle()
    val averageGrade by vm.averageGrade.collectAsStateWithLifecycle()

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
        LazyColumn { items(grades.size){ grade ->
            GradeItemScreen(navController = navController, grade = grades[grade])
        }
        }


        Spacer(Modifier.weight(1F))
        Row (
            Modifier
                .background(Color.Gray)
                .fillMaxWidth()
                .padding(15.dp)
        ){
            Text(
                text = "Średnia Ocen: ",
                fontSize = 25.sp,
                modifier = Modifier.weight(1F)
            )

            Text((round(averageGrade*100)/100).toString(), fontSize = 25.sp)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = {navController.navigate(Screens.AddScreen.route)}, modifier = Modifier.fillMaxWidth()) { Text("Nowy") }
    }
}

@Composable
fun GradeItemScreen(navController: NavHostController, grade: Grade){
    Spacer(Modifier.height(10.dp))
    Row (
        Modifier
            .background(Color.Gray)
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                navController.navigate("${Screens.EditScreen.route}/${grade.id}")
            }
    ){
        Text(
            text = grade.name,
            fontSize = 25.sp,
            modifier = Modifier.weight(1F)
        )

        Text(grade.grade.toString(), fontSize = 25.sp)
    }
}

//@Composable
//fun EditScreen(navController: NavHostController, vm : MainViewModel, gradeID : Int){
//    vm.getGrade(gradeID)
//
//    Column (
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.Start,
//        modifier = Modifier
//            .background(Color.LightGray)
//            .fillMaxSize()
//            .padding(15.dp)
//    ){
//        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
//            Text(
//                text = "Edytuj",
//                fontSize = 45.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//        val grade by vm.selectedGrade.collectAsStateWithLifecycle()
//
//        var nametext by remember { mutableStateOf(TextFieldValue(grade!!.name)) }
//
//        Spacer(Modifier.height(20.dp))
//        TextField(
//            value = nametext,
//            onValueChange = {newText -> nametext = newText},
//            modifier = Modifier.fillMaxWidth()
//
//        )
//        var gradetext by remember { mutableStateOf(TextFieldValue(grade!!.grade.toString())) }
//        Spacer(Modifier.height(20.dp))
//        TextField(
//            value = gradetext,
//            onValueChange = {newText -> gradetext = newText},
//            modifier = Modifier.fillMaxWidth()
//
//        )
//        Spacer(Modifier.weight(1F))
//        Button(onClick = {
//            var new_grade = Grade(grade!!.id, nametext.toString(), gradetext.toString().toInt())
//            vm.updateGrade(new_grade)
//            navController.navigate(Screens.GradeScreen.route)
//        }, modifier = Modifier.fillMaxWidth()) { Text("Update") }
//
//        Button(onClick = {
//            vm.deleteGrade(gradeID)
//            navController.navigate(Screens.GradeScreen.route)
//        }, modifier = Modifier.fillMaxWidth()) { Text("Usuń") }
//    }
//}

@Composable
fun EditScreen(navController: NavHostController, vm: MainViewModel, gradeID: Int) {
    vm.getGrade(gradeID) // Fetch grade .

    DisposableEffect(Unit) {
        onDispose {
            vm.clearSelectedGrade()
        }
    }


    val grade by vm.selectedGrade.collectAsStateWithLifecycle()

    // Check if the grade is loaded
    if (grade == null) {
        // Display a loading indicator or placeholder
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxSize()
        ) {
            Text(text = "Loading...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        // When the grade is loaded, display the edit screen
        var nametext by remember { mutableStateOf(TextFieldValue(grade!!.name)) }
        var gradetext by remember { mutableStateOf(TextFieldValue(grade!!.grade.toString())) }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Edytuj",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))
            TextField(
                value = nametext,
                onValueChange = { newText -> nametext = newText },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            TextField(
                value = gradetext,
                onValueChange = { newText -> gradetext = newText },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.weight(1F))
            Button(
                onClick = {
                    val newGrade = Grade(grade!!.id, nametext.text, gradetext.text.toInt())
                    vm.updateGrade(newGrade)
                    navController.navigate(Screens.GradeScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update")
            }

            Button(
                onClick = {
                    vm.deleteGrade(gradeID)
                    navController.navigate(Screens.GradeScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usuń")
            }
        }
    }
}


@Composable
fun AddScreen(navController: NavHostController, vm : MainViewModel){
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

        Button(onClick = {
            vm.addGrade(Grade(id = 0, name = nametext.text, grade = gradetext.text.toInt()))
            navController.navigate(Screens.GradeScreen.route)
                         }, modifier = Modifier.fillMaxWidth()) { Text("Dodaj") }
    }


}