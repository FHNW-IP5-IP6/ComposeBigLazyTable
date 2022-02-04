package demo.bigLazyTable.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Marco Sprenger, Livio Näf
 */
object Scheduler {
    private var inProcess = false
    private var task: ((Any?) -> Unit)? = null

    private fun process() {
        if (inProcess) return
        if (task == null) return
        inProcess = true
        val taskToDo = task
        CoroutineScope(Dispatchers.IO).launch {
            //println("Task in process: ${taskToDo.hashCode()}")
            taskToDo!!.invoke(null)
        }.invokeOnCompletion {
            //println("Task finished: ${taskToDo.hashCode()}")
            inProcess = false
            if (task!! == taskToDo) task = null
            process()
        }
    }

    fun add(task: (Any?) -> Unit) {
        //println("Task added to scheduler: ${task.hashCode()}")
        this.task = task
        process()
    }
}