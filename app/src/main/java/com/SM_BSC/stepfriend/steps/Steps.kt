/**
 * @author 21005729 Saul Maylin
 * @since 08/09/2025
 * @version v1.0
 * Steps Class
 */

package com.SM_BSC.stepfriend.steps

import kotlin.math.sqrt

class Steps {
    // Blueprint Variables/Properties
    var stepCount = 0
        private set

    var lastStep = System.currentTimeMillis()
        private set

    // Constructor
    constructor() {}

    /**
     * Increment the step count by one
     */

    fun incrementStep() {
        this.stepCount++
    }

    /**
     * Reset the step count to zero
     */
    fun resetSteps() {
        this.stepCount = 0
    }

    /**
     * Calculate if a step has been taken based on accelerometer data
     * @param x Double - x axis acceleration
     * @param y Double - y axis acceleration
     * @param z Double - z axis acceleration
     * @return Void
     */
    fun calculateStep(x: Double, y: Double, z: Double) {
        val threshold = 2.5 // For checking if movement has been made.
        val gravity = 9.81 // Earth Gravity. m/s^2

        // Calculate Magnitude. Square root of x^2 + y^2 + z^2
        val magnitude: Double = sqrt(((x * x) + (y * y) + (z * z)))
        println("mag = $magnitude")

        // Apply Gravity.
        val newMagnitude = magnitude - gravity
        println("new mag = $newMagnitude")

        // Check if the threshold has been crossed.
        if (newMagnitude > threshold) {
            println("Step detected")

            // Check if 1000ms has passed since last step to avoid multiple steps being counted.
            if (System.currentTimeMillis() - lastStep > 1000) {
                println("Step counted")

                // update last step time then count step.
                lastStep = System.currentTimeMillis()
                incrementStep()

            }
        } else {
            println("No step detected")
        }
    }
}