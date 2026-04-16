package com.example.vinilosapp.testing

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    val countingIdlingResource = CountingIdlingResource("album_repository_calls")

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}
