/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nesk.akkurate

import dev.nesk.akkurate.constraints.ConstraintViolation
import dev.nesk.akkurate.constraints.ConstraintViolationSet
import kotlin.test.*

class ValidationResultTest {
    @Test
    fun `'Success' can never throw`() {
        assertDoesNotThrow {
            ValidationResult.Success(null).orThrow()
        }
    }

    @Test
    fun `'Success' returns the value as-is`() {
        val value = object {}
        assertSame(value, ValidationResult.Success(value).value)
    }

    @Test
    fun `'Success component1' returns the value`() {
        val success = ValidationResult.Success(object {})
        assertSame(success.value, success.component1())
    }

    @Test
    fun `'Failure' always throws and contains the same violations as the result`() {
        // Arrange
        val violations = ConstraintViolationSet(emptySet())
        val failure = ValidationResult.Failure(violations)
        // Act & Assert
        val exception = assertThrows<ValidationResult.Exception> { failure.orThrow() }
        assertSame(violations, exception.violations)
    }

    @Test
    fun `'Failure' returns the violations as-is`() {
        val violations = ConstraintViolationSet(emptySet())
        assertSame(violations, ValidationResult.Failure(violations).violations)
    }

    @Test
    fun `'Failure component1' returns the violations`() {
        val failure = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
        assertSame(failure.violations, failure.component1())
    }

    //region Success: tests for `equals()` and `hashCode()`

    @Test
    fun `'Success equals' returns true when all the values are the same`() {
        val original = ValidationResult.Success("foo")
        val other = ValidationResult.Success("foo")
        assertTrue(original.equals(other))
    }

    @Test
    fun `'Success equals' returns false when at least one of the values differ (variant 'value')`() {
        val original = ValidationResult.Success("foo")
        val other = ValidationResult.Success("bar")
        assertFalse(original.equals(other))
    }

    @Test
    fun `'Success hashCode' returns the same hash when all the values are the same`() {
        val original = ValidationResult.Success("foo")
        val other = ValidationResult.Success("foo")
        assertEquals(original.hashCode(), other.hashCode())
    }

    @Test
    fun `'Success hashCode' returns different hashes when at least one of the values differ (variant 'value')`() {
        val original = ValidationResult.Success("foo")
        val other = ValidationResult.Success("bar")
        assertNotEquals(original.hashCode(), other.hashCode())
    }

    //endregion

    //region Failure: tests for `equals()` and `hashCode()`

    @Test
    fun `'Failure equals' returns true when all the values are the same`() {
        val original = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        val other = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        assertTrue(original.equals(other))
    }

    @Test
    fun `'Failure equals' returns false when at least one of the values differ (variant 'violations')`() {
        val original = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        val other = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
        assertFalse(original.equals(other))
    }

    @Test
    fun `'Failure hashCode' returns the same hash when all the values are the same`() {
        val original = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        val other = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        assertEquals(original.hashCode(), other.hashCode())
    }

    @Test
    fun `'Failure hashCode' returns different hashes when at least one of the values differ (variant 'violations')`() {
        val original = ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
        val other = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
        assertNotEquals(original.hashCode(), other.hashCode())
    }

    //endregion
}
