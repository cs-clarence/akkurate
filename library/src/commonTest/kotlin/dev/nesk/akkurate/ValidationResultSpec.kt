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
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.*

class ValidationResultSpec : FunSpec() {
    init {
        test("'Success' can never throw") {
            shouldNotThrowAny {
                ValidationResult.Success(null).orThrow()
            }
        }


        test("'Success' returns the value as-is") {
            val value = object {}
            value shouldBe ValidationResult.Success(value).value
        }


        test("'Success component1' returns the value") {
            val success = ValidationResult.Success(object {})
            success.value shouldBeSameInstanceAs success.component1()
        }


        test("'Failure' always throws and contains the same violations as the result") {
            // Arrange
            val violations = ConstraintViolationSet(emptySet())
            val failure = ValidationResult.Failure(violations)
            // Act & Assert
            val exception = assertFailsWith<ValidationResult.Exception> { failure.orThrow() }
            violations shouldBeSameInstanceAs exception.violations
        }


        test("'Failure' returns the violations as-is") {
            val violations = ConstraintViolationSet(emptySet())
            assertSame(violations, ValidationResult.Failure(violations).violations)
        }


        test("'Failure component1' returns the violations") {
            val failure = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
            assertSame(failure.violations, failure.component1())
        }

        //region Success: tests for "equals()" and `hashCode()`


        test("'Success equals' returns true when all the values are the same") {
            val original = ValidationResult.Success("foo")
            val other = ValidationResult.Success("foo")
            assertTrue(original.equals(other))
        }


        test("'Success equals' returns false when at least one of the values differ (variant 'value')") {
            val original = ValidationResult.Success("foo")
            val other = ValidationResult.Success("bar")
            assertFalse(original.equals(other))
        }


        test("'Success hashCode' returns the same hash when all the values are the same") {
            val original = ValidationResult.Success("foo")
            val other = ValidationResult.Success("foo")
            assertEquals(original.hashCode(), other.hashCode())
        }


        test("'Success hashCode' returns different hashes when at least one of the values differ (variant 'value')") {
            val original = ValidationResult.Success("foo")
            val other = ValidationResult.Success("bar")
            assertNotEquals(original.hashCode(), other.hashCode())
        }

        //endregion

        //region Failure: tests for "equals()" and `hashCode()`


        test("'Failure equals' returns true when all the values are the same") {
            val original =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            val other =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            assertTrue(original.equals(other))
        }


        test("'Failure equals' returns false when at least one of the values differ (variant 'violations')") {
            val original =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            val other = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
            assertFalse(original.equals(other))
        }


        test("'Failure hashCode' returns the same hash when all the values are the same") {
            val original =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            val other =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            assertEquals(original.hashCode(), other.hashCode())
        }


        test("'Failure hashCode' returns different hashes when at least one of the values differ (variant 'violations')") {
            val original =
                ValidationResult.Failure(ConstraintViolationSet(setOf(ConstraintViolation("foo", listOf("bar")))))
            val other = ValidationResult.Failure(ConstraintViolationSet(emptySet()))
            assertNotEquals(original.hashCode(), other.hashCode())
        }

        //endregion
    }
}
