package com.example.lista8

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Entity(tableName = "grade_table")
data class Grade(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val name : String,
    val grade : Int
)

@Dao
interface GradeDao{
    @Query("SELECT * FROM grade_table")
    fun getGrades(): Flow<List<Grade>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(grade : Grade)

    @Update
    suspend fun update(grade : Grade)

    @Query("SELECT * FROM grade_table WHERE id = :id")
    fun getGrade(id : Int) : List<Grade>
}

@Database(entities = [Grade::class], version = 1, exportSchema = false)
abstract class GradeDatabase : RoomDatabase() {
    abstract fun gradeDao() : GradeDao

    companion object{
        @Volatile
        private var Instance: GradeDatabase? = null

        fun getDatabase(context: Context) : GradeDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, GradeDatabase::class.java, "grade_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class GradeRepository(private val gradeDao : GradeDao){
    fun getGrades() = gradeDao.getGrades()
    suspend fun updateGrade(grade : Grade) = gradeDao.update(grade)
    suspend fun insertGrade(grade : Grade) = gradeDao.insert(grade)
    fun getGrade(id : Int) = gradeDao.getGrade(id)
}


class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}

class MainViewModel(application : Application) : ViewModel(){
    private val repository : GradeRepository
    private val _gradesState: MutableStateFlow<List<Grade>> = MutableStateFlow(emptyList())
    val gradesState: StateFlow<List<Grade>>
        get() = _gradesState



    init {
        val db = GradeDatabase.getDatabase(application)
        val dao = db.gradeDao()
        repository = GradeRepository(dao)

        fetchGrades()
    }

    private fun fetchGrades(){
        viewModelScope.launch {
            repository.getGrades().collect{
                grades ->
                _gradesState.value = grades
            }
        }
    }

    fun addGrade(grade : Grade){
        viewModelScope.launch {
            repository.insertGrade(grade)
        }
    }

    fun updateGrade(grade : Grade){
        viewModelScope.launch {
            repository.updateGrade(grade)
        }
    }

    fun getGrade(id : Int){
        viewModelScope.launch {
            repository.getGrade(id)
        }
    }


}