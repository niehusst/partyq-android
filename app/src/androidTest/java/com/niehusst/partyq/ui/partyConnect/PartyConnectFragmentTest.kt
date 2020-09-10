package com.niehusst.partyq.ui.partyConnect

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.niehusst.partyq.R
import com.niehusst.partyq.services.SpotifyAuthenticator
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class PartyConnectFragmentTest {

    private lateinit var scenario: FragmentScenario<PartyConnectFragment>
    private lateinit var navController: NavController

    @Before
    fun setup() {
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        navController.setGraph(R.navigation.pre_login_nav_graph)

        mockSpotify()

        scenario = launchFragmentInContainer<PartyConnectFragment>(null, R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun startPartyButtonNavigatesOnClick() {
        // WHEN - party_start_button is clicked
        onView(withId(R.id.party_start_button)).perform(click())

        // THEN - navigation to SpotifyLoginFragment is initiated
        assertEquals(R.id.spotifyLoginFragment, navController.currentDestination?.id)
    }

    @Test
    fun joinPartyButtonNavigatesOnClick() {
        // WHEN - party_start_button is clicked
        onView(withId(R.id.party_join_button)).perform(click())

        // THEN - navigation to PartyJoinFragment is initiated TODO:
//        assertEquals(R.id.partyJoinFragment, navController.currentDestination?.id)
    }

    private fun mockSpotify() {
        mockkObject(SpotifyAuthenticator)
        every { SpotifyAuthenticator.authenticateWithSpotfiy(any(), any(), any()) } returns Unit
    }
}
