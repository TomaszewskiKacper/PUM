package com.example.project

import android.content.Context
import androidx.room.*
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

// TABLE FOR COMMENTS
@Entity(tableName = "comment_table")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val text: String,
    val timestamp: Long
)

// TABLE FOR LINKING TAGS TO IMAGES
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

// TABLE FOR LINKING COMMENTS TO IMAGES
@Entity(tableName = "image_comments_table",
    primaryKeys = ["imageId", "commentId"],
    foreignKeys = [
        ForeignKey(entity = Image::class, parentColumns = ["id"], childColumns = ["imageId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Comment::class, parentColumns = ["id"], childColumns = ["commentId"], onDelete = ForeignKey.CASCADE)
    ])
data class ImageComments(
    val imageId: Int,
    val commentId: Int
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddComment(comment: Comment): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddCommentToImage(imageComments: ImageComments)

    // REMOVING
    @Delete
    suspend fun DeleteImage(image: Image)

    @Delete
    suspend fun DeleteTag(tag: Tag)

    @Delete
    suspend fun RemoveTagFromImage(imageTags: ImageTags)

    @Delete
    suspend fun DeleteComment(comment: Comment)

    @Delete
    suspend fun RemoveCommentFromImage(imageComments: ImageComments)

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

    @Query("""
    SELECT c.* FROM comment_table c
    JOIN image_comments_table ic ON c.id = ic.commentId
    WHERE ic.imageId = :imageId
    """)
    fun getCommentsForImage(imageId: Int): Flow<List<Comment>>

    @Update
    suspend fun updateTag(tag : Tag)

    @Update
    suspend fun updateImage(img : Image)

    @Update
    suspend fun updateComment(comment: Comment)
}

// DATABASE
@Database(entities = [Image::class, Tag::class, ImageTags::class, Comment::class, ImageComments::class], version = 2, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imgDao() : ImageDao

    companion object{
        @Volatile
        private var Instance: ImageDatabase? = null

        fun getDatabase(context: Context) : ImageDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ImageDatabase::class.java, "image_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
