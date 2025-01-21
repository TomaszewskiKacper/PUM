package com.example.lista6

data class Exercise(val content : String, val points : Int)
data class Subject(val name : String)
data class ExerciseList(val exercises : List<Exercise>, val subject: Subject, val grade : Int, val num : Int)

val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras arcu ex, ullamcorper non mauris sit amet, imperdiet iaculis tellus. Aenean varius eget nisl eget dapibus. Ut non"

