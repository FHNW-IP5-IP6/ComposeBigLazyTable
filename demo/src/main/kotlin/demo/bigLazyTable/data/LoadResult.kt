package demo.bigLazyTable.data

import androidx.compose.runtime.*
import demo.bigLazyTable.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay

sealed class LoadResult<T, U> {

    data class Page(
        val data: PagingData<Playlist>,
        val nextKey: Int?,
        val prevKey: Int?
    )

    data class Error(val throwable: Throwable)

}

sealed class LoadingState<out T: Any> {
    object Start: LoadingState<Nothing>()
    object Loading: LoadingState<Nothing>()
    class Error(val error: Exception): LoadingState<Nothing>()
    class Success<T: Any>(val data: T): LoadingState<T>()
}

//@Composable
//fun <T: Any> loadingStateFor(vararg inputs: Any?, initBlock: () -> LoadingState<T> = { LoadingState.Start },
//                             loadingBlock: suspend CoroutineScope.() -> T): LoadingState<T> {
//    var state by remember(*inputs) { mutableStateOf(initBlock()) }
//    if (state !is LoadingState.Success) {
//        LaunchedEffect(*inputs) {
//            val loadingSpinnerDelay = async {
//                delay(300)
//                state = LoadingState.Loading
//            }
//            state = try {
//                LoadingState.Success(loadingBlock())
//            } catch (err: Exception) {
//                LoadingState.Error(err)
//            } finally {
//                loadingSpinnerDelay.cancelAndJoin()
//            }
//        }
//    }
//    return state
//}
