package com.example.project

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// FACTORY
class MyViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyViewModel(application) as T
    }
}


class MyViewModel(application: Application) : ViewModel() {

    // Repository
    private val repository: ImageRepository

    // Image States
    private val _imageState: MutableStateFlow<List<Image>> = MutableStateFlow(emptyList())
    val images: StateFlow<List<Image>> get() = _imageState

    private val _imageSearchState: MutableStateFlow<List<Image>> = MutableStateFlow(emptyList())
    val imagesSearch: StateFlow<List<Image>> get() = _imageSearchState

    private val _selectedImage: MutableStateFlow<Image?> = MutableStateFlow(null)
    val selectedImage: StateFlow<Image?> get() = _selectedImage

    // Tag States
    private val _tagState: MutableStateFlow<List<Tag>> = MutableStateFlow(emptyList())
    val tags: StateFlow<List<Tag>> get() = _tagState

    private val _imageTags: MutableStateFlow<List<Tag>> = MutableStateFlow(emptyList())
    val imageTags: StateFlow<List<Tag>> get() = _imageTags

    private val _selectedTag: MutableStateFlow<Tag?> = MutableStateFlow(null)
    val selectedTag: StateFlow<Tag?> get() = _selectedTag

    // Comment States
    private val _commentState: MutableStateFlow<List<Comment>> = MutableStateFlow(emptyList())
    val comments: StateFlow<List<Comment>> get() = _commentState

    private val _imageCommentsState: MutableStateFlow<List<Comment>> = MutableStateFlow(emptyList())
    val imageComments: StateFlow<List<Comment>> get() = _imageCommentsState

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Fetch Data
    private fun fetchImages() {
        viewModelScope.launch {
            repository.getAllImages().collect { images ->
                _imageState.value = images
            }
        }
    }

    private fun fetchTags() {
        viewModelScope.launch {
            repository.getAllTags().collect { tags ->
                _tagState.value = tags
            }
        }
    }

    fun fetchTagsForImage(imageId: Int) {
        viewModelScope.launch {
            repository.getTagsForImage(imageId).collect { tags ->
                _imageTags.value = tags
            }
        }
    }

    private fun fetchSearchImages(query: String) {
        viewModelScope.launch {
            val tags = query.split(" ").filter { it.isNotBlank() }
            repository.getImagesByTags(tags, tags.size, query).collect { images ->
                _imageSearchState.value = images
            }
        }
    }

    fun fetchCommentsForImage(imageId: Int) {
        viewModelScope.launch {
            repository.getCommentsForImage(imageId).collect { comments ->
                _imageCommentsState.value = comments
            }
        }
    }

    // CRUD Operations
    suspend fun addImage(img: Image): Long {
        return repository.addImage(img)
    }

    fun addTag(tag: Tag) {
        viewModelScope.launch {
            repository.addTag(tag)
        }
    }

    fun addTagToImage(imgTag: ImageTags) {
        viewModelScope.launch {
            repository.addTagToImage(imgTag)
        }
    }

    suspend fun addComment(comment: Comment): Long {
        return repository.addComment(comment = comment)
    }

    fun addCommentToImage(imageComment: ImageComments) {
        viewModelScope.launch {
            repository.addCommentToImage(imageComment)
        }
    }

    fun updateTag(tag: Tag) {
        viewModelScope.launch {
            repository.updateTag(tag)
        }
    }

    fun updateImage(img: Image) {
        viewModelScope.launch {
            repository.updateImage(img)
        }
    }

    fun updateComment(comment: Comment) {
        viewModelScope.launch {
            repository.updateComment(comment)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        fetchSearchImages(query)
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }

    fun deleteImage(img: Image) {
        viewModelScope.launch {
            repository.deleteImage(img)
        }
    }

    fun removeTagFromImage(imageTags: ImageTags) {
        viewModelScope.launch {
            repository.removeTagFromImage(imageTags)
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }

    fun removeCommentFromImage(imageComment: ImageComments) {
        viewModelScope.launch {
            repository.removeCommentFromImage(imageComment)
        }
    }

    fun getTag(id: Int) {
        viewModelScope.launch {
            _selectedTag.value = repository.getTag(id)
        }
    }

    fun getImage(id: Int) {
        viewModelScope.launch {
            _selectedImage.value = repository.getImage(id)
        }
    }

    fun clearSelected() {
        _selectedTag.value = null
        _selectedImage.value = null
    }

    // Init
    init {
        val db = ImageDatabase.getDatabase(application)
        val dao = db.imgDao()
        repository = ImageRepository(dao)

        fetchImages()
        fetchTags()
    }
}
