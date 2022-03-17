package demo.bigLazyTable.model

import composeForms.model.ILabel

/**
 * @author Marco Sprenger, Livio Näf
 */
enum class BLTLabels(val deutsch: String, val english: String) : ILabel {
    TITLE("Spotify Daten","Spotify data"),
    HEADER_GROUP("Playlist Übersicht", "Playlist Overview"),
    PLAYLIST_INFO_GROUP("Playlist Informationen", "Playlist Informations"),
    TRACK0_GROUP("Song 0", "Track 0"),
    TRACK1_GROUP("Song 1", "Track 1"),
    TRACK2_GROUP("Song 2", "Track 2"),
    TRACK3_GROUP("Song 3", "Track 3"),
    TRACK4_GROUP("Song 4", "Track 4"),

    ID("ID", "ID"),
    NAME("Name", "Name"),
    COLLABORATIVE("Gemeinsam", "Collaborative"),
    SELECTION_YES("Ja", "Yes"),
    SELECTION_NO("Nein", "No"),
    MODIFIED_AT("Geändert am", "Modified at"),
    NUM_TRACKS("Anz. Songs", "No. of tracks"),
    NUM_ALBUMS("Anz. Alben", "No. of albums"),
    NUM_FOLLOWERS("Anz. Follower", "No. of followers"),
    NUM_EDITS("Anz. Änderungen", "No. of edits"),
    DURATION_MS("Länge in ms", "Duration in ms"),
    NUM_ARTISTS("Anz. Künstler", "No. of artists"),
    TRACK_ARTIST_NAME("Song Künstler", "Track artist"),
    TRACK_TRACK_NAME("Song Name", "Track name"),
    TRACK_DURATION_MS("Song Länge in ms", "Track duration in ms"),
    TRACK_ALBUM_NAME("Song Album", "Track album")
}