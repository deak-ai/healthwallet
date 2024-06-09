package ch.healthwallet.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.healthwallet.data.chmed16a.MedicamentRefDataDTO
import ch.healthwallet.tabs.vc.VCScreenModel

@Composable
fun VcTile(
    medRefData: MedicamentRefDataDTO
) {
    VcTileContent(
        medicament = medRefData,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun SelectableVcTile(
    selection: VCScreenModel.PrescriptionSelection,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onSelected() }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        VcTileContent(
            medicament = selection.medicament,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
fun VcTileContent(
    medicament: MedicamentRefDataDTO,
    modifier: Modifier = Modifier,
    additionalContent: @Composable () -> Unit = {}
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainerLow

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(15.dp),
        border = CardDefaults.outlinedCardBorder(true)
    ) {
        Column(
            modifier = Modifier
                .background(containerColor)
                .padding(16.dp)
        ) {
            additionalContent()
            Text(
                text = medicament.nameDe,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = medicament.authHolderName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
