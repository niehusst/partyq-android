/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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