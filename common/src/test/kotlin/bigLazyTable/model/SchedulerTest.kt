package bigLazyTable.model

import bigLazyTable.controller.Scheduler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled

internal class SchedulerTest {

    private var scheduler: Scheduler? = null

    @BeforeEach
    fun createScheduler() {
        // standard scheduler with 50 milliseconds delay
        scheduler = Scheduler()
    }

    @AfterEach
    fun destroyScheduler() {
        scheduler = null
    }

    @Test
    fun scheduleTaskOverflowZeroDelay() {
        scheduler = Scheduler(delayInMillis = 0)
        assertDoesNotThrow {
            for (i in 0 until 1_000_000) {
                scheduler!!.scheduleTask { Thread.sleep(40) }
            }
            Thread.sleep(1000)
        }
    }

    @Test
    fun scheduleTaskOverflow() {
        assertDoesNotThrow {
            for (i in 0 until 1_000_000) {
                scheduler!!.scheduleTask { Thread.sleep(40) }
            }
            Thread.sleep(1000)
        }
    }

    // TODO: Check why this fails when running all tests
    @Disabled("when running all tests this one doesn't work - but on its own it works")
    @Test
    fun processOnlyNewestTask() {
        var result = 0
        fun task1() { result += 10 }
        fun task2() { result += 100 }
        fun task3() { result += 1000 }

        scheduler!!.scheduleTask { task1() }
        scheduler!!.scheduleTask { task2() }
        scheduler!!.scheduleTask { task3() }

        Thread.sleep(1000)

        assertEquals(1000, result)
    }

    // TODO: Check why this fails when running all tests
    @Disabled("when running all tests this one doesn't work - but on its own it works")
    @Test
    fun processOnlyOneTaskInDelayTime() {
        var result = 0
        fun task() { result += 1 }

        for (i in 0 until 100) {
            scheduler!!.scheduleTask { task() }
        }

        Thread.sleep(1000)

        assertEquals(1, result)
    }

    // TODO: 1 Mio im Disabled Text aber unten sind nur 100_000?
    @Disabled(
        "Enable only after bigger changes in scheduler. Takes a lot of time to execute, because the scheduler " +
        "is waiting 1mio times 10milliseconds and 1mio times a 5millisecond sleep before schedule a new task."
    )
    @Test
    fun scheduleTaskOverflowWithDelay() {
        scheduler = Scheduler(10)
        var result = 0

        assertDoesNotThrow {
            for (i in 0 until 100_000) {
                scheduler!!.scheduleTask { result += 1 }
                Thread.sleep(5)
            }
            Thread.sleep(1000)
        }

        assertEquals(1, result)
    }

    // TODO: Check why this fails when running all tests
    @Disabled("when running all tests this one doesn't work - but on its own it works")
    @Test
    fun delayIsConsidered() {
        var result = 0
        fun task() {
            result += 1
        }

        scheduler!!.scheduleTask { task() }
        Thread.sleep(155)
        scheduler!!.scheduleTask { task() }
        Thread.sleep(155)
        scheduler!!.scheduleTask { task() }

        Thread.sleep(1000)

        assertEquals(3, result)
    }
}