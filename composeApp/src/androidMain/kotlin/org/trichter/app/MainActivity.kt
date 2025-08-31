package org.trichter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.trichter.app.service.initPreferencesDataStore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initPreferencesDataStore(applicationContext)


        setContent {
            App()
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}