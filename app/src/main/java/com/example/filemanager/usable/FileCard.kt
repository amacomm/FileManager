package com.example.filemanager.usable

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_STREAM
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


fun bytesToHumanReadableSize(bytes: Double) = when {
    bytes >= 1 shl 30 -> "%.1f GB".format(bytes / (1 shl 30))
    bytes >= 1 shl 20 -> "%.1f MB".format(bytes / (1 shl 20))
    bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
    else -> "$bytes bytes"
}

fun folderSize(file: File): Long
{
    var size: Long = 0
    if(file.isFile)
        return file.length()
    file.listFiles().forEach {
        if(it.isFile)
            size += it.length()
        else
            size+= folderSize(it)
    }
    return size
}


@SuppressLint("SimpleDateFormat", "IntentReset")
@Composable
fun FileCard(activity: ComponentActivity, file: File,
             action: ()->Unit ){
    val imageExt = arrayOf("png", "jpg", "jpeg", "bmp", "gif", "ico")
    val textExt = arrayOf("txt", "doc", "docx", "pdf", "html", "djvu")
    val videoExt = arrayOf("mp4", "avi", "mkv", "wmv", "flv", "mpeg")
    val audioExt = arrayOf("mp3", "wav", "midi", "aac", "flac")
    val archiveExt = arrayOf("zip", "rar", "7z", "iso", "gzip")
    val attributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
    Card(modifier = Modifier
        .clickable {
            when {
                file.isDirectory -> {
                    action()
                }
                else -> {
                    try {
                        val mime = MimeTypeMap
                            .getSingleton()
                            .getMimeTypeFromExtension(file.extension)
                        val intent = Intent()
                            .setAction(ACTION_VIEW)
                            .setType(mime)
                            .setData(
                                FileProvider.getUriForFile(
                                    activity,
                                    activity
                                        .getApplicationContext()
                                        .getPackageName() + ".provider",
                                    file
                                )
                            )
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        activity.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast
                            .makeText(activity, "Cannot open the file", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        .padding(8.dp, 4.dp)
        .fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
            file.hashCode()
            Icon(
                    when{
                        (file.isDirectory) -> Icons.Default.Folder
                        (file.extension in textExt) -> Icons.Default.TextSnippet
                        (file.extension in imageExt) -> Icons.Default.Image
                        (file.extension in videoExt) -> Icons.Default.VideoFile
                        (file.extension in audioExt) -> Icons.Default.AudioFile
                        (file.extension in archiveExt) -> Icons.Default.Archive
                        else -> Icons.Default.InsertDriveFile
                    }
                ,
                contentDescription = "ContentIcon",
            modifier = Modifier
                .size(45.dp)
                .padding(8.dp, 0.dp),
            tint = MaterialTheme.colorScheme.primary
            )
            Text(text = file.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge)
            Column(horizontalAlignment = Alignment.End) {
                var size by remember { mutableStateOf(0L) }
//                Thread(Runnable {
//                    size = folderSize(file)
//                }).start()
                Text(text = bytesToHumanReadableSize(folderSize(file).toDouble()))

                val output = SimpleDateFormat("dd MMM yyyy").format(attributes.creationTime().toMillis())
                Text(text = output)
            }
            if(!file.isDirectory)
            IconButton(onClick = {
                try {
                    val mime = MimeTypeMap
                        .getSingleton()
                        .getMimeTypeFromExtension(file.extension)
                    val intent = Intent()
                        .setAction(ACTION_SEND)
                        .putExtra(
                            EXTRA_STREAM,
                            FileProvider.getUriForFile(
                                activity,
                                activity
                                    .getApplicationContext()
                                    .getPackageName() + ".provider",
                                file
                            )
                        )
                        .setType(mime)
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    activity.startActivity(
                        Intent.createChooser(
                            intent,
                            "Поделиться файлом"
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    Toast
                        .makeText(activity, "Cannot share the file", Toast.LENGTH_SHORT)
                        .show()
                }
            }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
            }
        }
    }
}