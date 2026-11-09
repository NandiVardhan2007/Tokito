package com.owlcoders.tokito.ui.settings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.owlcoders.tokito.db.AppDatabase
import com.owlcoders.tokito.db.entities.PersonalDocument
import com.owlcoders.tokito.documents.DocumentIntake
import com.owlcoders.tokito.documents.ExtractedFields
import com.owlcoders.tokito.documents.IdParser
import com.owlcoders.tokito.security.AppLock
import com.owlcoders.tokito.security.SecureScreen
import com.owlcoders.tokito.security.Vault
import com.owlcoders.tokito.ui.components.BarIconButton
import com.owlcoders.tokito.ui.components.LargeTitleScaffold
import com.owlcoders.tokito.ui.components.LargeTitleSubtitle
import com.owlcoders.tokito.ui.components.LinkButton
import com.owlcoders.tokito.ui.components.InsetRow
import com.owlcoders.tokito.ui.components.Space
import com.owlcoders.tokito.ui.components.insetSection
import com.owlcoders.tokito.ui.components.rememberHaptics
import com.owlcoders.tokito.ui.theme.Tokito
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * One document, decrypted into memory only for as long as it is on screen. A PDF is rendered
 * through a private temp copy that is deleted straight after. Hidden from screenshots; locked
 * behind the same unlock as Personal details.
 */
@Composable
fun DocumentViewerScreen(documentId: Long, onDeleted: () -> Unit) {
    SecureScreen()
    val context = LocalContext.current
    val colors = Tokito.colors
    val scope = rememberCoroutineScope()
    val haptics = rememberHaptics()
    var doc by remember { mutableStateOf<PersonalDocument?>(null) }
    var pages by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
    var fields by remember { mutableStateOf(ExtractedFields()) }
    var confirmDelete by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(documentId) {
        if (!AppLock.sensitiveUnlocked()) {
            onDeleted() // bounced back: the list screen asks for the unlock
            return@LaunchedEffect
        }
        val d = AppDatabase.getDatabase(context).personalDocumentDao().getById(documentId) ?: run { onDeleted(); return@LaunchedEffect }
        doc = d
        fields = runCatching { DocumentIntake.readFields(context, d) }.getOrDefault(ExtractedFields())
        pages = withContext(Dispatchers.IO) {
            runCatching {
                val bytes = Vault.read(context, d.fileName) ?: return@runCatching emptyList()
                if (d.mimeType == "application/pdf") renderPdf(context.cacheDir, bytes)
                else listOfNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap())
            }.getOrElse { emptyList() }
        }
        failed = pages.isEmpty()
    }

    val d = doc ?: return
    LargeTitleScaffold(
        title = d.title,
        subtitle = { LargeTitleSubtitle("Encrypted on this phone") },
        actions = { BarIconButton(icon = Icons.Rounded.Delete, contentDescription = "Delete document", tint = colors.danger, onClick = { confirmDelete = true }) }
    ) {
        pages.forEachIndexed { i, page ->
            item(key = "page-$i") {
                Image(
                    bitmap = page,
                    contentDescription = "Page ${i + 1} of ${d.title}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Space.gutter, vertical = Space.s)
                        .clip(RoundedCornerShape(12))
                )
            }
        }
        if (failed) {
            item(key = "failed") {
                Text("This document couldn't be opened.", style = MaterialTheme.typography.bodyLarge, color = colors.textMid, modifier = Modifier.padding(Space.gutter))
            }
        }
        val rows = listOf(
            "Name" to fields.name, "Date of birth" to fields.dateOfBirth, "Gender" to fields.gender,
            "Father's name" to fields.fatherName, "Address" to fields.address,
            "Aadhaar" to IdParser.maskAadhaar(fields.aadhaarNumber), "PAN" to IdParser.maskPan(fields.panNumber),
            "Ration card" to fields.rationCardNumber
        ).filter { it.second.isNotBlank() }
        if (rows.isNotEmpty()) {
            insetSection(key = "fields", header = "What Tokito read") {
                rows.forEach { (label, value) ->
                    row(label) { InsetRow(title = label, subtitle = value, subtitleLines = 4) }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = colors.surface,
            title = { Text("Delete this document?", style = MaterialTheme.typography.titleLarge, color = colors.textHigh) },
            text = { Text("The file is erased from this phone. Details already saved to your profile stay until you edit them.", style = MaterialTheme.typography.bodyMedium, color = colors.textMid) },
            confirmButton = {
                LinkButton(text = "Delete", color = colors.danger, style = MaterialTheme.typography.titleLarge, onClick = {
                    confirmDelete = false
                    scope.launch {
                        DocumentIntake.delete(context, d)
                        haptics.confirm()
                        onDeleted()
                    }
                })
            },
            dismissButton = { LinkButton(text = "Cancel", onClick = { confirmDelete = false }) }
        )
    }
}

private fun renderPdf(cacheDir: File, bytes: ByteArray): List<ImageBitmap> {
    val tmp = File.createTempFile("view", ".pdf", cacheDir)
    try {
        tmp.writeBytes(bytes)
        ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { r ->
                return (0 until minOf(r.pageCount, 10)).map { i ->
                    r.openPage(i).use { page ->
                        val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                        bmp.eraseColor(Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bmp.asImageBitmap()
                    }
                }
            }
        }
    } finally {
        tmp.delete()
    }
}
