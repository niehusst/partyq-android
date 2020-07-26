package com.niehusst.partyq.ui.mainActivity

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.niehusst.partyq.R
import com.niehusst.partyq.ui.partyConnect.PartyConnectFragment
import io.mockk.mockk
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var navController: NavController

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        navController = mockk<NavController>()
        scenario.onActivity {
            Navigation.setViewNavController(it.nav_host_fragment!!, navController)
        }
    }

    @Test
    fun toolbarAboutNavigatesOnClick() {
        // GIVEN - the Toolbar overflow menu is open
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // WHEN - about_item is clicked
        onView(withText("About")).perform(click())

        // THEN - navigation to AboutFragment is initiated
//        verify { TODO:
//            navController.navigate(
//                PartyConnectFragmentDirections.actionPartyConnectFragmentToPartyStartFragment()
//            )
//        }
    }

    @Test
    fun toolbarLegalNavigatesOnClick() {
        // GIVEN - the Toolbar overflow menu is open
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // WHEN - legal_item is clicked
        onView(withText("Legal")).perform(click())

        // THEN - navigation to LegalFragment is initiated
//        verify { TODO:
//            navController.navigate(
//                PartyConnectFragmentDirections.actionPartyConnectFragmentToPartyStartFragment()
//            )
//        }
    }
}