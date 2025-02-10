package me.dvyy.tasks.views.data

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import me.dvyy.tasks.app.ui.VaultViewModel
import me.dvyy.tasks.database.helpers.DocumentHelpers.content
import me.dvyy.tasks.database.helpers.DocumentHelpers.vaultPath
import me.dvyy.tasks.notes.ui.NoteTypography
import me.dvyy.tasks.tasks.ui.CachedUpdate
import org.dizitart.no2.collection.Document
import org.koin.compose.viewmodel.koinViewModel

class ViewsManager {

}

interface NoteView {
    @Composable
    fun content(configuration: Document, document: Document)
}

class EditView: NoteView {
    @Composable
    override fun content(configuration: Document, document: Document) {
        val vault = koinViewModel<VaultViewModel>()
        val path = document.vaultPath()
        CachedUpdate(path, document.content(), {
            vault.updateContent(path, it)
        }) { content, setContent ->
            TextField(content, onValueChange = setContent)
        }
    }
}

class MarkdownView: NoteView {
    @Composable
    override fun content(configuration: Document, document: Document) {
        val typography = NoteTypography()
        val highlightsBuilder = remember {
            Highlights.Builder().theme(SyntaxThemes.atom(darkMode = true))
        }
        Markdown(
            document.content().toString(),
            typography = typography,
            components = markdownComponents(
                codeBlock = { MarkdownHighlightedCodeBlock(it.content, it.node, highlightsBuilder) },
                codeFence = { MarkdownHighlightedCodeFence(it.content, it.node, highlightsBuilder) },
            )
        )
    }
}
