package demo.bigLazyTable.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Scheduler {
    private var inProcess = false
    private var task: ((Any?) -> Unit)? = null

    private fun process() {
        if (inProcess) return
        if (task == null) return
        inProcess = true
        CoroutineScope(Dispatchers.IO).launch {
            task!!.invoke(null)
        }.invokeOnCompletion {
            inProcess = false
            task = null
            process()
        }
    }

    fun add(task: (Any?) -> Unit) {
        this.task = task
        process()
    }
}