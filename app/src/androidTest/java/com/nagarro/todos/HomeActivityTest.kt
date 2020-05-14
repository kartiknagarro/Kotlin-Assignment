package com.nagarro.todos

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.nagarro.todos.util.Utils as AppUtil
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.presentation.adapter.TodoListAdapter
import com.nagarro.todos.presentation.ui.home.HomeActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(HomeActivity::class.java, true, false)
    private lateinit var mockWebServer: MockWebServer

    /**
     * Will run before each test
     * */
    @Before
    fun setUp() {
        Intents.init()
        mockWebServer = MockWebServer()
        mockWebServer.start(8888)
        AppUtil.BASE_URL = mockWebServer.url("/").toString()
    }

    /**
     * Check list visibility and match data with displayed list item
     * */
    @Test
    fun listVisibilityTest() {
        init()
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withText("Test Data 1")).check(matches(isDisplayed()))
    }
    /**
     * Check error ui visibility on api failure
     * */
    @Test
    fun apiFailureTest() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal server error"))
        activityRule.launchActivity(Intent())
        onView(withId(R.id.errorText)).check(matches(isDisplayed()))
    }

    /**
     * Scroll to list item after matching given data
     * */
    @Test
    fun dataAssertionTest() {
        init()
        val list = Utils.getList(InstrumentationRegistry.getInstrumentation().context)
        onView(withId(R.id.list))
            .perform(scrollToHolder(viewHolderMatcher(list[3])))
    }

    /**
     * Will release intents and shut down mock server after each test
     * */
    @After
    fun clear(){
        Intents.release()
        if (::mockWebServer.isInitialized)
            mockWebServer.shutdown()
    }

    private fun init() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(Utils.getJsonString(InstrumentationRegistry.getInstrumentation().context))
        )
        activityRule.launchActivity(Intent())
    }

    private fun viewHolderMatcher(item: Todo): Matcher<RecyclerView.ViewHolder> {
        return object :
            BoundedMatcher<RecyclerView.ViewHolder, RecyclerView.ViewHolder>(RecyclerView.ViewHolder::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has view id $description")
            }

            public override fun matchesSafely(holder: RecyclerView.ViewHolder): Boolean {
                return if (holder is TodoListAdapter.ListItemViewHolder) {
                    holder.todo.id == item.id
                            && holder.todo.title == item.title
                            && holder.todo.completed == item.completed
                } else false
            }
        }
    }
}