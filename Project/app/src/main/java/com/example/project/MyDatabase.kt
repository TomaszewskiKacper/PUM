package com.example.project

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


// TABLE FOR IMAGES
@Entity(tableName = "image_table")
data class Image(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val name : String,
    val image_path : String
)

// TABLE FOR TAGS
@Entity(tableName = "tag_table")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val name : String
)

//TABLE FOR LINKING TAGS TO IMAGES
@Entity(tableName = "image_tags_table",
    primaryKeys = ["imageId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = Image::class, parentColumns = ["id"], childColumns = ["imageId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE)
    ])
data class ImageTags(
    val imageId : Int,
    val tagId : Int
)



// DAO FOR DATABASE
@Dao
interface ImageDao{

    // INSERTING
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddImage(image: Image) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddTagToImage(imageTags: ImageTags)


    // REMOVING
    @Delete
    suspend fun DeleteImage(image: Image)

    @Delete
    suspend fun DeleteTag(tag: Tag)

    @Delete
    suspend fun RemoveTagFromImage(imageTags: ImageTags)


    // QUERIES
    @Query("""
    SELECT i.* FROM image_table i
    LEFT JOIN image_tags_table it ON i.id = it.imageId
    LEFT JOIN tag_table t ON it.tagId = t.id
    WHERE LOWER(i.name) LIKE '%' || LOWER(:searchQuery) || '%'
       OR (t.name IN (:tags) AND (SELECT COUNT(DISTINCT t2.id)
                                  FROM image_tags_table it2
                                  JOIN tag_table t2 ON it2.tagId = t2.id
                                  WHERE it2.imageId = i.id) = :tagCount)
    GROUP BY i.id
""")
    fun getImagesByTags(tags: List<String>, tagCount: Int, searchQuery: String): Flow<List<Image>>

    @Query("SELECT * FROM image_table")
    fun getAllImages() : Flow<List<Image>>

    @Query("SELECT * FROM tag_table")
    fun getAllTags() : Flow<List<Tag>>

    @Query("SELECT * FROM tag_table WHERE id = :id")
    suspend fun getTag(id :Int) : Tag?

    @Query("SELECT * FROM image_table WHERE id = :id")
    suspend fun getImage(id :Int) : Image?

    @Query("""
    SELECT t.* FROM tag_table t
    JOIN image_tags_table it ON t.id = it.tagId
    WHERE it.imageId = :imageId
""")
    fun getTagsForImage(imageId: Int): Flow<List<Tag>>


    @Update
    suspend fun updateTag(tag : Tag)

    @Update
    suspend fun updateImage(img : Image)



}




// DATABASE
@Database(entities = [Image::class, Tag::class, ImageTags::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imgDao() : ImageDao

    companion object{
        @Volatile
        private var Instance: ImageDatabase? = null

        fun getDatabase(context: Context) : ImageDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ImageDatabase::class.java, "image_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}