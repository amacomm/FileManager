package com.example.filemanager.pages

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.filemanager.usable.FileCard
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

fun sortFun(){

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerPage(activity: ComponentActivity, file: File){
    val sortState = arrayOf("имени ↓", "имени ↑", "дате ↓", "дате ↑", "размеру ↓", "размеру ↑", "расширению ↓", "расширению ↑")
    var expanded by remember { mutableStateOf(false) }
    var currentState by remember { mutableStateOf(0) }
    var currentFile by remember { mutableStateOf(file) }
    var files by remember { mutableStateOf(currentFile.listFiles()) }

    files.sort()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = currentFile.absolutePath) },
                navigationIcon = {
                    if(currentFile.path != file.path)
                        IconButton(onClick = {
                            currentFile = currentFile.parentFile as File
                            files = currentFile.listFiles()
                            when{
                                (currentState == 0) -> files.sort()
                                (currentState == 1) -> files.sortDescending()
                                (currentState == 2) -> files.sortBy{Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                                (currentState == 3) -> files.sortByDescending { Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                                (currentState == 4) -> files.sortBy { it.length()}
                                (currentState == 5) -> files.sortByDescending { it.length()}
                                (currentState == 6) -> files.sortBy { it.extension}
                                (currentState == 7) -> files.sortByDescending { it.extension}
                            }
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
                }
            )
        }
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 60.dp, 0.dp, 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сортирвка по",
                    modifier = Modifier.weight(1f)
                )
                Text(text = sortState[currentState])
                Column() {
                    IconButton(
                        onClick = {
                            expanded = !expanded
                        }
                    ) {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sortState.forEach {
                            DropdownMenuItem(onClick = { currentState = sortState.indexOf(it)
                                when{
                                    (currentState == 0) -> files.sort()
                                    (currentState == 1) -> files.sortDescending()
                                    (currentState == 2) -> files.sortBy{Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                                    (currentState == 3) -> files.sortByDescending { Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                                    (currentState == 4) -> files.sortBy { it.length()}
                                    (currentState == 5) -> files.sortByDescending { it.length()}
                                    (currentState == 6) -> files.sortBy { it.extension}
                                    (currentState == 7) -> files.sortByDescending { it.extension}
                                }
                                expanded = false
                            },
                                text = { Text(it) })
                        }
                    }
                }

            }
            files.forEach {
                FileCard(activity, file = it) {
                    currentFile = it
                    files = currentFile.listFiles()
                    when{
                        (currentState == 0) -> files.sort()
                        (currentState == 1) -> files.sortDescending()
                        (currentState == 2) -> files.sortBy{Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                        (currentState == 3) -> files.sortByDescending { Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime()}
                        (currentState == 4) -> files.sortBy { it.length()}
                        (currentState == 5) -> files.sortByDescending { it.length()}
                        (currentState == 6) -> files.sortBy { it.extension}
                        (currentState == 7) -> files.sortByDescending { it.extension}
                    }
                }
            }
        }
    }
}