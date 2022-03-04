package demo.bigLazyTable.ui.table
/*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import demo.bigLazyTable.utils.printTestMethodName
import org.jetbrains.skia.Surface
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class LazyTableKtTestVariantJUnit5 {

    @get:Rule
    val rule = createComposeRule()

    @BeforeEach
    fun setUp() {
//        rule.setContent {
//            val scrollState = rememberScrollState()
//            LazyTable(
//                viewModel = LazyTableController(
//                    FakePagingService(
//                        numberOfPlaylists = 1_000_000,
//                        pageSize = 40
//                    )
//                ),
//                horizontalScrollState = scrollState
//            )
//        }
    }

    @AfterEach
    fun tearDown() {
    }

    // TODO:
    @Disabled("lateinit property scene has not been initialized")
    @Test
    fun lazyTable() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)


        rule.onNodeWithText("Test").assertExists()
        Thread.sleep(1000)
    }


    // don't inline, surface controls canvas life time
    private val surface = Surface.makeRasterN32Premul(100, 100)
    private val canvas = surface.canvas

//    @Test
//    fun `test jhjku`() {
//        println("Inside method")
//        rule.runOnIdle {
//            rule.setContent {
//                Text(text = "Test")
//            }
//            rule.waitForIdle()
//
//            rule.onNodeWithText("Test").assertExists()
//        }
//    }

//    @Test
//    fun `drag slider to the middle`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 25f), end = Offset(0f, 50f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-50.dp)
//        }
//    }
//
//    @Test
//    fun `drag slider to the edges`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 25f), end = Offset(0f, 500f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-100.dp)
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 99f), end = Offset(0f, -500f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(0.dp)
//        }
//    }
//
//    @Test
//    fun `drag outside slider`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(10f, 25f), end = Offset(0f, 50f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(0.dp)
//        }
//    }
//
//    // TODO(demin): enable after we resolve b/171889442
//    @Ignore("Enable after we resolve b/171889442")
//    @Test
//    fun `mouseScroll over slider`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.performMouseScroll(0, 25, 1f)
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-10.dp)
//        }
//    }
//
//    // TODO(demin): enable after we resolve b/171889442
//    @Ignore("Enable after we resolve b/171889442")
//    @Test
//    fun `mouseScroll over scrollbar outside slider`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.performMouseScroll(0, 99, 1f)
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-10.dp)
//        }
//    }
//
//    // TODO(demin): enable after we resolve b/171889442
//    @Ignore("Enable after we resolve b/171889442")
//    @Test
//    fun `vertical mouseScroll over horizontal scrollbar `() {
//        runBlocking(Dispatchers.Main) {
//            // TODO(demin): write tests for vertical mouse scrolling over
//            //  horizontalScrollbar for the case when we have two-way scrollable content:
//            //  Modifier.verticalScrollbar(...).horizontalScrollbar(...)
//            //  Content should scroll vertically.
//        }
//    }
//
//    @Test
//    fun `mouseScroll over column then drag to the beginning`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 10, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.performMouseScroll(20, 25, 10f)
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-100.dp)
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 99f), end = Offset(0f, -500f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(0.dp)
//        }
//    }
//
//    @Test(timeout = 3000)
//    fun `press on scrollbar outside slider`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 20, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                down(Offset(0f, 26f))
//            }
//
//            tryUntilSucceeded {
//                rule.awaitIdle()
//                rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-100.dp)
//            }
//        }
//    }
//
//    @Test(timeout = 3000)
//    fun `press on the end of scrollbar outside slider`() {
//        runBlocking(Dispatchers.Main) {
//            rule.setContent {
//                TestBox(size = 100.dp, childSize = 20.dp, childCount = 20, scrollbarWidth = 10.dp)
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                down(Offset(0f, 99f))
//            }
//
//            tryUntilSucceeded {
//                rule.awaitIdle()
//                rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-300.dp)
//            }
//        }
//    }
//
//    @Test(timeout = 3000)
//    fun `dynamically change content then drag slider to the end`() {
//        runBlocking(Dispatchers.Main) {
//            val isContentVisible = mutableStateOf(false)
//            rule.setContent {
//                TestBox(
//                    size = 100.dp,
//                    scrollbarWidth = 10.dp
//                ) {
//                    if (isContentVisible.value) {
//                        repeat(10) {
//                            Box(Modifier.size(20.dp).testTag("box$it"))
//                        }
//                    }
//                }
//            }
//            rule.awaitIdle()
//
//            isContentVisible.value = true
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 25f), end = Offset(0f, 500f))
//            }
//            rule.awaitIdle()
//            rule.onNodeWithTag("box0").assertTopPositionInRootIsEqualTo(-100.dp)
//        }
//    }
//
//    @Suppress("SameParameterValue")
//    @OptIn(ExperimentalFoundationApi::class)
//    @Test(timeout = 3000)
//    fun `scroll by less than one page in lazy list`() {
//        runBlocking(Dispatchers.Main) {
//            lateinit var state: LazyListState
//
//            rule.setContent {
//                state = rememberLazyListState()
//                LazyTestBox(
//                    state,
//                    size = 100.dp,
//                    childSize = 20.dp,
//                    childCount = 20,
//                    scrollbarWidth = 10.dp
//                )
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 0f), end = Offset(0f, 11f))
//            }
//            rule.awaitIdle()
//            assertEquals(2, state.firstVisibleItemIndex)
//            assertEquals(4, state.firstVisibleItemScrollOffset)
//        }
//    }
//
//    @Suppress("SameParameterValue")
//    @OptIn(ExperimentalFoundationApi::class)
//    @Test(timeout = 3000)
//    fun `scroll by more than one page in lazy list`() {
//        runBlocking(Dispatchers.Main) {
//            lateinit var state: LazyListState
//
//            rule.setContent {
//                state = rememberLazyListState()
//                LazyTestBox(
//                    state,
//                    size = 100.dp,
//                    childSize = 20.dp,
//                    childCount = 20,
//                    scrollbarWidth = 10.dp
//                )
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 0f), end = Offset(0f, 26f))
//            }
//            rule.awaitIdle()
//            assertEquals(5, state.firstVisibleItemIndex)
//            assertEquals(4, state.firstVisibleItemScrollOffset)
//        }
//    }
//
//    @Suppress("SameParameterValue")
//    @OptIn(ExperimentalFoundationApi::class)
//    @Test(timeout = 3000)
//    fun `scroll outside of scrollbar bounds in lazy list`() {
//        runBlocking(Dispatchers.Main) {
//            lateinit var state: LazyListState
//
//            rule.setContent {
//                state = rememberLazyListState()
//                LazyTestBox(
//                    state,
//                    size = 100.dp,
//                    childSize = 20.dp,
//                    childCount = 20,
//                    scrollbarWidth = 10.dp
//                )
//            }
//            rule.awaitIdle()
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 0f), end = Offset(0f, 10000f))
//            }
//            rule.awaitIdle()
//            assertEquals(15, state.firstVisibleItemIndex)
//            assertEquals(0, state.firstVisibleItemScrollOffset)
//
//            rule.onNodeWithTag("scrollbar").performGesture {
//                instantSwipe(start = Offset(0f, 99f), end = Offset(0f, -10000f))
//            }
//            rule.awaitIdle()
//            assertEquals(0, state.firstVisibleItemIndex)
//            assertEquals(0, state.firstVisibleItemScrollOffset)
//        }
//    }
//
//    private suspend fun tryUntilSucceeded(block: suspend () -> Unit) {
//        while (true) {
//            try {
//                block()
//                break
//            } catch (e: Throwable) {
//                delay(10)
//            }
//        }
//    }
//
//    private fun ComposeTestRule.performMouseScroll(x: Int, y: Int, delta: Float) {
//        (this as DesktopComposeTestRule).window.onMouseScroll(
//            x, y, MouseScrollEvent(MouseScrollUnit.Line(delta), MouseScrollOrientation.Vertical)
//        )
//    }
//
//    @Composable
//    private fun TestBox(
//        size: Dp,
//        childSize: Dp,
//        childCount: Int,
//        scrollbarWidth: Dp,
//    ) = withTestEnvironment {
//        Box(Modifier.size(size)) {
//            val state = rememberScrollState()
//
//            Column(
//                Modifier.fillMaxSize().testTag("column").verticalScroll(state)
//            ) {
//                repeat(childCount) {
//                    Box(Modifier.size(childSize).testTag("box$it"))
//                }
//            }
//
//            VerticalScrollbar(
//                adapter = rememberScrollbarAdapter(state),
//                modifier = Modifier
//                    .width(scrollbarWidth)
//                    .fillMaxHeight()
//                    .testTag("scrollbar")
//            )
//        }
//    }
//
//    @Composable
//    private fun TestBox(
//        size: Dp,
//        scrollbarWidth: Dp,
//        scrollableContent: @Composable ColumnScope.() -> Unit
//    ) = withTestEnvironment {
//        Box(Modifier.size(size)) {
//            val state = rememberScrollState()
//
//            Column(
//                Modifier.fillMaxSize().testTag("column").verticalScroll(state),
//                content = scrollableContent
//            )
//
//            VerticalScrollbar(
//                adapter = rememberScrollbarAdapter(state),
//                modifier = Modifier
//                    .width(scrollbarWidth)
//                    .fillMaxHeight()
//                    .testTag("scrollbar")
//            )
//        }
//    }
//
//    @Suppress("SameParameterValue")
//    @OptIn(ExperimentalFoundationApi::class)
//    @Composable
//    private fun LazyTestBox(
//        state: LazyListState,
//        size: Dp,
//        childSize: Dp,
//        childCount: Int,
//        scrollbarWidth: Dp,
//    ) = withTestEnvironment {
//        Box(Modifier.size(size)) {
//            LazyColumn(
//                Modifier.fillMaxSize().testTag("column"),
//                state
//            ) {
//                items((0 until childCount).toList()) {
//                    Box(Modifier.size(childSize).testTag("box$it"))
//                }
//            }
//
//            VerticalScrollbar(
//                adapter = rememberScrollbarAdapter(state, childCount, childSize),
//                modifier = Modifier
//                    .width(scrollbarWidth)
//                    .fillMaxHeight()
//                    .testTag("scrollbar")
//            )
//        }
//    }
//
//    private fun GestureScope.instantSwipe(start: Offset, end: Offset) {
//        down(start)
//        moveTo(end)
//        up()
//    }
//
//    @Composable
//    private fun withTestEnvironment(content: @Composable () -> Unit) = CompositionLocalProvider(
//        ScrollbarStyleAmbient provides ScrollbarStyle(
//            minimalHeight = 16.dp,
//            thickness = 8.dp,
//            shape = RectangleShape,
//            hoverDurationMillis = 300,
//            unhoverColor = Color.Black,
//            hoverColor = Color.Red
//        ),
//        DesktopPlatformAmbient provides DesktopPlatform.MacOS,
//        content = content
//    )
}
 */