package org.hugopalma.domaintranslator.dictionary

import com.intellij.openapi.components.Service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.ktor.util.*
import java.io.File

@Service(Service.Level.PROJECT)
class DictionaryService {
    private val dictionaries: MutableMap<String, Dictionary> = mutableMapOf()

    fun getDictionary(element: PsiElement): Dictionary? {
        val module: Module = ModuleUtilCore.findModuleForPsiElement(element) ?: return null
        val moduleName = module.name
        val dictionaryFile = findFileInContentRoots(module) ?: return null
        val dictionary = dictionaries[moduleName]

        if (dictionary != null && dictionary.timestamp >= dictionaryFile.timeStamp) {
            return dictionary
        }

        dictionaries[moduleName] = Dictionary(parseCsvToMap(dictionaryFile.path))
        return dictionaries[moduleName]
    }

    private fun findFileInContentRoots(module: Module): VirtualFile? {
        val contentRoots = ModuleRootManager.getInstance(module).contentRoots

        for (root in contentRoots) {
            val file = root.findChild("dictionary.csv")
            if (file != null && !file.isDirectory) {
                return file
            }
        }

        return null;
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
