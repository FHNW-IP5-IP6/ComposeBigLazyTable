package demo.bigLazyTable.data

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import java.io.FileReader


class CSVService {

    fun requestAllData(): MutableList<Playlist> {
        val playlists: MutableList<Playlist> = mutableListOf()

        val csvReader = CSVReaderBuilder(FileReader("./demo/src/main/resources/spotify_playlist_dataset.csv"))
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()

        val header = csvReader.readNext()

        var nextLine: Array<String>? = csvReader.readNext()

        var index: Long = 0

        while (nextLine != null) {
            playlists.add(
                Playlist(
                    nextLine[0],
                    nextLine[1].toBoolean(),
                    nextLine[2],
                    /*nextLine[3],
                    nextLine[4],
                    nextLine[5],
                    nextLine[6],
                    nextLine[7],
                    nextLine[8],
                    nextLine[9],
                    nextLine[10],
                    nextLine[11],
                    nextLine[12],
                    nextLine[13],
                    nextLine[14],
                    nextLine[15],
                    nextLine[16],
                    nextLine[17],
                    nextLine[18],
                    nextLine[19],
                    nextLine[20],
                    nextLine[21],
                    nextLine[22],
                    nextLine[23],
                    nextLine[24],*/
                    index
                )
            )
            index++
            nextLine = csvReader.readNext()
        }
        //println("Number of playlists loaded: " + playlists.size)

        return playlists
    }

    fun requestDataPage(startIndex: Int, pageSize: Int): MutableList<Playlist> {
        val playlists: MutableList<Playlist> = mutableListOf()

        val csvReader = CSVReaderBuilder(FileReader("./demo/src/main/resources/spotify_playlist_dataset.csv"))
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()

        val header = csvReader.readNext()

        var nextLine: Array<String>? = csvReader.readNext()
        var index = 0
        while (nextLine != null && index < startIndex-1) {
            nextLine = csvReader.readNext()
            index++
        }
        var numbersOfElementsLoaded = 0
        var ind: Long = 0
        while (nextLine != null && numbersOfElementsLoaded < pageSize) {
            playlists.add(
                Playlist(
                    nextLine[0],
                    nextLine[1].toBoolean(),
                    nextLine[2],
                    /*nextLine[3],
                    nextLine[4],
                    nextLine[5],
                    nextLine[6],
                    nextLine[7],
                    nextLine[8],
                    nextLine[9],
                    nextLine[10],
                    nextLine[11],
                    nextLine[12],
                    nextLine[13],
                    nextLine[14],
                    nextLine[15],
                    nextLine[16],
                    nextLine[17],
                    nextLine[18],
                    nextLine[19],
                    nextLine[20],
                    nextLine[21],
                    nextLine[22],
                    nextLine[23],
                    nextLine[24],*/
                    ind
                )
            )
            ind++
            nextLine = csvReader.readNext()
            numbersOfElementsLoaded++
        }
        //println("Number of playlists loaded: " + playlists.size)

        return playlists
    }

}
