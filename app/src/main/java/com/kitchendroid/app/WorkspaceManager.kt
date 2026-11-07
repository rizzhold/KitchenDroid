package com.kitchendroid.app

import android.content.Context
import android.system.Os
import android.system.OsConstants
import java.io.File

class WorkspaceManager(private val context: Context) {

    private val workspaceDir: File
        get() = File(context.filesDir, "workspace").apply { mkdirs() }

    private val toolsDir: File
        get() = File(context.filesDir, "tools").apply { mkdirs() }

    fun workspacePath(): String = workspaceDir.absolutePath

    fun listWorkspace(): List<String> {
        return workspaceDir.list()?.sorted() ?: emptyList()
    }

    fun readText(name: String): String {
        return File(workspaceDir, name).readText()
    }

    fun writeText(name: String, text: String) {
        File(workspaceDir, name).writeText(text)
    }

    private fun isSymlink(file: File): Boolean {
        return try {
            OsConstants.S_ISLNK(Os.lstat(file.absolutePath).st_mode)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteItem(name: String): Boolean {
        val target = File(workspaceDir, name)
        return when {
            isSymlink(target) -> target.delete() // hapus symlink saja, jangan ikuti target
            !target.exists() -> true
            target.isDirectory -> deleteRecursively(target)
            else -> target.delete()
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (!isSymlink(file) && file.isDirectory) {
            file.listFiles()?.forEach { deleteRecursively(it) }
        }
        return file.delete()
    }

    /** Copy binary tools dari assets/tools ke internal app data dan beri izin eksekusi. */
    fun copyToolsFromAssets() {
        val assetNames = context.assets.list("tools") ?: emptyArray()
        for (name in assetNames) {
            val out = File(toolsDir, name)
            if (!out.exists()) {
                context.assets.open("tools/$name").use { input ->
                    out.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            out.setReadable(true, false)
            out.setExecutable(true, false)
        }
    }

    fun toolPath(name: String): String {
        return File(toolsDir, name).absolutePath
    }
}
