package com.done.core.presentation.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import new_partner_app.composecore.generated.resources.Res
import new_partner_app.composecore.generated.resources.poppins_bold
import new_partner_app.composecore.generated.resources.poppins_light
import new_partner_app.composecore.generated.resources.poppins_medium
import new_partner_app.composecore.generated.resources.poppins_regular
import new_partner_app.composecore.generated.resources.poppins_semibold
import org.jetbrains.compose.resources.Font


@Composable
fun dispalayFontFamily() = FontFamily(
    Font(Res.font.poppins_light, weight = FontWeight.Light),
    Font(Res.font.poppins_regular, weight = FontWeight.Normal),
    Font(Res.font.poppins_medium, weight = FontWeight.Medium),
    Font(Res.font.poppins_semibold, weight = FontWeight.SemiBold),
    Font(Res.font.poppins_bold, weight = FontWeight.Bold)
)

@Composable
fun AppTypography() = Typography().run {
    val displayFontFamily = dispalayFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = titleMedium.copy(
            fontFamily = displayFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = displayFontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = displayFontFamily),
        bodySmall = bodySmall.copy(fontFamily = displayFontFamily),
        labelLarge = labelLarge.copy(fontFamily = displayFontFamily),
        labelMedium = labelMedium.copy(fontFamily = displayFontFamily),
        labelSmall = labelSmall.copy(fontFamily = displayFontFamily)
    )
}
