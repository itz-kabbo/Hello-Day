package com.flux

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.flux.common.createNotificationChannel
import com.flux.plan.ui.PlanViewModel
import com.flux.ritual.ui.RitualsViewModel
import com.flux.ui.foundation.FluxTheme
import com.flux.ui.navigation.FluxNavigationShell
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var keepSplashScreen = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)

        splashScreen.setKeepOnScreenCondition {
            keepSplashScreen.value
        }

        // Animated exit transition for the system splash screen.
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView

            iconView.alpha = 1f
            iconView.scaleX = 1f
            iconView.scaleY = 1f
            iconView.translationY = 0f

            val scaleX = ObjectAnimator.ofFloat(iconView, "scaleX", 1f, 1.05f, 0.95f)
            val scaleY = ObjectAnimator.ofFloat(iconView, "scaleY", 1f, 1.05f, 0.95f)
            val alpha = ObjectAnimator.ofFloat(iconView, "alpha", 1f, 0f)

            AnimatorSet().apply {
                playTogether(scaleX, scaleY, alpha)
                duration = 350L
                interpolator = android.view.animation.PathInterpolator(0.2f, 0f, 0f, 1f)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) = Unit
                    override fun onAnimationEnd(animation: Animator) {
                        splashScreenView.remove()
                    }
                    override fun onAnimationCancel(animation: Animator) {
                        splashScreenView.remove()
                    }
                    override fun onAnimationRepeat(animation: Animator) = Unit
                })
                start()
            }
        }

        enableEdgeToEdge()

        setContent {
            val ritualsViewModel: RitualsViewModel = hiltViewModel()
            val planViewModel: PlanViewModel = hiltViewModel()
            val settingsViewModel: com.flux.settings.SettingsViewModel = hiltViewModel()

            val settings by settingsViewModel.settings.collectAsState()
            val isDark = when (settings.themeMode) {
                com.flux.settings.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                com.flux.settings.ThemeMode.LIGHT -> false
                com.flux.settings.ThemeMode.DARK -> true
            }

            FluxTheme(
                darkTheme = isDark,
                reduceMotion = settings.reduceMotion
            ) {
                FluxNavigationShell(
                    ritualsViewModel = ritualsViewModel,
                    planViewModel = planViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
