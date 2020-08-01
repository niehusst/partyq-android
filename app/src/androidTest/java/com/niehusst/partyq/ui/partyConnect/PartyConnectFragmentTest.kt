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
        navController.setGraph(R.navigation.nav_graph)

        scenario = launchFragmentInContainer<PartyConnectFragment>(null, R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
    }

    @Test
    fun startPartyButtonNavigatesOnClick() {
        // WHEN - party_start_button is clicked
        onView(withId(R.id.party_start_button)).perform(click())

        // THEN - navigation to PartyStartFragment is initiated
        assertEquals(R.id.spotifyLoginFragment, navController.currentDestination?.id)
    }

    @Test
    fun joinPartyButtonNavigatesOnClick() {
        // WHEN - party_start_button is clicked
        onView(withId(R.id.party_join_button)).perform(click())

        // THEN - navigation to PartyStartFragment is initiated TODO:
//        assertEquals(R.id.partyJoinFragment, navController.currentDestination?.id)
    }
}