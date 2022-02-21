package com.idex.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember


@Composable
fun rememberCodeEditor(): CodeEditor {
	val editor = remember { CodeEditor(123) }
	
	return editor
}


@Stable
class CodeEditor(val a: Int) {
	@Composable
	operator fun invoke() {
		
	}
}
