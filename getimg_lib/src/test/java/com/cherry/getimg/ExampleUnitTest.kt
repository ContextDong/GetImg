package com.cherry.getimg

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        lambdaReturn(666)
    }

    fun lambdaReturn(num: Int) {

        num.run {

            if (this == 666) {
                return
            }
            println("哈哈哈")
        }

    }


    fun lambdaReturn(str: String): Boolean {
        str.apply {
            return this == "hhh"
        }
        return false
    }


}