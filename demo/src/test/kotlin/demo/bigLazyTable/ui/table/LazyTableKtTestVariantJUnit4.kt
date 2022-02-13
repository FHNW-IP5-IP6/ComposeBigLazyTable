package demo.bigLazyTable.ui.table

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Surface
import org.junit.Test
import org.junit.Rule

// TODO: java.lang.NoSuchMethodError: <init>
//  class java.util.Collections$SingletonList cannot be cast to class androidx.compose.ui.text.AnnotatedString
//  (java.util.Collections$SingletonList is in module java.base of loader 'bootstrap'; androidx.compose.ui.text.AnnotatedString
//  is in unnamed module of loader 'app')
//  java.lang.ClassCastException: class java.util.Collections$SingletonList cannot be cast to class androidx.compose.ui.text.AnnotatedString
//  (java.util.Collections$SingletonList is in module java.base of loader 'bootstrap'; androidx.compose.ui.text.AnnotatedString
//  is in unnamed module of loader 'app')
class LazyTableKtTestVariantJUnit4 {

    @get:Rule
    val rule = createComposeRule()

    // don't inline, surface controls canvas life time
    private val surface = Surface.makeRasterN32Premul(100, 100)
    private val canvas = surface.canvas

    @Test
    fun `drag slider to the middle`() {
        runBlocking(Dispatchers.Main) {
            rule.setContent {
                Text(text = "Test")
            }
            rule.awaitIdle()

            rule.onNodeWithText("Test").assertExists()
        }
    }

    @Test
    fun xyz() {
        assertEquals(true, true)
    }

    @Test
    fun lazyTable() {
        runBlocking {
            rule.setContent {
                Text(text = "Test")
            }
            rule.waitForIdle()

            rule.onNodeWithText("Test").assertExists()
        }
    }
}