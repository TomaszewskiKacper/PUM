package com.example.lista6

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lista6.ui.theme.Lista6Theme
import kotlin.math.round
import kotlin.random.Random

sealed class Screens(val route : String){
    data object ListyScreen : Screens("listy")
    data object OcenyScreen : Screens("oceny")
    data object ZadaniaScreen : Screens("zadania")
}

sealed class BottomBar(val route : String, val title : String, val icon : ImageVector){
    data object ListyZadan : BottomBar(Screens.ListyScreen.route, "Listy Zadan", Icons.Default.Menu)
    data object Oceny : BottomBar(Screens.OcenyScreen.route, "Oceny", Icons.Default.Check)
}



class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Lista6Theme {
                Main()
            }
        }
    }
}

@Composable
fun Main(){
//CREATE DATA
    var item_list : MutableList<ExerciseList> = mutableListOf()
    val subjects: List<Subject> = listOf(
        Subject("PUM"),
        Subject("Matematyka"),
        Subject("Fizyka"),
        Subject("Elektronika"),
        Subject("Algorytmy")
    )

    var n: MutableMap<Subject, Int> = subjects.associateWith { 0 }.toMutableMap()

    for (lista in 1..20){   //20 list zadań
        var zad : MutableList<Exercise> = mutableListOf()
        for (zadanie in 1 .. Random.nextInt(1,11)){ //Add up to 10 questions to a list
            zad.add(Exercise(loremIpsum.substring(0, Random.nextInt(loremIpsum.length)), Random.nextInt(1,11)))
        }
        var subj = subjects[Random.nextInt(0, subjects.size)]
        n[subj] = n[subj]?.plus(1) ?: 1
        item_list.add(ExerciseList(zad, subj, 2 + Random.nextInt(0,7)/2,n[subj] ?: 1))
    }

    Navigation(item_list, subjects)

}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(item_list: List<ExerciseList>, subjects: List<Subject>){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {BottomMenu(navController = navController)},
        content = {NavGraph(navController = navController, item_list, subjects)}
    )
}


@Composable
fun NavGraph(navController: NavHostController, item_list: List<ExerciseList>, subjects: List<Subject>){
    NavHost(
        navController = navController,
        startDestination = Screens.ListyScreen.route
    ){
        composable(route = Screens.ListyScreen.route){ListyScreen(item_list, navController)}
        composable(route = Screens.OcenyScreen.route){OcenyScreen(item_list, subjects)}
        composable(route = "${Screens.ZadaniaScreen.route}/{index}") {
            navBackStackEntry ->
            val index = navBackStackEntry.arguments?.getString("index")?.toInt()?:0
            ZadaniaScreen(item_list, index)
        }
    }
}

@Composable
fun BottomMenu(navController: NavHostController){
    val screens = listOf(
        BottomBar.ListyZadan, BottomBar.Oceny
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = "icon") },
                selected = currentDestination?.hierarchy?.any{it.route == screen.route} == true,
                onClick = {navController.navigate(screen.route)}
            )
        }
    }
}


@Composable
fun ZadaniaScreen(item_list: List<ExerciseList>, ind : Int){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ){
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = item_list[ind].subject.name, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text(text = "Lista " + item_list[ind].num.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            items(item_list[ind].exercises.size){ index ->
                val exerciseList = item_list[ind].exercises
                ZadaniaListItem(exerciseList[index], index)
            }
        }
    }
}

@Composable
fun ZadaniaListItem(exercise: Exercise, ind: Int){
    Spacer(modifier = Modifier.height(15.dp))
    Column(
        modifier = Modifier
            .background(color = Color.Gray)
            .clickable { },
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()

        ) {

            Text(
                text = "pkt :" + exercise.points,
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp)

            )
        }
        Text(
            text = "Zadanie " + (ind+1).toString() ,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)

        )
        Text(
            text = exercise.content ,
            fontSize = 18.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}



@Composable
fun OcenyScreen(item_list: List<ExerciseList>, subjects : List<Subject>){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ){
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Moje Oceny", fontSize = 40.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            items(subjects.size){ index ->
                val exerciseList = item_list[index]
                OcenyListItem(subjects[index].name, item_list)
            }
        }
    }
}

@Composable
fun OcenyListItem(Subject : String, item_list: List<ExerciseList>) {
    var grade: Float = 0F
    var count = 0;
    for (item in item_list) {
        if (item.subject.name == Subject) {
            grade += item.grade
            count++
        }
    }
    if (count > 0) {
        grade /= count

        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .background(color = Color.Gray)
                .clickable { },
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()

            ) {
                Text(
                    text = Subject,
                    fontSize = 30.sp,
                    modifier = Modifier.weight(3F)
                )
                Text(
                    text = "Średnia :" + (round(grade*2)/2 ).toString(),
                    fontSize = 25.sp,
                    modifier = Modifier.weight(1F)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()

            ) {
                Text(
                    text = "Liczba List: " + count.toString(),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(3F)
                )
            }
        }
    }
}



@Composable
fun ListyScreen(item_list : List<ExerciseList>, navController: NavHostController){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Moje Listy Zadań", fontSize = 40.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            items(item_list.size){ index ->
                val exerciseList = item_list[index]
                ExerciseListItem(exerciseList, navController, index)
            }
        }

    }
}

@Composable
fun ExerciseListItem(exerciseList: ExerciseList, navController: NavHostController, index : Int){
    Spacer(modifier = Modifier.height(15.dp))
    Column (
        modifier = Modifier
            .background(color = Color.Gray)
            .clickable {
                navController.navigate("${Screens.ZadaniaScreen.route}/$index")
            },
        verticalArrangement = Arrangement.Center
    ){
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()

        ) {
            Text(
                text = exerciseList.subject.name,
                fontSize = 30.sp,
                modifier = Modifier.weight(3F)
            )
            Text(
                text = "Lista " + exerciseList.num.toString(),
                fontSize = 30.sp,
                modifier = Modifier.weight(1F)
            )
        }
        Row (
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()

        ){
            Text(
                text = "Liczba Zadań: " + exerciseList.exercises.size.toString(),
                fontSize = 15.sp,
                modifier = Modifier.weight(3F)
            )
            Text(
                text = "Ocena: " + exerciseList.grade.toString(),
                fontSize = 15.sp,
                modifier = Modifier.weight(1F)
            )
        }
    }
}