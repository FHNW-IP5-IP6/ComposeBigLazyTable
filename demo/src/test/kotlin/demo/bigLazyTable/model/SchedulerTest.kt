package demo.bigLazyTable.model

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
    fun createScheduler() {
        scheduler = Scheduler()
    }

    @Disabled
    @Test
    fun zeroDelayOverflow() {
        scheduler = Scheduler(delayInMillis = 0)
        assertDoesNotThrow {
            for (i in 0 until 1_000_000) {
                scheduler.scheduleTask { doWork(40) }
            }
            Thread.sleep(1000)
        }
    }

    @Disabled("Why is it disbaled & when to enable back")
    @Test
    fun setOverflow() {
        assertDoesNotThrow {
            for (i in 0 until 1_000_000) {
                scheduler.scheduleTask { doWork(40) }
            }
            Thread.sleep(1000)
        }
    }

    /*
    Helper functions
     */
    private fun doWork(sleepInMillis: Long) {
        Thread.sleep(sleepInMillis)
        println("Work done")
    }
}