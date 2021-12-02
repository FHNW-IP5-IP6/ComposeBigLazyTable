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
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.model.PlaylistFormModel

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun PlaylistList(model: ViewModelLazyList) {

    Column(
        modifier = Modifier.padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top
    ) {
        PageInfoRow(model)
        HeaderRow(model, PlaylistFormModel(Playlist()))
        LazyColumn(model)

    }
}

@Composable
private fun LazyColumn(model: ViewModelLazyList) {
    val lazyListItems = AppState.lazyModelList
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val scrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 12.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 1000,
        unhoverColor = Color.Red.copy(alpha = 0.3f),
        hoverColor = Color.Red.copy(alpha = 0.8f)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (listState.firstVisibleItemIndex != model.firstIndex) {
            model.get(listState.firstVisibleItemIndex)
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            items(lazyListItems) { playlistFormModel ->
                if (playlistFormModel != null) {
                    PlaylistRow(model, playlistFormModel)
                } else {
                    PlaylistRowPlaceholder()
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            style = scrollbarStyle
        )
    }
}

@Composable
private fun HeaderRow(model: ViewModelLazyList, playlistFormModel: PlaylistFormModel) = LazyRow(
    modifier = Modifier.background(get(FormColors.BACKGROUND_COLOR_HEADER)).fillMaxWidth().padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    items(model.allAttributes(playlistFormModel)) { attribute ->
        Text(text = attribute.getLabel(), color = Color.White, fontWeight = FontWeight.Bold)
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
private fun PlaylistRow(model: ViewModelLazyList, playlistFormModel: PlaylistFormModel) {
    val isSelected = AppState.selectedPlaylist.value.id.getValue() == playlistFormModel.id.getValue()
    val backgroundColor = if (isSelected) {
        Color.Yellow
    } else {
        Color.LightGray
    }

    LazyRow(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .selectable(
                selected = isSelected,
                onClick = {
                    model.selectPlaylist(playlistFormModel)
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(model.allAttributes(playlistFormModel)) { attribute ->
            Text(
                text = attribute.getValueAsText(),
                color = Color.Black,
                modifier = Modifier.background(backgroundColor)
            )
        }
    }
}

@Composable
private fun PlaylistRowPlaceholder() = LazyRow(
    modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    item {
        Text("")
    }
}
