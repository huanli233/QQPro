package momoi.plugin.apkmixin.utils

import java.io.File

fun File.child(name: String) = File(this, name)

fun File.createIfNotExists() {
    if (!exists()) createNewFile()
}

fun File.mkdirsIfNotExists() {
    if (!exists()) mkdirs()
}

fun File.childFileOrCreate(name: String) =
    File(this, name).apply {
        parentFile.mkdirsIfNotExists()
        createIfNotExists()
    }