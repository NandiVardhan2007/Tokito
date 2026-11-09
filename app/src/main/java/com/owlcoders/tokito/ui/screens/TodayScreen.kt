package com.owlcoders.tokito.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.owlcoders.tokito.db.CapturedEvent
import com.owlcoders.tokito.db.entities.AutomationHistory
import com.owlcoders.tokito.ui.components.AppMenuButton
import com.owlcoders.tokito.ui.components.CommitmentRow
import com.owlcoders.tokito.ui.components.CommitmentTextInset
import com.owlcoders.tokito.ui.components.EmptyState
import com.owlcoders.tokito.ui.components.HeaderStyle
import com.owlcoders.tokito.ui.components.Inset
import com.owlcoders.tokito.ui.components.InsetRow
import com.owlcoders.tokito.ui.components.LargeTitleScaffold
import com.owlcoders.tokito.ui.components.RollingNumber
import com.owlcoders.tokito.ui.components.SwipeAction
import com.owlcoders.tokito.ui.components.SwipeActionBox
import com.owlcoders.tokito.ui.components.insetSection
import com.owlcoders.tokito.ui.components.skeletonSection
import com.owlcoders.tokito.ui.theme.Tokito
import com.owlcoders.tokito.ui.theme.TokitoColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Today: what needs you, and what Tokito did about it.
 *
 * The large title carries the date above it and the live counts under it, so the screen answers
 * "how much is waiting" before any row is read. Commitments are split by state (urgent, then the
 * rest) rather than listed as one undifferentiated feed. The menu holds Settings and the rest.
 */
@Composable
fun TodayScreen(
    events: List<CapturedEvent>,
    history: List<AutomationHistory>,
    onDone: (CapturedEvent) -> Unit,
    onOpenHistory: () -> Unit,
    loading: Boolean = false
) {
    val colors = Tokito.colors
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("EEEE d MMMM", Locale.getDefault()).format(Date()) }
    val urgent = events.filter { it.urgency.equals("high", ignoreCase = true) }
    val rest = events.filterNot { it.urgency.equals("high", ignoreCase = true) }
    val sourceNames = remember(events) { events.associate { it.id to appLabel(context, it.sourceApp) } }

    LargeTitleScaffold(
        title = "Today",
        eyebrow = today,
        subtitle = { if (!loading) TodayCounts(total = events.size, urgent = urgent.size) },
        actions = { AppMenuButton() }
    ) {
        if (loading) {
            skeletonSection("today-loading", rows = 3)
            return@LargeTitleScaffold
        }
        if (events.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = Icons.Rounded.TaskAlt,
                    title = "Nothing needs you",
                    message = "Tokito reads your notifications and puts anything you need to act on here."
                )
            }
        }

        commitmentSection("urgent", "Urgent", urgent, sourceNames, onDone)
        commitmentSection("coming-up", if (urgent.isEmpty()) "Needs you" else "Coming up", rest, sourceNames, onDone)

        if (history.isNotEmpty()) {
            val recent = history.take(3)
            insetSection(
                key = "did",
                header = "What Tokito did",
                headerStyle = HeaderStyle.Prominent,
                headerAction = if (history.size > recent.size) "See all" to onOpenHistory else null
            ) {
                rows(recent, key = { it.id }, separatorInset = Inset.iconInset) { item ->
                    ActionRow(item, colors)
                }
            }
        }
    }
}

@Composable
private fun TodayCounts(total: Int, urgent: Int) {
    val colors = Tokito.colors
    val style = MaterialTheme.typography.bodyMedium
    if (total == 0) {
        Text("You're all caught up", style = style, color = colors.textMid)
        return
    }
    // Numbers roll when they change, so a new capture or a completion is seen happening.
    val spoken = if (urgent > 0) "$total need you, $urgent urgent" else "$total need you"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clearAndSetSemantics { contentDescription = spoken }
    ) {
        RollingNumber(total, style, colors.textMid)
        Text(" need you", style = style, color = colors.textMid)
        if (urgent > 0) {
            Text(" · ", style = style, color = colors.textMid)
            RollingNumber(urgent, style, colors.danger)
            Text(" urgent", style = style, color = colors.danger)
        }
    }
}

private fun LazyListScope.commitmentSection(
    key: String,
    header: String,
    events: List<CapturedEvent>,
    sourceNames: Map<Int, String>,
    onDone: (CapturedEvent) -> Unit
) {
    if (events.isEmpty()) return
    insetSection(key = key, header = header, headerStyle = HeaderStyle.Prominent) {
        rows(events, key = { it.id }, separatorInset = CommitmentTextInset) { event ->
            // Swipe right to complete, as the circle does for a tap.
            SwipeActionBox(
                startAction = SwipeAction(
                    label = "Done",
                    icon = Icons.Rounded.CheckCircle,
                    tint = Tokito.colors.success,
                    removes = true,
                    onCommit = { onDone(event) }
                )
            ) {
                CommitmentRow(event = event, sourceName = sourceNames[event.id].orEmpty(), onDone = { onDone(event) })
            }
        }
    }
}

/** One executed action: what, when, and the outcome only when it was not a plain success. */
@Composable
fun ActionRow(item: AutomationHistory, colors: TokitoColors) {
    val (icon, tint, label) = outcomeLook(outcomeOf(item.result), colors)
    InsetRow(
        title = humaniseAction(item.actionType),
        subtitle = listOfNotNull(
            relativeTime(item.executedAt) + if (item.userConfirmed) " · Confirmed by you" else "",
            actionDetail(item)
        ).joinToString("\n"),
        subtitleLines = 3,
        icon = icon,
        iconTint = tint,
        value = label,
        valueColor = tint
    )
}

private fun outcomeLook(outcome: Outcome, colors: TokitoColors): Triple<ImageVector, Color, String?> = when (outcome) {
    Outcome.Success -> Triple(Icons.Rounded.CheckCircle, colors.success, null)
    Outcome.Failed -> Triple(Icons.Rounded.Error, colors.danger, "Failed")
    Outcome.Cancelled -> Triple(Icons.Rounded.Block, colors.warning, "Cancelled")
    Outcome.Pending -> Triple(Icons.Rounded.Schedule, colors.textMid, "Pending")
    Outcome.Other -> Triple(Icons.Rounded.Info, colors.info, null)
}
