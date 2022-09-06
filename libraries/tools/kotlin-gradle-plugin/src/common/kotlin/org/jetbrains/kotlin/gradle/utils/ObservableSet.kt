/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

interface ObservableSet<T> : Set<T> {
    fun whenObjectAdded(action: (T) -> Unit)
    fun forAll(action: (T) -> Unit)
}

internal class MutableObservableSet<T>(vararg elements: T) : ObservableSet<T>, MutableSet<T> {
    private val underlying = mutableSetOf(*elements)
    private val whenObjectAddedActions = mutableListOf<(T) -> Unit>()
    private val forAllActions = mutableListOf<(T) -> Unit>()

    override fun whenObjectAdded(action: (T) -> Unit) {
        whenObjectAddedActions.add(action)
    }

    override fun forAll(action: (T) -> Unit) {
        forAllActions.add(action)
        underlying.toList().forEach(action)
    }

    override val size: Int
        get() = underlying.size

    override fun clear() {
        underlying.clear()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val toAdd = elements.toSet() - underlying
        return underlying.addAll(toAdd).also {
            toAdd.forEach { added ->
                whenObjectAddedActions.toList().forEach { action -> action(added) }
                forAllActions.toList().forEach { action -> action(added) }
            }
        }
    }

    override fun add(element: T): Boolean {
        return underlying.add(element).also {
            whenObjectAddedActions.toList().forEach { action -> action(element) }
            forAllActions.toList().forEach { action -> action(element) }
        }
    }

    override fun isEmpty(): Boolean {
        return underlying.isEmpty()
    }

    override fun iterator(): MutableIterator<T> {
        return underlying.iterator()
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return underlying.retainAll(elements)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return underlying.removeAll(elements)
    }

    override fun remove(element: T): Boolean {
        return underlying.remove(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return underlying.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return underlying.contains(element)
    }

    init {
        underlying.addAll(elements)
    }
}
