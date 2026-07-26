package dev.gaphunter.themecompanion.theme

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.impl.EditorColorsSchemeImpl
import com.intellij.openapi.editor.colors.impl.EmptyColorScheme
import com.intellij.openapi.util.JDOMUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * `EditorColorsManager`'s handling of the `bundledColorScheme` extension
 * point is skipped under `ApplicationManager.getApplication().isUnitTestMode()`
 * (confirmed by decompiling `EditorColorsManagerImpl`), so looking the scheme
 * up by name through that manager is not a viable test inside a platform test
 * fixture. Instead, this test runs the exact resource this plugin ships
 * through the platform's own scheme deserializer
 * (`AbstractColorsScheme.readExternal`, the same method a real IDE run uses
 * to load any bundled scheme) and checks it parses without exceptions and
 * actually carries our color overrides.
 */
class GapHunterMonokaiSchemeTest : BasePlatformTestCase() {

    private fun loadScheme(): EditorColorsSchemeImpl {
        val resource = javaClass.getResourceAsStream("/schemes/GapHunterMonokai.xml")
            ?: error("bundled scheme resource is missing from the plugin classpath")
        val scheme = EditorColorsSchemeImpl(EmptyColorScheme.getEmptyScheme())
        scheme.readExternal(JDOMUtil.load(resource))
        return scheme
    }

    fun testSchemeParsesWithoutExceptionsAndKeepsItsDeclaredName() {
        val scheme = loadScheme()

        assertEquals("Gap Hunter Monokai", scheme.name)
    }

    fun testEditorBackgroundIsExplicitlyDark() {
        val scheme = loadScheme()

        val text = scheme.getAttributes(HighlighterColors.TEXT)
        assertNotNull(text)
        val background = text!!.backgroundColor
        assertNotNull("editor background must be explicitly set, not left null", background)

        // High-contrast dark background: each channel well below mid-gray.
        assertTrue(background!!.red < 0x40)
        assertTrue(background.green < 0x40)
        assertTrue(background.blue < 0x40)
    }

    fun testForegroundIsHighContrastAgainstBackground() {
        val scheme = loadScheme()
        val text = scheme.getAttributes(HighlighterColors.TEXT)!!

        val fg = text.foregroundColor!!
        val bg = text.backgroundColor!!
        val contrast = (fg.red + fg.green + fg.blue) - (bg.red + bg.green + bg.blue)

        assertTrue("foreground must be clearly lighter than background", contrast > 300)
    }

    fun testKeywordStringCommentAndNumberColorsAreEachDistinct() {
        val scheme = loadScheme()

        val keyword = scheme.getAttributes(DefaultLanguageHighlighterColors.KEYWORD)!!.foregroundColor
        val string = scheme.getAttributes(DefaultLanguageHighlighterColors.STRING)!!.foregroundColor
        val comment = scheme.getAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT)!!.foregroundColor
        val number = scheme.getAttributes(DefaultLanguageHighlighterColors.NUMBER)!!.foregroundColor

        assertNotNull(keyword)
        assertNotNull(string)
        assertNotNull(comment)
        assertNotNull(number)

        val colors = setOf(keyword, string, comment, number)
        assertEquals("keyword/string/comment/number must each be a distinct color", 4, colors.size)
    }

    fun testCommentIsClearlyMutedComparedToDefaultText() {
        val scheme = loadScheme()

        val comment = scheme.getAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT)!!.foregroundColor!!
        val text = scheme.getAttributes(HighlighterColors.TEXT)!!.foregroundColor!!

        val commentBrightness = comment.red + comment.green + comment.blue
        val textBrightness = text.red + text.green + text.blue

        assertTrue("comments should read as muted, not as bright as plain text", commentBrightness < textBrightness)
    }

    fun testFunctionAndClassNameColorsAreEachDistinctFromKeyword() {
        val scheme = loadScheme()

        val keyword = scheme.getAttributes(DefaultLanguageHighlighterColors.KEYWORD)!!.foregroundColor!!
        val function = scheme.getAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)!!.foregroundColor!!
        val className = scheme.getAttributes(DefaultLanguageHighlighterColors.CLASS_NAME)!!.foregroundColor!!

        assertTrue(function != keyword)
        assertTrue(className != keyword)
        assertTrue(className != function)
    }
}
