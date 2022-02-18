package demo.bigLazyTable.model

import kotlinx.coroutines.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled

/**
 * TODO: How to test that fast scrolling doesnt load a page
 */
internal class SchedulerTest {

    lateinit var scheduler: Scheduler

    @BeforeEach
    fun setUp() {
        scheduler = Scheduler
    }

    @Test
    fun set() {
//        val scope = CoroutineScope(newFixedThreadPoolContext(4, "synchroPool"))
//        for (i in 0 until 1_000_000) {
//            scope.launch {
//                delay(1000)
//                scheduler.set { println("Task $i") }
//            }
//        }
    }

    // TODO: Disable when finished
    @Disabled("Why is it disbaled & when to enable back")
    @Test
    fun setOverflow() {
        assertDoesNotThrow {
            for (i in 0 until 1_000_000_000) {
                scheduler.set { doWork() }
            }
            Thread.sleep(1000)
        }
    }

    // TODO: Add delay < 50
    private fun doWork() {
        Thread.sleep(40)
        println("Work done")
    }
}