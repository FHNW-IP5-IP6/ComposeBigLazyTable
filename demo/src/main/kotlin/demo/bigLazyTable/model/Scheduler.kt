package demo.bigLazyTable.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Marco Sprenger, Livio Näf
 *
 * Scheduler to run only one task. If a new task is added, the old one is overwritten.
 * If the scheduler is in process, after a short delay the newest task is executes.
 * After execution the scheduler checks if a new task is available, if not the scheduler is paused.
 */
// TODO: As class
object Scheduler {
    private var inProcess = false
    private var task: (() -> Unit)? = null
    private var taskToDo: (() -> Unit)? = null

    // TODO: Param delay mitgeben oder attribut setzen
    private fun process() {
        if (inProcess) return
        if (task == null) return
        inProcess = true
        taskToDo = task
        CoroutineScope(Dispatchers.IO).launch {
            delay(50) // TODO: Test with 0 delay

            if (taskToDo == task) {
                taskToDo?.invoke()
            }
        }.invokeOnCompletion {
            inProcess = false
            if (task == taskToDo) task = null
            println("process called")
            process()
        }
    }

    // TODO: Should this be synchronized?
    fun set(task: () -> Unit) {
        this.task = task
        process()
    }
}
