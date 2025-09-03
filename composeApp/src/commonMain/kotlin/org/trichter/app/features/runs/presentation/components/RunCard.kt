@file:OptIn(
    ExperimentalUuidApi::class, ExperimentalTime::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package org.trichter.app.features.runs.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nl.jacobras.humanreadable.HumanReadable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.trichter.app.features.runs.data.model.Run
import org.trichter.app.features.runs.data.model.RunData
import org.trichter.app.features.runs.data.model.User
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun RunCard(
    run: Run,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
        ) {
            AsyncImage(
                model = "https://picsum.photos/1000",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Stat(
                    icon = Icons.Default.Settings,
                   statTitle = "Duration",
                    valueText = "${run.data.duration.toFixed(2)}s",
                    color = MaterialTheme.colorScheme.primary
                )
                Stat(
                    icon = Icons.Default.Add,
                    statTitle = "Volume",
                    valueText = "${run.data.volume.toFixed(2)}L",
                    color = MaterialTheme.colorScheme.secondary,

                )
                Stat(
                    icon = Icons.Default.Check,
                    statTitle = "Rate",
                    valueText = "${run.data.rate.toFixed(2)}L/min",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(15.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = run.user?.name ?: "Unkown",
                    style = MaterialTheme.typography.bodyMediumEmphasized
                )
                Text(
                    text = HumanReadable.timeAgo(run.createdAt)
                )
            }
        }

    }

}

@Composable
fun Stat(
    icon: ImageVector,
    statTitle: String,
    valueText: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row() {
            Icon(icon, contentDescription = statTitle, tint = color)
            Spacer(Modifier.size(5.dp))
            Text(
                statTitle,
                color = color
            )

        }
        Text(
            valueText,
            textAlign = TextAlign.Left,
            color = color
        )

    }
}

@Preview
@Composable
fun RunCardPreview() {
    val run = { instant: Instant ->
        Run(
            id = Uuid.random().toString(),
            data = RunData(
                duration = 4.5,
                rate = 4.7,
                volume = 0.7,
            ),
            image = "image",
            createdAt = instant,
            user = User(
                id = Uuid.random().toString(),
                name = "Max Mustermann",
                username = "maxmustermann"
            ),
            userId = null
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(100) {
            RunCard(run(randomInstant((Clock.System.now() - 2.days), Clock.System.now())))
        }
    }
}

fun randomInstant(start: Instant, end: Instant, random: Random = Random): Instant {
    val startMs = start.toEpochMilliseconds()
    val endMs = end.toEpochMilliseconds()
    val randomMs = random.nextLong(from = startMs, until = endMs + 1)
    return Instant.fromEpochMilliseconds(randomMs)
}

private val POW10: LongArray = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L,
    10_000_000L, 100_000_000L, 1_000_000_000L,
    10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
    10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L,
    10_000_000_000_000_000L, 100_000_000_000_000_000L, 1_000_000_000_000_000_000L
)

private fun roundHalfAwayFromZero(x: Double): Long =
    if (x >= 0.0) floor(x + 0.5).toLong() else ceil(x - 0.5).toLong()

fun Double.toFixed(decimals: Int): String {
    require(decimals >= 0 && decimals < POW10.size) { "decimals out of range" }
    val pow = POW10[decimals].toDouble()
    val scaled = roundHalfAwayFromZero(this * pow)
    val sign = if (scaled < 0) "-" else ""
    val absScaled = abs(scaled)
    val intPart = (absScaled / POW10[decimals]).toString()
    return if (decimals == 0) {
        "$sign$intPart"
    } else {
        val fracPart = (absScaled % POW10[decimals]).toString().padStart(decimals, '0')
        "$sign$intPart.$fracPart"
    }
}

fun Float.toFixed(decimals: Int): String = this.toDouble().toFixed(decimals)