package demo.bigLazyTable.model

import composeForms.model.BaseModel
import composeForms.model.attributes.*
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.FieldSize
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
import demo.bigLazyTable.data.database.DatabasePlaylists

/**
 * @author Marco Sprenger, Livio Näf
 */
class PlaylistModel(playlist: Playlist, val appState: AppState) : BaseModel<BLTLabels>(title = BLTLabels.TITLE) {

    // Our Filter treats all values as Strings so
    // TODO: Add a floatingPointAttribute (Double/Float) to test if it works aswell with them
    // TODO: Add a DecisionAttribute to test (Is like BooleanAttri. a DualAttribute
    // TODO: Add a SelectionAttribute to test

    val id = LongAttribute(
        model = this,
        label = BLTLabels.ID,
        value = playlist.id,
        readOnly = true,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.id
    )

    private val numEdits = IntegerAttribute(
        model = this,
        label = BLTLabels.NUM_EDITS,
        value = playlist.numEdits,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.num_edits
    )

    private val name = StringAttribute(
        model = this,
        label = BLTLabels.NAME,
        value = playlist.name,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.name
    )

    private val modifiedAt = IntegerAttribute(
        model = this,
        label = BLTLabels.MODIFIED_AT,
        required = true,
        value = playlist.modifiedAt,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.modified_at
    )

    private val track0ArtistName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ARTIST_NAME,
        value = playlist.track0ArtistName,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.track0_artist_name
    )

    private val collaborative = BooleanAttribute(
        model = this,
        label = BLTLabels.COLLABORATIVE,
        trueText = BLTLabels.SELECTION_YES,
        falseText = BLTLabels.SELECTION_NO,
        value = playlist.collaborative,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.collaborative
    )

    private val numTracks = IntegerAttribute(
        model = this,
        label = BLTLabels.NUM_TRACKS,
        value = playlist.numTracks,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.num_tracks
    )

    private val durationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.DURATION_MS,
        value = playlist.durationMs,
        canBeFiltered = true,
        databaseField = DatabasePlaylists.duration_ms
    )

    val displayedAttributesInTable = listOf(
        id,
        numEdits,
        name,
        modifiedAt,
        track0ArtistName,
        collaborative,
        numTracks,
        numTracksDouble,
        numTracksFloat,
        durationMs
    )

    private val numFollowers = IntegerAttribute(
        model = this,
        label = BLTLabels.NUM_FOLLOWERS,
        value = playlist.numFollowers
    )

    private val numArtists = IntegerAttribute(
        model = this,
        label = BLTLabels.NUM_ARTISTS,
        value = playlist.numArtists
    )

    private val track0TrackName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_TRACK_NAME,
        value = playlist.track0TrackName
    )

    private val track0DurationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.TRACK_DURATION_MS,
        value = playlist.track0DurationMs
    )

    private val track0AlbumName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ALBUM_NAME,
        value = playlist.track0AlbumName
    )

    private val track1ArtistName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ARTIST_NAME,
        value = playlist.track1ArtistName
    )

    private val track1TrackName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_TRACK_NAME,
        value = playlist.track1TrackName
    )

    private val track1DurationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.TRACK_DURATION_MS,
        value = playlist.track1DurationMs
    )

    private val track1AlbumName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ALBUM_NAME,
        value = playlist.track1AlbumName
    )

    private val track2ArtistName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ARTIST_NAME,
        value = playlist.track2ArtistName
    )

    private val track2TrackName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_TRACK_NAME,
        value = playlist.track2TrackName
    )

    private val track2DurationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.TRACK_DURATION_MS,
        value = playlist.track2DurationMs
    )

    private val track2AlbumName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ALBUM_NAME,
        value = playlist.track2AlbumName
    )

    private val track3ArtistName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ARTIST_NAME,
        value = playlist.track3ArtistName
    )

    private val track3TrackName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_TRACK_NAME,
        value = playlist.track3TrackName
    )

    private val track3DurationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.TRACK_DURATION_MS,
        value = playlist.track3DurationMs
    )

    private val track3AlbumName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ALBUM_NAME,
        value = playlist.track3AlbumName
    )

    private val track4ArtistName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ARTIST_NAME,
        value = playlist.track4ArtistName
    )

    private val track4TrackName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_TRACK_NAME,
        value = playlist.track4TrackName
    )

    private val track4DurationMs = IntegerAttribute(
        model = this,
        label = BLTLabels.TRACK_DURATION_MS,
        value = playlist.track4DurationMs
    )

    private val track4AlbumName = StringAttribute(
        model = this,
        label = BLTLabels.TRACK_ALBUM_NAME,
        value = playlist.track4AlbumName
    )

    private val headerGroup = HeaderGroup(
        model = this,
        title = BLTLabels.HEADER_GROUP,
        Field(id, FieldSize.SMALL),
        Field(name, FieldSize.NORMAL)
    )

    private val playlistInfoGroup = Group(
        model = this,
        title = BLTLabels.PLAYLIST_INFO_GROUP,
        Field(name, FieldSize.NORMAL),
        Field(collaborative, FieldSize.SMALL),
        Field(modifiedAt, FieldSize.SMALL),
        Field(numTracks, FieldSize.SMALL),
        Field(numEdits, FieldSize.SMALL),
        Field(numArtists, FieldSize.SMALL),
        Field(durationMs, FieldSize.SMALL),
    )

    private val track0Group = Group(
        model = this,
        title = BLTLabels.TRACK0_GROUP,
        Field(track0TrackName, FieldSize.BIG),
        Field(track0ArtistName, FieldSize.NORMAL),
        Field(track0AlbumName, FieldSize.NORMAL),
        Field(track0DurationMs, FieldSize.SMALL)
    )

    private val track1Group = Group(
        model = this,
        title = BLTLabels.TRACK1_GROUP,
        Field(track1TrackName, FieldSize.BIG),
        Field(track1ArtistName, FieldSize.NORMAL),
        Field(track1AlbumName, FieldSize.NORMAL),
        Field(track1DurationMs, FieldSize.SMALL)
    )

    private val track2Group = Group(
        model = this,
        title = BLTLabels.TRACK2_GROUP,
        Field(track2TrackName, FieldSize.BIG),
        Field(track2ArtistName, FieldSize.NORMAL),
        Field(track2AlbumName, FieldSize.NORMAL),
        Field(track2DurationMs, FieldSize.SMALL)
    )

    private val track3Group = Group(
        model = this,
        title = BLTLabels.TRACK3_GROUP,
        Field(track3TrackName, FieldSize.BIG),
        Field(track3ArtistName, FieldSize.NORMAL),
        Field(track3AlbumName, FieldSize.NORMAL),
        Field(track3DurationMs, FieldSize.SMALL)
    )

    private val track4Group = Group(
        model = this,
        title = BLTLabels.TRACK4_GROUP,
        Field(track4TrackName, FieldSize.BIG),
        Field(track4ArtistName, FieldSize.NORMAL),
        Field(track4AlbumName, FieldSize.NORMAL),
        Field(track4DurationMs, FieldSize.SMALL)
    )

    override fun updateChanges() {
        if (!appState.changedPlaylistModels.contains(this)) {
            appState.changedPlaylistModels.add(this)
        }
        super.updateChanges()
    }

}