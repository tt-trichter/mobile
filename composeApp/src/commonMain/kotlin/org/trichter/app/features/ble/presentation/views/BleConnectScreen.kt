@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package org.trichter.app.features.ble.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.juul.kable.PlatformAdvertisement
import org.trichter.app.features.ble.domain.models.id
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun BleConnectScreen(
    advertisements: List<PlatformAdvertisement>,
    onConnectClick: (PlatformAdvertisement) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth().padding(50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(15.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingIndicator()
                Text(
                    "Scanning..."
                )


                if (advertisements.isNotEmpty()) {
                    Spacer(Modifier.size(15.dp))
                    HorizontalDivider()
                    Spacer(Modifier.size(15.dp))
                    LazyColumn(
//                        modifier = Modifier.fillMaxHeight(0.7f)
                    ) {
                        items(advertisements, key = { it.id().toString() }) { ad ->
                            ScanResult(
                                advertisement = ad,
                                onConnectClick = onConnectClick
                            )
                        }
                    }
                    Spacer(Modifier.size(15.dp))
                    HorizontalDivider()
                    Spacer(Modifier.size(15.dp))
                    Text("Click on an entry to connect"      ,
                        style = MaterialTheme.typography.labelMedium

                    )
                }
            }
        }
    }

}

@Composable
fun ScanResult(
    advertisement: PlatformAdvertisement,
    onConnectClick: (PlatformAdvertisement) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    modifier = Modifier.padding(15.dp),
                    text = advertisement.name ?: "Unnamed"
                )
            },
            supportingContent = {
            },
            tonalElevation = 5.dp,
            shadowElevation = 5.dp,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onConnectClick(advertisement) }
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }

}