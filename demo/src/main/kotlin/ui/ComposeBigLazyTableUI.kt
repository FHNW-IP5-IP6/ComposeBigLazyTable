package ui

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import model.Person

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    DesktopMaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }

        val persons = listOf(
            Person(23, "Hans", "Perter"),
            Person(24, "Hanis", "Petrer"),
            Person(25, "Hansi", "Preter"),
            Person(26, "Hani", "Peterr")
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            item {
                TableHeader(item = persons.first())
            }
            items(items = persons) { person ->
                TableRowItem(item = person)
            }
        }
    }
}

fun getNumberOfAttributesOf(any: Any) = any.javaClass.declaredFields.size-1

@Composable
@Preview
fun TableHeader(item: Any) {
    Row(
        modifier = Modifier
            .background(Color.Cyan)
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (i in 0 until getNumberOfAttributesOf(item)) {
            Text(
                text = item.javaClass.declaredFields[i].name,
                modifier = Modifier.width(70.dp)
            )
        }
    }
}

@Composable
@Preview
fun TableRowItem(item: Any) {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 0 until getNumberOfAttributesOf(item)) {
            val field = item.javaClass.declaredFields[i]
            field.isAccessible = true
            val value = field.get(item)
            Text(
                text = value?.toString() ?: "-",
                modifier = Modifier.width(70.dp),
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}