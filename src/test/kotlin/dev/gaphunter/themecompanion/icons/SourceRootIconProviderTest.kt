package dev.gaphunter.themecompanion.icons

import com.intellij.icons.AllIcons
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.jps.model.java.JavaSourceRootType

/**
 * Real platform test: marks actual directories as source/test/resource
 * roots on the fixture's module (the same mechanism a real project uses),
 * then asserts the provider never resolves a null/empty icon for them. This
 * is the exact regression the competitor shipped ("Marked directories as
 * src or test are not visible anymore").
 */
class SourceRootIconProviderTest : BasePlatformTestCase() {

    private val provider = SourceRootIconProvider()

    fun testSourceRootNeverResolvesToNullIcon() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("src")
        PsiTestUtil.addSourceRoot(myFixture.module, dir, JavaSourceRootType.SOURCE)

        val icon = provider.getIcon(dir, 0, project)

        assertNotNull("a marked source root must never resolve to a null icon", icon)
        assertSame(AllIcons.Modules.SourceRoot, icon)
    }

    fun testTestSourceRootNeverResolvesToNullIcon() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("test")
        PsiTestUtil.addSourceRoot(myFixture.module, dir, JavaSourceRootType.TEST_SOURCE)

        val icon = provider.getIcon(dir, 0, project)

        assertNotNull("a marked test root must never resolve to a null icon", icon)
        assertSame(AllIcons.Modules.TestRoot, icon)
    }

    fun testResourcesRootNeverResolvesToNullIcon() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("resources")
        PsiTestUtil.addSourceRoot(myFixture.module, dir, JavaResourceRootType.RESOURCE)

        val icon = provider.getIcon(dir, 0, project)

        assertNotNull("a marked resources root must never resolve to a null icon", icon)
        assertSame(AllIcons.Modules.ResourcesRoot, icon)
    }

    fun testTestResourcesRootNeverResolvesToNullIcon() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("testResources")
        PsiTestUtil.addSourceRoot(myFixture.module, dir, JavaResourceRootType.TEST_RESOURCE)

        val icon = provider.getIcon(dir, 0, project)

        assertNotNull("a marked test-resources root must never resolve to a null icon", icon)
        assertSame(AllIcons.Modules.TestResourcesRoot, icon)
    }

    fun testUnmarkedDirectoryIsNotClaimedByThisProvider() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("notes")

        assertNull(provider.getIcon(dir, 0, project))
    }

    fun testRegularFileIsNeverClaimed() {
        myFixture.tempDirFixture.createFile("Foo.txt", "hi")
        val file = myFixture.tempDirFixture.getFile("Foo.txt")!!

        assertNull(provider.getIcon(file, 0, project))
    }

    fun testNullProjectIsHandledSafely() {
        val dir = myFixture.tempDirFixture.findOrCreateDir("src2")
        PsiTestUtil.addSourceRoot(myFixture.module, dir, JavaSourceRootType.SOURCE)

        assertNull(provider.getIcon(dir, 0, null))
    }
}
