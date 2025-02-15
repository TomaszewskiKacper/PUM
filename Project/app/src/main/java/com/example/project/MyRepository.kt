package com.example.project



class ImageRepository(private val imgDao : ImageDao) {

    // Image Queries
    fun getImagesByTags(tags: List<String>, tagCount: Int) = imgDao.getImagesByTags(tags, tagCount)
    fun getAllImages() = imgDao.getAllImages()
    suspend fun getImage(id:Int): Image? = imgDao.getImage(id)

    // Tag Queries
    fun getAllTags() = imgDao.getAllTags()
    fun getTagsForImage(imageId: Int) = imgDao.getTagsForImage(imageId)
    suspend fun getTag(id: Int): Tag? = imgDao.getTag(id)

    // Comment Queries
    fun getCommentsForImage(imageId: Int) = imgDao.getCommentsForImage(imageId)

    // Insert Operations
    suspend fun addImage(img: Image): Long = imgDao.AddImage(img)
    suspend fun addTag(tag: Tag) = imgDao.AddTag(tag)
    suspend fun addTagToImage(imgTag: ImageTags) = imgDao.AddTagToImage(imgTag)
    suspend fun addComment(comment: Comment): Long = imgDao.AddComment(comment)
    suspend fun addCommentToImage(imageComment: ImageComments) = imgDao.AddCommentToImage(imageComment)

    // Update Operations
    suspend fun updateTag(tag: Tag) = imgDao.updateTag(tag)
    suspend fun updateImage(img: Image) = imgDao.updateImage(img)
    suspend fun updateComment(comment: Comment) = imgDao.updateComment(comment)

    // Delete Operations
    suspend fun deleteTag(tag: Tag) = imgDao.DeleteTag(tag)
    suspend fun deleteImage(img: Image) = imgDao.DeleteImage(img)
    suspend fun removeTagFromImage(imageTags: ImageTags) = imgDao.RemoveTagFromImage(imageTags)
    suspend fun deleteComment(comment: Comment) = imgDao.DeleteComment(comment)
    suspend fun removeCommentFromImage(imageComment: ImageComments) = imgDao.RemoveCommentFromImage(imageComment)
}
