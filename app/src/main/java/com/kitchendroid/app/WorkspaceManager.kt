package com.kitchendroid.app

import android.content.Context
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

    fun deleteItem(name: String): Boolean {
        val target = File(workspaceDir, name)
        return if (target.exists()) {
            if (target.isSymbolicLink) {
                target.delete() // hapus symlink saja, tidak mengikuti target
            } else if (target.isDirectory) {
                deleteRecursively(target)
            } else {
                target.delete()
            }
        } else {
            true
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        val files = file.listFiles() ?: return file.delete()
        files.forEach { deleteRecursively(it) }
        return file.delete()
    }

    /** Copy binary tools dari assets ke internal app data. Rename 'dna' jadi 'kd_tool'. */
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
                out.setReadable(true, false)
                out.setExecutable(true, false)
            }
        }

        // Rebrand: binary asli bernama 'dna' dipanggil sebagai 'kd_tool' di seluruh kode
        val asli = File(toolsDir, "dna")
        val rebrand = File(toolsDir, "kd_tool")
        if (asli.exists() && !rebrand.exists()) {
            asli.renameTo(rebrand)
            rebrand.setExecutable(true, false)
        }
    }

    fun toolPath(name: String): String {
        return File(toolsDir, name).absolutePath
    }
}
