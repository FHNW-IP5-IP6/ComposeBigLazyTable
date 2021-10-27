package demo.bigLazyTable.model

import model.ILabel

enum class ComposeFormsBigLazyTableLabels(val deutsch: String, val english: String) : ILabel {
    TITLE("Spotify Daten","Spotify data"),
    HEADERGROUP("Playlist Übersicht", "Playlist Overview"),

    NAME("Name", "Name"),
    COLLABORATIVE("Gemeinsam", "Collaborative"),
    SELECTIONYES("Ja", "Yes"),
    SELECTIONNO("Nein", "No"),
    MODIFIED_AT("Geändert am", "Modified at")
}