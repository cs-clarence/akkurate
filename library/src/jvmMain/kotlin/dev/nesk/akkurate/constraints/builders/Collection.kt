package dev.nesk.akkurate.constraints.builders

import dev.nesk.akkurate.constraints.Constraint
import dev.nesk.akkurate.validatables.Validatable

public fun <T> Validatable<out Collection<T>?>.minSize(length: Int): Constraint = TODO()
public fun <T> Validatable<out Collection<T>?>.maxSize(length: Int): Constraint = TODO()