@file:OptIn(ExperimentalAnimationApi::class)

package ch.healthwallet.tabs.vc

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition

object VCTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "QR"
            val icon = rememberVectorPainter(Icons.Outlined.QrCode2)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(screen = VCScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}
