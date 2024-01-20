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

package dev.nesk.akkurate.constraints

import dev.nesk.akkurate.Configuration
import dev.nesk.akkurate.ValidationResult
import dev.nesk.akkurate._test.Validatable
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf

class ConstraintRegistrySpec : FunSpec() {
    init {

        test("calling 'register' with a satisfied constraint leaves the collection empty") {
            // Arrange
            val constraint = Constraint(true, Validatable(null))
            val registry = ConstraintRegistry(Configuration())
            // Act
            registry.register(constraint)
            // Assert
            withClue("The collection is empty") { registry.toSet().isEmpty().shouldBeTrue() }
        }


        test("calling 'register' with an unsatisfied constraint adds it to the collection") {
            // Arrange
            val constraint = Constraint(false, Validatable(null))
            val registry = ConstraintRegistry(Configuration())
            // Act
            registry.register(constraint)
            // Assert
            withClue("The single item is identical to the registered constraint") {
                constraint.shouldBeSameInstanceAs(
                    registry.toSet().single()
                )
            }
        }


        test("calling 'register' with a constraint violation adds it to the collection") {
            // Arrange
            val constraint = Constraint(false, Validatable(null))
            val registry = ConstraintRegistry(Configuration())
            // Act
            registry.register(constraint)
            // Assert
            withClue("The single item is identical to the registered constraint") {
                constraint.shouldBeSameInstanceAs(
                    registry.toSet().single()
                )
            }
        }


        test("calling 'register' multiples times with constraints adds them to the collection if they're unsatisfied") {
            // Arrange
            val constraint1 = Constraint(false, Validatable(null, "path1"))
            val constraint2 = Constraint(true, Validatable(null, "path2"))
            val constraint3 = Constraint(false, Validatable(null, "path3"))
            val registry = ConstraintRegistry(Configuration())
            // Act
            registry.register(constraint1)
            registry.register(constraint2)
            registry.register(constraint3)
            // Assert
            withClue("The collection contains the unsatisfied constraints in the same order") {
                listOf(constraint1, constraint3).shouldContainAll(
                    registry.toSet()
                )
            }

        }


        test("'runWithConstraintRegistry' returns a validation success if all the constraint where satisfied") {
            // Arrange
            val value = object {}
            val constraint1 = Constraint(true, Validatable(null, "path1"))
            val constraint2 = Constraint(true, Validatable(null, "path2"))
            val constraint3 = Constraint(true, Validatable(null, "path3"))
            // Act
            val result = runWithConstraintRegistry(value, Configuration()) {
                it.register(constraint1)
                it.register(constraint2)
                it.register(constraint3)
            }
            // Assert
            result.shouldBeTypeOf<ValidationResult.Success<*>>()
            value.shouldBeSameInstanceAs(result.value)
        }


        test("'runWithConstraintRegistry' returns a validation failure if any constraint was unsatisfied") {
            // Arrange
            val value = object {}
            val constraint1 = Constraint(true, Validatable(null, "path1")) otherwise { "message 1" }
            val constraint2 = Constraint(false, Validatable(null, "path2")) otherwise { "message 2" }
            val constraint3 = Constraint(false, Validatable(null, "path3")) otherwise { "message 3" }
            // Act
            val result = runWithConstraintRegistry(value, Configuration()) {
                it.register(constraint1)
                it.register(constraint2)
                it.register(constraint3)
            }
            // Assert
            result.shouldBeTypeOf<ValidationResult.Failure>()
            listOf("message 2", "message 3").shouldContainAll(result.violations.map { it.message })
        }


        test("definining the root path in the configuration will prepend all the paths in the returned constraint violations") {
            // Arrange
            val config = Configuration { rootPath("foo", "bar") }
            val constraint = Constraint(false, Validatable(null, "baz"))
            // Act
            val result = runWithConstraintRegistry(null, config) { it.register(constraint) }
            // Assert
            withClue("The result is a failure") {
                result.shouldBeInstanceOf<ValidationResult.Failure>()
                listOf("foo", "bar", "baz").shouldBe(result.violations.single().path)
            }
        }


        test("definining the default message in the configuration will replace empty messages in constraints") {
            // Arrange
            val config = Configuration { defaultViolationMessage = "default" }
            val constraint = Constraint(false, Validatable(null))
            // Act
            val result = runWithConstraintRegistry(null, config) { it.register(constraint) }
            // Assert
            withClue("The result is a failure") {
                result.shouldBeInstanceOf<ValidationResult.Failure>()
                result.violations.single().message.shouldBe("default")
            }
        }


        test("calling 'checkFirstViolationConfiguration' with failOnFirstViolation=true skips all the upcoming constraints after the first constraint violation") {
            // Arrange
            val config = Configuration { failOnFirstViolation = true }
            val constraint1 = Constraint(false, Validatable(null)) otherwise { "first message" }
            val constraint2 = Constraint(false, Validatable(null)) otherwise { "second message" }
            // Act
            val result = runWithConstraintRegistry(null, config) {
                it.register(constraint1)
                it.checkFirstViolationConfiguration()
                it.register(constraint2)
            }
            // Assert
            withClue("The result is a failure") {
                result.shouldBeInstanceOf<ValidationResult.Failure>()
                result.violations.single().message.shouldBe("first message")
            }
        }


        test("calling 'checkFirstViolationConfiguration' with failOnFirstViolation=false executes all the constraints before returning a failure") {
            // Arrange
            val config = Configuration { failOnFirstViolation = false }
            val constraint1 = Constraint(false, Validatable(null)) otherwise { "first message" }
            val constraint2 = Constraint(false, Validatable(null)) otherwise { "second message" }
            // Act
            val result = runWithConstraintRegistry(null, config) {
                it.register(constraint1)
                it.checkFirstViolationConfiguration()
                it.register(constraint2)
            }
            // Assert
            withClue("The result is a failure") {
                result.shouldBeInstanceOf<ValidationResult.Failure>()
                result.violations.size.shouldBe(2)
                listOf("first message", "second message").shouldContainAll(result.violations.map { it.message })
            }
        }
    }
}
