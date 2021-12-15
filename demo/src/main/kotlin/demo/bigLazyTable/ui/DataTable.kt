package demo.bigLazyTable.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.model.PlaylistFormModel

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun DataTable(model: ViewModelLazyList) {
    Column(
        modifier = Modifier.padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top
    ) {
        PageInfoRow(model)
        HeaderRow()
        LazyList(model)
    }
}

@Composable
private fun PageInfoRow(model: ViewModelLazyList) = LazyRow(
    modifier = Modifier.background(get(FormColors.BACKGROUND_COLOR_HEADER)).fillMaxWidth().padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.Start
) {
    item {
        Text(
            text = "Page: ${model.currentPage.value}/${model.maxPages}",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HeaderRow() = LazyRow(
    modifier = Modifier.background(get(FormColors.BACKGROUND_COLOR_HEADER)).fillMaxWidth().padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    items(AppState.defaultPlaylistFormModel.lazyListAttributes) { attribute ->
        Text(text = attribute.getLabel(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LazyList(model: ViewModelLazyList) {
    val lazyListItems = AppState.lazyModelList
    val listState = rememberLazyListState()
    val scrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 12.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 1000,
        unhoverColor = Color.Red.copy(alpha = 0.3f),
        hoverColor = Color.Red.copy(alpha = 0.8f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val firstVisibleItemIndex = listState.firstVisibleItemIndex
        if (timeToLoadPage(firstVisibleItemIndex, model)) {
            model.loadAllNeededPagesFor(firstVisibleItemIndex = firstVisibleItemIndex)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            items(lazyListItems) { playlistFormModel ->
                PlaylistRow(model, playlistFormModel)
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            style = scrollbarStyle
        )
    }
}

private fun timeToLoadPage(firstVisibleItemIndex: Int, model: ViewModelLazyList): Boolean {
    return timeToLoadNextPage(firstVisibleItemIndex, model) || timeToLoadPreviousPage(firstVisibleItemIndex, model)
}

private fun timeToLoadNextPage(firstVisibleItemIndex: Int, model: ViewModelLazyList): Boolean {
    val endOfPage = model.lastVisibleIndex + model.pageSize
    return firstVisibleItemIndex > endOfPage
}

private fun timeToLoadPreviousPage(firstVisibleItemIndex: Int, model: ViewModelLazyList): Boolean {
    val startOfPage = model.lastVisibleIndex - model.pageSize
    return firstVisibleItemIndex < startOfPage
}

@Composable
private fun PlaylistRow(model: ViewModelLazyList, playlistFormModel: PlaylistFormModel) {
    val isSelected = AppState.selectedPlaylist.id.getValue() == playlistFormModel.id.getValue()
    val backgroundColor = if (isSelected) {
        get(FormColors.BACKGROUND_COLOR_GROUPS)
    } else {
        get(FormColors.BACKGROUND_COLOR_LIGHT)
    }

    LazyRow(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .selectable(
                selected = isSelected,
                onClick = { model.selectPlaylist(playlistFormModel) }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(playlistFormModel.lazyListAttributes) { attribute ->
            Text(
                text = if (attribute.getValueAsText() != (-999_999).toString()) attribute.getValueAsText() else "...",
                color = Color.Black,
                modifier = Modifier.background(backgroundColor)
            )
        }
    }
}
