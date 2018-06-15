package com.sethchhim.kuboo_client

/**
 * Instrumented test, which will download on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.sethchhim.kuboo", appContext.packageName)
    }
}
