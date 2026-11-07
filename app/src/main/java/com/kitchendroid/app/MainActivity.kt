package com.kitchendroid.app

import android.provider.OpenableColumns
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.io.File

enum class Screen { Home, Workspace, Settings, Edit }

class MainActivity : ComponentActivity() {

    private val ws by lazy { WorkspaceManager(this) }
    private val kt by lazy { KitchenTool(this, ws) }

    private var currentScreen by mutableStateOf(Screen.Home)
    private var files by mutableStateOf<List<String>>(emptyList())
    private var message by mutableStateOf("")
    private var newFileName by mutableStateOf("")
    private var editingFile by mutableStateOf<String?>(null)
    private var propText by mutableStateOf("")
    private var lang by mutableStateOf("en")

    private val openFile = registerForActivityResult(OpenDocument()) { uri: Uri? ->
        if (uri != null) importUri(uri)
    }

    private fun queryDisplayName(uri: Uri): String {
        try {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) {
                    val n = c.getString(0)
                    if (!n.isNullOrBlank()) return n
                }
            }
        } catch (e: Exception) {
        }
        return uri.lastPathSegment?.substringAfterLast('/') ?: "imported_file"
    }

    private fun importUri(uri: Uri) {
        try {
            val input = contentResolver.openInputStream(uri) ?: return
            val displayName = queryDisplayName(uri)
            val dest = File(ws.workspacePath(), displayName)
            input.use { src ->
                dest.outputStream().use { out -> src.copyTo(out) }
            }
            message = getString(R.string.msg_imported, displayName)
            refresh()
            currentScreen = Screen.Workspace
        } catch (e: Exception) {
            message = getString(R.string.msg_import_failed, e.message ?: "unknown")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lang = LocaleManager.getLanguage(this)
        LocaleManager.applyLocale(this, lang)
        // copy tools di background supaya UI tidak macet
        Thread { try { ws.copyToolsFromAssets() } catch (e: Exception) { } }.start()
        refresh()

        setContent {
            KitchenDroidTheme {
                Scaffold(
                    bottomBar = {
                        KitchenBottomNav(
                            selected = currentScreen,
                            onClick = { screen -> currentScreen = screen }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentScreen) {
                            Screen.Home -> HomeScreen()
                            Screen.Workspace -> WorkspaceScreen()
                            Screen.Settings -> SettingsScreen()
                            Screen.Edit -> {
                                val f = editingFile
                                if (f != null) EditPropScreen(f) else HomeScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun refresh() {
        files = kt.listWorkspace()
    }

    // ==================== HOME ====================
    @Composable
    private fun HomeScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = getString(R.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getString(R.string.subtitle_workspace),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Project card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = getString(R.string.card_project), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { /* select project */ }, shape = RoundedCornerShape(12.dp)) {
                            Text(getString(R.string.btn_select_project))
                        }
                        Button(onClick = { /* new project */ }, shape = RoundedCornerShape(12.dp)) {
                            Text(getString(R.string.btn_new_project))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = getString(R.string.card_actions), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { openFile.launch(arrayOf("*/*")) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(getString(R.string.btn_import_sdcard))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { currentScreen = Screen.Workspace },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(getString(R.string.btn_open_workspace))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = getString(R.string.card_info), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getString(R.string.info_workspace_path) + " " + ws.workspacePath(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ==================== WORKSPACE ====================
    @Composable
    private fun WorkspaceScreen() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = getString(R.string.workspace_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val name = newFileName
                        if (name.isNotBlank()) {
                            ws.writeText(name, "")
                            newFileName = ""
                            refresh()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = newFileName.isNotBlank()
                ) {
                    Text(getString(R.string.btn_create_prop))
                }
                OutlinedButton(
                    onClick = { refresh() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(getString(R.string.msg_refreshed))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newFileName,
                onValueChange = { newFileName = it },
                label = { Text("nama file .prop") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (files.isEmpty()) {
                Text(text = getString(R.string.workspace_empty), style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(files, key = { it }) { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { /* pilih file */ },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f).padding(start = 12.dp)
                            )
                            Row(horizontalArrangement = Arrangement.End) {
                                if (name.endsWith(".prop")) {
                                    Button(
                                        onClick = {
                                            editingFile = name
                                            propText = kt.readProp(name)
                                            currentScreen = Screen.Edit
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                    ) {
                                        Text(getString(R.string.btn_edit))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Button(
                                    onClick = {
                                        kt.deleteItem(name)
                                        refresh()
                                        message = getString(R.string.msg_deleted, name)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Text(getString(R.string.btn_delete))
                                }
                            }
                        }
                    }
                }
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // ==================== EDIT PROP ====================
    @Composable
    private fun EditPropScreen(fileName: String) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = getString(R.string.edit_title, fileName),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = propText,
                onValueChange = { propText = it },
                label = { Text("Isi file") },
                modifier = Modifier.fillMaxWidth().height(220.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { currentScreen = Screen.Workspace }, shape = RoundedCornerShape(12.dp)) {
                    Text(getString(R.string.btn_cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        kt.editProp(fileName, propText)
                        refresh()
                        currentScreen = Screen.Workspace
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(getString(R.string.btn_save))
                }
            }
        }
    }

    // ==================== SETTINGS ====================
    @Composable
    private fun SettingsScreen() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = getString(R.string.settings_title), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = getString(R.string.pref_language), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val langs = listOf(
                "en" to getString(R.string.lang_english),
                "id" to getString(R.string.lang_indonesian)
            )

            for ((code, label) in langs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (lang != code) {
                                lang = code
                                LocaleManager.saveLanguage(this@MainActivity, code)
                                LocaleManager.applyLocale(this@MainActivity, code)
                                recreate()
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (lang == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (lang == code) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Divider()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = getString(R.string.about_title), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getString(R.string.about_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // ==================== BOTTOM NAV ====================
    @Composable
    private fun KitchenBottomNav(selected: Screen, onClick: (Screen) -> Unit) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text(getString(R.string.nav_home)) },
                selected = selected == Screen.Home,
                onClick = { onClick(Screen.Home) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text(getString(R.string.nav_workspace)) },
                selected = selected == Screen.Workspace,
                onClick = { onClick(Screen.Workspace) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text(getString(R.string.nav_settings)) },
                selected = selected == Screen.Settings,
                onClick = { onClick(Screen.Settings) }
            )
        }
    }
}
