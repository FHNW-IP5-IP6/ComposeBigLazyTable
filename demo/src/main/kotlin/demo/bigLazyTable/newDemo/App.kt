package demo.bigLazyTable.newDemo

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.database.SqliteDb
import bigLazyTable.view.BigLazyTableUI
import demo.bigLazyTable.newDemo.data.service.Superstore
import demo.bigLazyTable.newDemo.data.service.SuperstoreDBService
import demo.bigLazyTable.newDemo.model.SuperstoreModel
import java.awt.Dimension

@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun main() {

    // DB Connection
    SqliteDb(pathToDb = "./demo/src/main/resources/superstore.db").initializeConnection()

    // Controller with PagingService, PresentationModel, mapToModels-Lambda
    val controller = LazyTableController(
        pagingService = SuperstoreDBService,
        defaultModel = SuperstoreModel(Superstore()),
        mapToModels = { page, appState ->
            page.map { SuperstoreModel(it as Superstore).apply { this.appState = appState } }
        }
    )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            title = "ComposeLists"
        ) {
            window.minimumSize = Dimension(1000, 800)

            // Use BigLazyTable Composable with defined controller
            BigLazyTableUI(controller = controller)
        }
    }
}