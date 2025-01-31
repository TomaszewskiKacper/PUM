package com.example.project







class ImageRepository(private val imgDao : ImageDao) {
    fun getImagesByTags(tags: List<String>, tagCount: Int, searchQuery: String) = imgDao.getImagesByTags(tags, tagCount, searchQuery)
    fun getAllImages() = imgDao.getAllImages()
    fun getAllTags() = imgDao.getAllTags()
    fun getTagsForImage(imageId:Int) = imgDao.getTagsForImage(imageId)
    suspend fun getTag(id:Int): Tag? = imgDao.getTag(id)
    suspend fun getImage(id:Int): Image? = imgDao.getImage(id)


    suspend fun addImage(img : Image) : Long = imgDao.AddImage(img)
    suspend fun addTag(tag : Tag) = imgDao.AddTag(tag)
    suspend fun updatetag(tag : Tag) = imgDao.updateTag(tag)
    suspend fun updateImage(img : Image) = imgDao.updateImage(img)
    suspend fun deleteTag(tag : Tag) = imgDao.DeleteTag(tag)
    suspend fun deleteImage(img : Image) = imgDao.DeleteImage(img)
    suspend fun removeTagFromImage(imageTags: ImageTags) = imgDao.RemoveTagFromImage(imageTags)

    suspend fun addTagToImage(imgTag : ImageTags) = imgDao.AddTagToImage(imgTag)
}