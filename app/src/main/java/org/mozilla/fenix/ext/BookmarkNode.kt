/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.ext

import android.content.Context
import mozilla.components.browser.storage.sync.PlacesBookmarksStorage
import mozilla.components.concept.storage.BookmarkNode

val Context.bookmarkStorage: PlacesBookmarksStorage
    get() = components.core.bookmarksStorage

/**
 * Removes [children] from [BookmarkNode.children] and returns the new modified [BookmarkNode].
 */
operator fun BookmarkNode.minus(children: Set<BookmarkNode>): BookmarkNode {
    val removedChildrenGuids = children.map { it.guid }.toSet()
    return this.copy(children = this.children?.filterNot { removedChildrenGuids.contains(it.guid) })
}

/**
 * Returns `true` if at least one [BookmarkNode] matches the given [predicate].
 */
fun BookmarkNode.any(predicate: (BookmarkNode) -> Boolean): Boolean {
    tailrec fun match(
        nodes: MutableList<BookmarkNode>,
        predicate: (BookmarkNode) -> Boolean,
        index: Int = 0
    ): Boolean {
        return when {
            index > nodes.lastIndex -> false
            predicate(nodes[index]) -> true
            else -> {
                val testedNode = nodes[index]
                nodes.addAll(testedNode.children.orEmpty())
                match(nodes, predicate, index + 1)
            }
        }
    }
    return match(mutableListOf(this), predicate)
}