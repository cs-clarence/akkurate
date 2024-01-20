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

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank

class ConfigurationSpec : FunSpec() {
    init {

        test("the default configuration is viable") {
            Configuration().let {
                withClue("The default message is not blank") {
                    it.defaultViolationMessage.shouldNotBeBlank()
                }
                withClue("The default root path is empty") {
                    it.rootPath.shouldNotBeEmpty()
                }
                withClue("By default, it doesn't fail on the first constraint") {
                    it.failOnFirstViolation.shouldBeFalse()
                }
            }
        }

        test("all configuration options are customizable") {
            val config = Configuration {
                defaultViolationMessage = "foo"
                rootPath = listOf("bar", "baz")
                failOnFirstViolation = true
            }

            withClue("defaultViolationMessage is customizable") {
                config.defaultViolationMessage.shouldBe("foo")
            }

            withClue("rootPath is customizable") {
                config.rootPath.shouldContainAll("bar", "baz")
            }

            withClue("failOnFirstViolation is customizable") {
                config.failOnFirstViolation.shouldBeTrue()
            }

        }

        test("a new configuration can be generated based from a previous one") {
            // Arrange
            val sourceConfig = Configuration {
                defaultViolationMessage = "foo"
                rootPath = listOf("bar", "baz")
                failOnFirstViolation = true
            }

            // Act
            val alteredConfig = Configuration(sourceConfig) {
                defaultViolationMessage += "_"
                rootPath = rootPath.map { it + "_" }
                failOnFirstViolation = failOnFirstViolation
            }

            // Assert
            withClue("defaultViolationMessage is altered") {
                alteredConfig.defaultViolationMessage.shouldBe("foo_")
            }
            withClue("rootPath is altered") {
                alteredConfig.rootPath.shouldContainAll("bar_", "baz_")
            }
            withClue("failOnFirstViolation is altered") {
                alteredConfig.failOnFirstViolation.shouldBeTrue()
            }
        }

        test("source configurations aren't mutated") {
            // Arrange
            val sourceConfig = Configuration {
                defaultViolationMessage = "foo"
            }

            // Act
            Configuration(sourceConfig) {
                defaultViolationMessage += "_"
            }

            // Assert
             sourceConfig.defaultViolationMessage.shouldBe("foo")
        }

        test("generated configurations cannot be mutated by keeping a reference to the builder") {
            // Arrange
            lateinit var builder: Configuration.Builder
            val config = Configuration {
                builder = this
                defaultViolationMessage = "foo"
            }

            // Act
            builder.defaultViolationMessage = "bar"

            // Assert
            config.defaultViolationMessage.shouldBe("foo")
        }

        test("'rootPath' function defines the property of the same name") {
            val config = Configuration { rootPath("foo", "bar") }
            config.rootPath.shouldContainAll("foo", "bar")
        }

        //region equals/hashCode/toString
        test("'equals' returns true when all the values are the same") {
            Configuration().shouldBe(Configuration())
        }

        test("'equals' returns false when at least one of the values differ (variant 'defaultViolationMessage')") {
            val original = Configuration()
            val other = Configuration { defaultViolationMessage = "foo" }
            original.shouldNotBe(other)
        }


        test("'equals' returns false when at least one of the values differ (variant 'rootPath')") {
            val original = Configuration()
            val other = Configuration { rootPath("foo") }
            original.shouldNotBe(other)
        }


        test("'equals' returns false when at least one of the values differ (variant 'failOnFirstViolation')") {
            val original = Configuration()
            val other = Configuration { failOnFirstViolation = true }
            original.shouldNotBe(other)
        }


        test("'hashCode' returns the same hash when all the values are the same") {
            Configuration().hashCode().shouldBe(Configuration().hashCode())
        }


        test("'hashCode' returns different hashes when at least one of the values differ (variant 'defaultViolationMessage')") {
            val original = Configuration()
            val other = Configuration { defaultViolationMessage = "foo" }
            original.hashCode().shouldNotBe(other.hashCode())
        }


        test("'hashCode' returns different hashes when at least one of the values differ (variant 'rootPath')") {
            val original = Configuration()
            val other = Configuration { rootPath("foo") }
            original.hashCode().shouldNotBe(other.hashCode())
        }


        test("'hashCode' returns different hashes when at least one of the values differ (variant 'failOnFirstViolation')") {
            val original = Configuration()
            val other = Configuration { failOnFirstViolation = true }
            original.hashCode().shouldNotBe(other.hashCode())
        }
    }
    //endregion
}
