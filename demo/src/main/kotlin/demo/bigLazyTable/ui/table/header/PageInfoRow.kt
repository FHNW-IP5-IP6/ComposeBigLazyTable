package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.ui.theme.BackgroundColorHeader

@Composable
fun PageInfoRow(viewModel: LazyTableController, currentPage: Int) = Row(
    modifier = Modifier
        .background(BackgroundColorHeader)
        .fillMaxWidth()
        .padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.Start
) {
    Text(
        text = "Page: ${currentPage}/${viewModel.totalPages}",
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}