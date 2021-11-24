package demo.bigLazyTable.model

import model.ILabel

/**
 * @author Marco Sprenger, Livio Näf
 */
enum class FormsBLTLabels(val deutsch: String, val english: String) : ILabel {
    TITLE("Spotify Daten","Spotify data"),
    HEADERGROUP("Playlist Übersicht", "Playlist Overview"),

    NAME("Name", "Name"),
    COLLABORATIVE("Gemeinsam", "Collaborative"),
    SELECTIONYES("Ja", "Yes"),
    SELECTIONNO("Nein", "No"),
    MODIFIED_AT("Geändert am", "Modified at")
}