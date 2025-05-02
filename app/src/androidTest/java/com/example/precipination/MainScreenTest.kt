package com.example.precipination

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    @Test
    fun invalidZip_showsAlertDialog() {
        composeTestRule.onNodeWithText("Zip Code").performTextInput("") // leave blank
        composeTestRule.onNodeWithText("Submit").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Invalid Zip Code").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Invalid Zip Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("OK").performClick()
    }

    @Test
    fun validZip_displaysWeatherData() {
        composeTestRule.onNodeWithText("Zip Code").performTextInput("55104")
        composeTestRule.onNodeWithText("Submit").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Feels like", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }


        composeTestRule.onNodeWithText("Feels like", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Pressure", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("High", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Low", substring = true).assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Invalid Zip Code").assertCountEquals(0)
    }


}