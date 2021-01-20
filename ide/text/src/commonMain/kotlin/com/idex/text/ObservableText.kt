package com.idex.text

import com.idex.text.diff.TextPatch
import kotlinx.coroutines.flow.MutableSharedFlow


data class TextMutation(val patch: TextPatch, val before: TextBuffer?, val after: TextBuffer?)


/**
 * A text that also enables for users to observe the changes of this text.
 */
interface ObservableText {
	val observation: MutableSharedFlow<TextMutation>
}
