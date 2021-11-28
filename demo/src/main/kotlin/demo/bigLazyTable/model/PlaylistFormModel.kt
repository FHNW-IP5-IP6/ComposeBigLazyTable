package demo.bigLazyTable.model

import model.BaseModel
import model.attributes.BooleanAttribute
import model.attributes.LongAttribute
import model.attributes.StringAttribute
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.HeaderGroup

/**
 * @author Marco Sprenger, Livio Näf
 */
class PlaylistFormModel(val playlist: Playlist): BaseModel<FormsBLTLabels>(title = FormsBLTLabels.TITLE) {

    private val id = LongAttribute(
        model = this,
        label = FormsBLTLabels.ID,
        value = playlist.id,
        readOnly = true
    )

    private val name = StringAttribute(
        model = this,
        label = FormsBLTLabels.NAME,
        value = playlist.name
    )

    private val collaborative = BooleanAttribute(
        model = this,
        label = FormsBLTLabels.COLLABORATIVE,
        trueText = FormsBLTLabels.SELECTIONYES,
        falseText = FormsBLTLabels.SELECTIONNO,
        value = playlist.collaborative
    )

    private val modifiedAt = StringAttribute(
        model = this,
        label = FormsBLTLabels.MODIFIED_AT,
        required = true,
        value = playlist.modifiedAt
    )

    val headerGroup = HeaderGroup(
        model = this, title = FormsBLTLabels.HEADERGROUP,
        Field(id, FieldSize.SMALL),
        Field(name, FieldSize.NORMAL),
        Field(collaborative, FieldSize.SMALL),
        Field(modifiedAt, FieldSize.NORMAL)
    )

}