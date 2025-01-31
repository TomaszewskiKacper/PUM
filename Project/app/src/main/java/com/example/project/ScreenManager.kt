package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Screens(val route : String){
    data object ImageScreen : Screens("image")
    data object GalleryScreen : Screens("gallery")
    data object TagScreen : Screens("tag")
    data object TagListScreen : Screens("tagList")
    data object AddImageScreen : Screens("imageAdd")
}



class ScreenManager() {

    // NAVIGATION
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun Navigation(vm : MyViewModel){
        val navController = rememberNavController()

        Scaffold(
            content = { NavGraph(navController = navController, vm = vm)}
        )
    }

    // NAVIGATION GRAPH
    @Composable
    fun NavGraph(navController: NavHostController, vm : MyViewModel){
        NavHost(
            navController = navController,
            startDestination = Screens.GalleryScreen.route
        ){

            composable(route = Screens.TagListScreen.route) { TagListScreen(vm = vm, navController = navController) }
            composable(route = Screens.AddImageScreen.route) { ImageAddScreen(vm = vm, navController = navController) }
            composable(route = Screens.GalleryScreen.route) { GalleryScreen(vm = vm, navController = navController) }

            composable(route = "${Screens.TagScreen.route}/{id}",
                arguments = listOf(navArgument("id"){type = NavType.IntType})
            ){
                backStackEntry ->
                val tagId = backStackEntry.arguments?.getInt("id") ?: 0
                TagScreen(vm = vm, navController = navController, id = tagId)
            }

            composable(route = "${Screens.ImageScreen.route}/{id}",
                arguments = listOf(navArgument("id"){type = NavType.IntType})
            ){
                    backStackEntry ->
                val imageId = backStackEntry.arguments?.getInt("id") ?: 0
                ImageScreen(vm = vm, NavController = navController, id = imageId)
            }

        }


    }


    // SCREENS
    //___________________________________________________________

    @Composable
    fun GalleryScreen(vm : MyViewModel, navController: NavHostController){
        val images by vm.images.collectAsStateWithLifecycle()
        val imageSearch by vm.imagesSearch.collectAsStateWithLifecycle()
        val searchQuery by vm.searchQuery.collectAsStateWithLifecycle() //

        //filtering tags
        var expanded by remember { mutableStateOf(false) } //currently typing

        //tag list
        val tags = vm.tags.collectAsStateWithLifecycle()

        val filteredTags = tags.value.filter { it.name.contains(searchQuery.split(" ").last(), ignoreCase = true) }

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }


