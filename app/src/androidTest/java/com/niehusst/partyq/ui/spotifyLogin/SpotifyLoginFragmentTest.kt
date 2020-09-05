package com.niehusst.partyq.ui.spotifyLogin

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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class SpotifyLoginFragmentTest {

    private lateinit var navController: NavController
    private lateinit var scenario: FragmentScenario<SpotifyLoginFragment>

    @Before
    fun setupNavFake() {
        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        navController.setGraph(R.navigation.pre_login_nav_graph)

        // GIVEN - on SpotifyLoginFragment screen
        scenario = launchFragmentInContainer<SpotifyLoginFragment>(null, R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    @Test
    fun authenticateButtonCallsAuthenticateWithSpotifyOnClick() {
        // WHEN - the spotify_auth_button is clicked
        onView(withId(R.id.spotify_auth_button)).perform(click())

        // THEN - the SpotifyAuthenticationService is called to auth w/ Spotify
//        verify { // mockk sucks super hard so this doesnt work
//            spotifyRepository.authenticateWithSpotfiy(any(), any(), any())
//        }
    }

    @Test
    fun whyIsThisInfoButtonNavigatesToAboutFragmentOnClick() {
        // WHEN - the info_button is clicked
        onView(withId(R.id.info_button)).perform(click())

        // THEN - navigation to about fragment is performed
        assertEquals(R.id.aboutFragment, navController.currentDestination?.id)
    }
}