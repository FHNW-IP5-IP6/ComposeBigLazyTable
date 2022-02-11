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
object Scheduler {
    private var inProcess = false
    private var task: (() -> Unit)? = null
    private var taskToDo: (() -> Unit)? = null

    private fun process() {
        if (inProcess) return
        if (task == null) return
        inProcess = true
        CoroutineScope(Dispatchers.IO).launch {
            //Thread.sleep(50)
            delay(50)
            taskToDo = task
            taskToDo!!.invoke()
        }.invokeOnCompletion {
            inProcess = false
            if (task!! == taskToDo) task = null
            process()
        }
    }

    // TODO: Should this be synchronized?
    fun set(task: () -> Unit) {
        this.task = task
        process()
    }
}
