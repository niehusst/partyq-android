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

package com.niehusst.partyq.ui.mainActivity

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.niehusst.partyq.R
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var nav: NavController

    @Before
    fun setup() {
        nav = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        nav.setGraph(R.navigation.pre_login_nav_graph)

        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity {
            Navigation.setViewNavController(it.nav_host_fragment!!, nav)
        }
    }

    @Test
    fun toolbarAboutNavigatesOnClick() {
        // GIVEN - the Toolbar overflow menu is open
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // WHEN - about_item is clicked
        onView(withText("About")).perform(click())

        // THEN - navigation to AboutFragment is the current location
        assertEquals(R.id.aboutFragment, nav.currentDestination?.id)
    }

    @Test
    fun toolbarLegalNavigatesOnClick() {
        // GIVEN - the Toolbar overflow menu is open
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // WHEN - legal_item is clicked
        onView(withText("Legal")).perform(click())

        // THEN - navigation to LegalFragment is the current location
        assertEquals(R.id.legalFragment, nav.currentDestination?.id)
    }
}
