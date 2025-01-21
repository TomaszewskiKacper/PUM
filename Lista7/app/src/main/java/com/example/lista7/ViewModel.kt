package com.example.lista7

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.random.Random


data class Student(
    val index : Int,
    val firstName : String,
    val surname : String,
    val mean : Float,
    val year : Int
)

val names = listOf("Kacper", "Ania", "Piotr", "Bartosz", "Michał", "Paulina", "Monika", "Janusz", "Zofia", "Rafał", "Aleksander", "Błażej", "Mignon", "Olimpia", "Edmondo")
val surnames = listOf("Tomaszewski", "Radmilo", "Aureliana", "Aphrodisia", "Flávio", "Einārs", "Ishfaq", "Vaso", "Winefride", "Erna", "Eyvǫr", "Mildgyð", "Yahui", "Halle")

class MainViewModel : ViewModel(){
    private var _studentList = mutableStateListOf<Student>()
    val studentList : List<Student>
        get() = _studentList


    private var _selectedStudent = mutableStateOf(0)
    val selectedStudent : Int
        get() = _selectedStudent.value

    fun setSelectedStudent(index: Int){
        _selectedStudent.value = index
    }


    fun addStudent(student : Student){
        _studentList.add(student)
    }

    init {
        for (i in 1..100){
            val s = Student(
                index = Random.nextInt(100_000, 399_999),
                firstName = names[Random.nextInt(0, names.size)],
                surname = surnames[Random.nextInt(0, surnames.size)],
                year = Random.nextInt(2010, 2025),
                mean = (Random.nextInt(4,11))/2F
            )
            addStudent(s)
        }
    }




}




