package demo.bigLazyTable.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import demo.bigLazyTable.model.BigLazyTablesModel

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
@Composable
fun DataChooser(model: BigLazyTablesModel) {
    with(model) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    loadTestData()
                    dataChooserStatus.value = true
                },
                content = {
                    Text("Load test data (30 items)")
                }
            )
            Button(
                onClick = {
                    loadProdData()
                    dataChooserStatus.value = true
                },
                content = {
                    Text("Load prod data (1MIO items)")
                }
            )
            Row {
                val noOfData = remember { mutableStateOf(TextFieldValue()) }
                TextField(
                    value = noOfData.value,
                    onValueChange = { noOfData.value = it }
                )
                Button(
                    onClick = {
                        loadCustomizedData((noOfData.value.text.toInt()))
                        dataChooserStatus.value = true
                    },
                    content = {
                        Text("Load customized data (${noOfData.value.text} items)")
                    }
                )
            }
        }
    }
}
