package demo.composeLists

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.FileReader

class CSVService {

    fun loadData(): List<Playlist> {
        val playlists: MutableList<Playlist> = mutableListOf()

        val csvReader = CSVReaderBuilder(FileReader("./demo/src/main/resources/spotify_playlist_dataset.csv"))
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()

        val header = csvReader.readNext()

        var nextLine: Array<String>? = csvReader.readNext()
        while (nextLine != null) {
            playlists.add(
                Playlist(
                    nextLine[0],
                    nextLine[1].toBoolean(),
                    nextLine[2],
                    nextLine[3],
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
                    nextLine[24]
                )
            )

            nextLine = csvReader.readNext()
        }
        println("Size of playlists: " + playlists.size)

        return playlists
    }
}