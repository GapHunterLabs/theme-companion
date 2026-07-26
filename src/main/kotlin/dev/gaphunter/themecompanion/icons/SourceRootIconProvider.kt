package dev.gaphunter.themecompanion.icons

import com.intellij.icons.AllIcons
import com.intellij.ide.FileIconProvider
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.java.JavaResourceRootType
import javax.swing.Icon

/**
 * The plugin exists in response to a real regression reported against a paid
 * competitor: directories marked as source/test roots stopped showing their
 * icon after an update ("Folders no longer have icons"). This provider never
 * depends on this plugin's own icon resources loading correctly at runtime —
 * it only ever resolves icons from [AllIcons], a hard platform dependency
 * that is always present — and it never returns null for a directory that is
 * genuinely registered as a source/test/resource root.
 */
class SourceRootIconProvider : FileIconProvider {

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (project == null || project.isDisposed || !file.isDirectory) return null

        return try {
            findMarkedSourceFolder(project, file)?.let(::iconFor)
        } catch (e: Exception) {
            null
        }
    }

    private fun findMarkedSourceFolder(project: Project, file: VirtualFile): SourceFolder? {
        for (module in ModuleManager.getInstance(project).modules) {
            if (module.isDisposed) continue
            for (contentEntry in ModuleRootManager.getInstance(module).contentEntries) {
                for (sourceFolder in contentEntry.sourceFolders) {
                    if (sourceFolder.file == file) return sourceFolder
                }
            }
        }
        return null
    }

    private fun iconFor(sourceFolder: SourceFolder): Icon {
        val rootType = sourceFolder.rootType
        val isResource = rootType is JavaResourceRootType
        val isTest = rootType.isForTests

        return when {
            isResource && isTest -> AllIcons.Modules.TestResourcesRoot
            isResource -> AllIcons.Modules.ResourcesRoot
            isTest -> AllIcons.Modules.TestRoot
            else -> AllIcons.Modules.SourceRoot
        }
    }
}