        // Main Container
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Search Field
            TextField(
                value = searchQuery,
                onValueChange = {
                    expanded = true
                    vm.updateSearchQuery(it) //update stored query
                    },
                label = { Text("Search by tags:") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .padding(6.dp)
                    .focusRequester(focusRequester)
            )

            //Text(searchQuery)
            //Text(searchQuery.split(" ").last())

            //Dropdown
            DropdownMenu(
                expanded = expanded && filteredTags.isNotEmpty(),
                onDismissRequest = { expanded = false
                focusRequester.requestFocus()}
            ) {
                filteredTags.forEach { tag ->
                    DropdownMenuItem(
                        onClick = {
                            // Update the search query with the selected tag
                            val currentTags = searchQuery.split(" ").dropLast(1).joinToString(" ")
                            val newQuery = if (currentTags.isEmpty()) tag.name else "$currentTags ${tag.name}"
                            vm.updateSearchQuery(newQuery)
                            expanded = false // Close the dropdown
                            focusRequester.requestFocus()
                        },
                        text = { Text(tag.name) }
                    )
                }
                focusRequester.requestFocus()
            }

            Spacer(Modifier.height(16.dp))

            // Images Returned by Search
            LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(5F).fillMaxWidth()
            ){
                if (searchQuery.isEmpty()){
                    items(images.size){ index ->

                        GalleryItemScreen(vm = vm, navController, images[index])
                    }
                } else{
                    items(imageSearch.size){ index ->

                        GalleryItemScreen(vm = vm, navController, imageSearch[index])
                    }
                }

            }
        }




        // Floating Buttons in the bottom-right corner
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Add Image Button
            FloatingActionButton(
                onClick = { navController.navigate(Screens.AddImageScreen.route) },
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp),  // Set size of the button
                containerColor = Color.DarkGray,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    tint = Color.White,
                    contentDescription = "add image",
                    modifier = Modifier.size(50.dp)
                )
            }

            // Manage Tags Button
            FloatingActionButton(
                onClick = { navController.navigate(Screens.TagListScreen.route) },
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp),  // Set size of the button
                containerColor = Color.DarkGray,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    tint = Color.White,
                    contentDescription = "manage tags",
                    modifier = Modifier.size(40.dp)
                )
            }
        }


    }


    @Composable
    fun GalleryItemScreen(vm: MyViewModel, NavController: NavHostController, image: Image){


        // Container for an Image
        Column(
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .padding(8.dp)
                .clickable {
                    NavController.navigate("${Screens.ImageScreen.route}/${image.id}")
                }

        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(width = 5.dp, Color.Black, shape = RoundedCornerShape(20.dp)),
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(image.image_path)
                    .size(Size.ORIGINAL)
                    .build(),
                contentDescription = null
            )
        }

    }

    @Composable
    fun ImageScreen(vm: MyViewModel, NavController: NavHostController, id: Int){

        vm.getImage(id)

        DisposableEffect(Unit) {
            onDispose {
                vm.clearSelected()
            }
        }

        val image by vm.selectedImage.collectAsStateWithLifecycle()

        if (image == null){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize()
            ) {
                Text(text = "Loading...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        else{
            var image_name by remember { mutableStateOf(image!!.name) }
            var image_path by remember { mutableStateOf(image!!.image_path) }
            // Main Container
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // input field
                Row(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    //Image Name
                    TextField(
                        value = image_name,
                        onValueChange = { newName ->
                            image_name = newName
                            vm.updateImage((Image(image!!.id, image_name, image_path)))
                        },
                        label = { Text("Image Name:") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .padding(6.dp)
                    )
                    //Delete button
                    Button(
                        onClick = {
                            vm.deleteImage((Image(image!!.id, image_name, image_path)))
                            NavController.navigate(Screens.GalleryScreen.route)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Transparent)


                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = Color.White,
                            contentDescription = "Add Tag",
                            modifier = Modifier.size(40.dp)

                        )
                    }


                }
                Spacer(Modifier.height(16.dp))
                //Image Name
                TextField(
                    value = image_path,
                    onValueChange = { newName ->
                        image_path = newName
                        vm.updateImage((Image(image!!.id, image_name, image_path)))
                    },
                    label = { Text("Image Path:") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .padding(6.dp)
                )
                Spacer(Modifier.height(16.dp))

                // Container for an Image
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        .clickable {  } //TODO: ADD moving to Edit Image screen
                        .weight(1F)

                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(width = 5.dp, Color.Black, shape = RoundedCornerShape(20.dp)),
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(image!!.image_path)
                            .size(Size.ORIGINAL)
                            .build(),
                        contentDescription = null
                    )
                }

                Spacer(Modifier.height(16.dp))


                vm.fetchTagsForImage(image!!.id)
                val tags = vm.imageTags.collectAsStateWithLifecycle()


                //Tag List and delete button for tags
                LazyColumn (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
                ){
                    items(tags.value.size){ index ->
                        Row (
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(20.dp))
                                    .clip(shape = RoundedCornerShape(20.dp))
                            ){
                                Text(
                                    text = tags.value[index].name,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                            //delete button
                            Button(onClick = { vm.removeTagFromImage(ImageTags(image!!.id, tags.value[index].id))}) { Icon(
                                imageVector = Icons.Filled.Close,
                                tint = Color.White,
                                contentDescription = "delete Tag",
                                modifier = Modifier.size(20.dp)

                            )}
                        }

                    }

                }

            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun TagListScreen(vm : MyViewModel, navController: NavHostController){
        var tag_name by remember { mutableStateOf("") }
        val tags by vm.tags.collectAsStateWithLifecycle()

        // Main Container
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // input field
            Row (
                modifier = Modifier.fillMaxWidth()
            )
            {
                TextField(
                    value = tag_name,
                    onValueChange = { newName -> tag_name = newName },
                    label = { Text("Add Tag:") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .padding(6.dp)
                )
                //Add tag
                Button(
                    onClick = {
                        vm.addTag(Tag(0, tag_name))
                        tag_name = ""
                              },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Transparent)


                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = Color.White,
                        contentDescription = "Add Tag",
                        modifier = Modifier.size(40.dp)

                    )
                }

            }


            Spacer(Modifier.height(16.dp))

            // all tags
            FlowRow (Modifier.weight(5F).padding(8.dp)){
                tags.forEach{ tag ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(20.dp))
                            .clip(shape = RoundedCornerShape(20.dp))
                            .clickable { navController.navigate("${Screens.TagScreen.route}/${tag.id}") }
                    ){
                        Text(
                            text = tag.name,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)

                        )
                    }

                }
            }
        }
    }

    @Composable
    fun TagScreen(vm : MyViewModel, navController: NavHostController, id : Int){


        vm.getTag(id)


        DisposableEffect(Unit) {
            onDispose {
                vm.clearSelected()
            }
        }

        val tag by vm.selectedTag.collectAsStateWithLifecycle()

        if (tag == null){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize()
            ) {
                Text(text = "Loading...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        else{
            var tag_name by remember { mutableStateOf(tag!!.name) }
            // Main Container
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // input field
                Row(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    TextField(
                        value = tag_name,
                        onValueChange = { newName ->
                            tag_name = newName
                            vm.updateTag(Tag(tag!!.id, tag_name))
                        },
                        label = { Text("Tag name:") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .padding(6.dp)
                    )
                    //Add tag
                    Button(
                        onClick = {
                            vm.deleteTag(Tag(tag!!.id, tag_name))
                            navController.navigate(Screens.TagListScreen.route)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Transparent)


                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = Color.White,
                            contentDescription = "Add Tag",
                            modifier = Modifier.size(40.dp)

                        )
                    }

                }
            }
        }
    }


    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ImageAddScreen(vm: MyViewModel, navController : NavHostController) {
        var image_path by remember { mutableStateOf("") }
        var image_name by remember { mutableStateOf("") }
        var tag_name by remember { mutableStateOf("") }

        var expanded by remember { mutableStateOf(false) }
        var image_tags : MutableList<Tag> by remember { mutableStateOf(mutableListOf()) }

        val tags = vm.tags.collectAsStateWithLifecycle()

        val filteredTags = tags.value.filter { it.name.contains(tag_name, ignoreCase = true) }

        // File Picker
        val context = LocalContext.current
        val getContent = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                image_path = it.toString()
            }
        }




        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            Spacer(Modifier.height(16.dp))

            // Main Container
            Row (
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Image Path text field
                TextField(
                    value = image_path,
                    onValueChange = { newValue -> image_path = newValue },
                    label = { Text("URL or Local storage path:") },
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(6.dp)
                )

                //Browse files button
                Button(onClick = {
                        getContent.launch("*/*")
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Transparent)


                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        tint = Color.White,
                        contentDescription = "Browse Files",
                        modifier = Modifier.size(40.dp)

                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Container for an Image
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .clickable {  } //TODO: ADD moving to Edit Image screen
                    .weight(1F)

            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 5.dp, Color.Black, shape = RoundedCornerShape(20.dp)),
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(image_path)
                        .size(Size.ORIGINAL)
                        .build(),
                    contentDescription = null
                )
            }

            Spacer(Modifier.height(16.dp))


            // IMAGE SPECS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // IMAGE NAME
                TextField(
                    value = image_name,
                    onValueChange = { newValue -> image_name = newValue },
                    label = { Text("Image name:") },
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .padding(6.dp)
                        .fillMaxWidth()
                )

                //TAGS
                Row (
                    modifier = Modifier.fillMaxWidth()
                ){
                    TextField(
                        value = tag_name,
                        onValueChange = { newValue ->
                            tag_name = newValue
                            expanded=true
                                        },
                        label = { Text("Enter tags:") },
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 1,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .padding(6.dp)
                    )


                    //Dropdown Menu to help pick tags
                    DropdownMenu(
                        expanded = expanded && filteredTags.isNotEmpty(),
                        onDismissRequest = {expanded = false}
                    ){
                        filteredTags.forEach { tag ->
                            DropdownMenuItem(
                                onClick = {
                                    image_tags.add(tag)
                                    tag_name = "" // Update search query with the clicked tag
                                    expanded = false // Hide dropdown after selecting a tag
                                },
                                text = {Text(tag.name)}
                            )
                        }
                    }
                }
                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ){
                    image_tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(20.dp))
                                    .clip(shape = RoundedCornerShape(20.dp))
                            ){
                                Text(
                                    text = tag.name,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)

                                )
                            }
                    }
                }


                // SUBMIT IMAGE
                Button(onClick = {
                    val img = Image(0, image_name, image_path)

                    CoroutineScope(Dispatchers.IO).launch {
                        val id = vm.addImage(img)

                        //insert tags after getting id
                        image_tags.forEach{ tag ->
                            vm.addTagToImage(ImageTags(id.toInt(), tag.id))
                        }



                        //Go back to gallery
                        withContext(Dispatchers.Main){
                            navController.navigate(Screens.GalleryScreen.route)
                        }
                    }

                }) { Text("Add Image") }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}