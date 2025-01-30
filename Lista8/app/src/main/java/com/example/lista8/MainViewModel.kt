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

    @Query("DELETE FROM grade_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT AVG(grade) FROM grade_table")
    fun getAVG(): Flow<Double>

    @Update
    suspend fun update(grade : Grade)

    @Query("SELECT * FROM grade_table WHERE id = :id")
    suspend fun getGrade(id: Int): Grade?}



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
    suspend fun getGrade(id: Int): Grade? = gradeDao.getGrade(id)
    suspend fun deleteGrade(id : Int) = gradeDao.delete(id)
    fun getAVG() = gradeDao.getAVG()
}


class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}

class MainViewModel(application: Application) : ViewModel() {
    private val repository: GradeRepository
    private val _gradesState: MutableStateFlow<List<Grade>> = MutableStateFlow(emptyList())
    val gradesState: StateFlow<List<Grade>>
        get() = _gradesState

    private val _selectedGrade: MutableStateFlow<Grade?> = MutableStateFlow(null)
    val selectedGrade: StateFlow<Grade?>
        get() = _selectedGrade

    private val _averageGrade: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val averageGrade: StateFlow<Double> get() = _averageGrade

    init {
        val db = GradeDatabase.getDatabase(application)
        val dao = db.gradeDao()
        repository = GradeRepository(dao)

        fetchGrades()
        fetchAverageGrade()
    }

    private fun fetchGrades() {
        viewModelScope.launch {
            repository.getGrades().collect { grades ->
                _gradesState.value = grades
            }
        }
    }

    private fun fetchAverageGrade() {
        viewModelScope.launch {
            repository.getAVG().collect { avg ->
                _averageGrade.value = avg
            }
        }
    }

    fun addGrade(grade: Grade) {
        viewModelScope.launch {
            repository.insertGrade(grade)
        }
    }
    fun clearSelectedGrade() {
        _selectedGrade.value = null
    }

    fun updateGrade(grade: Grade) {
        viewModelScope.launch {
            repository.updateGrade(grade)
        }
    }

    fun deleteGrade(id: Int){
        viewModelScope.launch {
            repository.deleteGrade(id)
        }
    }

    fun getGrade(id: Int) {
        viewModelScope.launch {
            _selectedGrade.value = repository.getGrade(id)
        }
    }
}
