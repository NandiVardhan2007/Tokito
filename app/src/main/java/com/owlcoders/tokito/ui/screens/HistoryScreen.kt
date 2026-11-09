package com.owlcoders.tokito.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.owlcoders.tokito.db.entities.AutomationHistory
import com.owlcoders.tokito.ui.components.EmptyState
import com.owlcoders.tokito.ui.components.Inset
import com.owlcoders.tokito.ui.components.LargeTitleScaffold
import com.owlcoders.tokito.ui.components.LargeTitleSubtitle
import com.owlcoders.tokito.ui.components.insetSection
import com.owlcoders.tokito.ui.components.skeletonSection
import com.owlcoders.tokito.ui.theme.Tokito

/**
 * What Tokito did: every action it took for you, newest first, grouped by day. Pushed from the
 * "See all" on Today.
 */
@Composable
fun HistoryScreen(history: List<AutomationHistory>, loading: Boolean = false) {
    val colors = Tokito.colors
    val failed = history.count { outcomeOf(it.result) == Outcome.Failed }
    val byDay = remember(history) { history.groupBy { dayBucket(it.executedAt) } }

    LargeTitleScaffold(
        title = "What Tokito did",
        subtitle = {
            LargeTitleSubtitle(
                when {
                    history.isEmpty() -> "Nothing yet"
                    failed == 0 -> "${history.size} actions, all went through"
                    else -> "${history.size} actions · $failed didn't go through"
                }
            )
        }
    ) {
        if (loading) {
            skeletonSection("history-loading", rows = 4)
            return@LargeTitleScaffold
        }
        if (history.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = Icons.Rounded.History,
                    title = "Nothing yet",
                    message = "When Tokito opens an app, sets a reminder or sends something for you, it shows up here."
                )
            }
        }
        byDay.forEach { (day, items) ->
            insetSection(key = day, header = day) {
                rows(items, key = { it.id }, separatorInset = Inset.iconInset) { item -> ActionRow(item, colors) }
            }
        }
    }
}
