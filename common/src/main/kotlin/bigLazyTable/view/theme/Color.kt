package bigLazyTable.view.theme

import composeForms.ui.theme.BackgroundColorHeader

/**
 * @author Marco Sprenger, Livio Näf
 */
val HoverColor      by lazy { BackgroundColorHeader }
val UnhoverColor    by lazy { BackgroundColorHeader.copy(alpha = 0.7f) }