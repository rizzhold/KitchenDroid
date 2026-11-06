package com.kitchendroid.app

import java.io.File

class KitchenTool(private val ctx: android.content.Context, private val ws: WorkspaceManager) {

    private fun run(cmd: List<String>, workDir: File): String {
        val pb = java.lang.ProcessBuilder(cmd)
        pb.directory(workDir)
        pb.redirectErrorStream(true)
        val proc = pb.start()
        return proc.inputStream.bufferedReader().use { it.readText() }
    }

    fun unpackZip(zipName: String): String {
        ws.copyToolsFromAssets()
        val kitchenBin = ws.toolPath("kd_tool")
        val zipFile = File(ws.workspacePath(), zipName)
        val dest = File(ws.workspacePath(), "module")
        dest.mkdirs()
        return run(listOf(kitchenBin, "unzip", zipFile.absolutePath, dest.absolutePath), File(ws.workspacePath()))
    }

    fun extractImg(imgName: String): String {
        ws.copyToolsFromAssets()
        val kitchenBin = ws.toolPath("kd_tool")
        val imgFile = File(ws.workspacePath(), imgName)
        return run(listOf(kitchenBin, "extract", imgFile.absolutePath), File(ws.workspacePath()))
    }

    fun editProp(propName: String, newContent: String): String {
        ws.writeText(propName, newContent)
        return ctx.getString(R.string.msg_saved, propName)
    }

    fun readProp(propName: String): String {
        return ws.readText(propName)
    }

    fun listWorkspace(): List<String> {
        return ws.listWorkspace()
    }

    fun deleteItem(name: String): Boolean {
        return ws.deleteItem(name)
    }
}
