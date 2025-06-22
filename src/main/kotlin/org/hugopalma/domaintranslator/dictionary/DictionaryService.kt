package org.hugopalma.domaintranslator.dictionary

import com.google.common.cache.CacheBuilder
import com.intellij.DynamicBundle
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.ktor.util.*
import org.hugopalma.domaintranslator.settings.Settings
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

@Service(Service.Level.PROJECT)
class DictionaryService {
    private val dictionaries: MutableMap<String, Dictionary> = mutableMapOf()
    private var lastRefreshTimestamp = System.currentTimeMillis()
    private val moduleDictionaryFileCache = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build<String, VirtualFile>()

    @Synchronized
    fun getDictionary(element: PsiElement): Dictionary? {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(element) ?: return null
        val dictionaryFile = moduleDictionaryFileCache.getIfPresent(module.name) ?: run {
            val newFile = findDictionaryFile(module)
            if (newFile != null) {
                moduleDictionaryFileCache.put(module.name, newFile)
                lastRefreshTimestamp = 0
            }
            newFile
        }
        if (dictionaryFile == null) {
            return null
        }

        val moduleName = module.name
        val dictionary = dictionaries[moduleName]

        // refresh file from filesystem every 10 seconds at least
        if (dictionary != null && System.currentTimeMillis().minus(lastRefreshTimestamp) > 10000) {
            dictionaryFile.refresh(true, false)
            lastRefreshTimestamp = System.currentTimeMillis()
        }

        if (dictionary != null && dictionary.timestamp >= dictionaryFile.timeStamp) {
            return dictionary
        }

        dictionaries[moduleName] = Dictionary(parseCsvToMap(dictionaryFile.path), dictionaryFile.timeStamp)
        return dictionaries[moduleName]
    }

    private fun findDictionaryFile(module: Module): VirtualFile? {
        val rootModule = findRootModuleByFolderStructure(module.project, module) ?: module
        val contentRoots = ModuleRootManager.getInstance(rootModule).contentRoots
        val pluginSettings = ApplicationManager.getApplication().getService(Settings::class.java).state

        val filePath: MutableList<String> = mutableListOf()
        if (pluginSettings.dictionaryFile != null) {
            if (Path(pluginSettings.dictionaryFile!!).isAbsolute) {
                return LocalFileSystem.getInstance().findFileByPath(pluginSettings.dictionaryFile!!)
            }

            filePath.add(pluginSettings.dictionaryFile!!)
        } else {
            var language: String
            if (pluginSettings.useSystemLanguage) {
                language = DynamicBundle.getLocale().language
            } else {
                language = pluginSettings.language
            }
            filePath.add("dictionary_${language}.csv")
            filePath.add("dictionary.csv")
        }

        for (root in contentRoots) {
            for (currentFile in filePath) {
                val file = root.findChild(currentFile)
                if (file != null && !file.isDirectory) {
                    return file
                }
            }
        }

        return null
    }

    private fun findRootModuleByFolderStructure(project: Project, module: Module): Module? {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules

        var currentRootPath = ModuleRootManager.getInstance(module)
            .contentRoots
            .firstOrNull()
            ?.path ?: return null

        var rootModule: Module = module

        for (candidateModule in modules) {
            val candidatePath = ModuleRootManager.getInstance(candidateModule)
                .contentRoots
                .firstOrNull()
                ?.path ?: continue

            if (File(currentRootPath).startsWith(File(candidatePath))) {
                rootModule = candidateModule
                currentRootPath = candidatePath
            }
        }

        return rootModule
    }

    private fun parseCsvToMap(filePath: String): Map<String, String> {
        val map = mutableMapOf<String, String>()

        File(filePath).forEachLine { line ->
            val parts = line.split(";")
            if (parts.size == 2) {
                val key = parts[0].trim().toLowerCasePreservingASCIIRules()
                val value = parts[1].trim()
                map[key] = value
            }
        }

        return map
    }
}
