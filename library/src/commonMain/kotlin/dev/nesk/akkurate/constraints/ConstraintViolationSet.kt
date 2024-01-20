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

import dev.nesk.akkurate.Path

public class ConstraintViolationSet internal constructor(private val messages: Set<ConstraintViolation>) : Set<ConstraintViolation> by messages {
    public val byPath: Map<Path, Set<ConstraintViolation>> by lazy { messages.groupBy { it.path }.mapValues { it.value.toSet() } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as ConstraintViolationSet

        return messages == other.messages
    }

    override fun hashCode(): Int = messages.hashCode()

    override fun toString(): String = "ConstraintViolationSet(messages=$messages)"

}
