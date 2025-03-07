/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.config

import org.jdom.Element
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author Alex Plate
 */
internal class VimState {
  var isIdeaJoinNotified by StateProperty("idea-join")
  var isIdeaPutNotified by StateProperty("idea-put")
  var wasSubscribedToEAPAutomatically by StateProperty("was-automatically-subscribed-to-eap")

  /**
   * First logged version of IdeaVim, or "-1" if no version logged
   */
  var firstIdeaVimVersion: String by StringProperty("first-ideavim-version", "-1")

  fun readData(element: Element) {
    val notifications = element.getChild("notifications")
    map.keys.forEach { name ->
      notifications?.getChild(name)?.getAttributeValue("enabled")?.let {
        map[name] = it.toBoolean()
      }
    }
    stringMap.keys.forEach { name ->
      notifications?.getChild(name)?.getAttributeValue("value")?.let {
        stringMap[name] = it
      }
    }
  }

  fun saveData(element: Element) {
    val notifications = Element("notifications")
    element.addContent(notifications)

    map.forEach { (name, value) ->
      val child = Element(name)
      child.setAttribute("enabled", value.toString())
      notifications.addContent(child)
    }
    stringMap.forEach { (name, value) ->
      val child = Element(name)
      child.setAttribute("value", value)
      notifications.addContent(child)
    }
  }
}

private val map by lazy { mutableMapOf<String, Boolean>() }
private val stringMap by lazy { mutableMapOf<String, String>() }

private class StateProperty(val xmlName: String) : ReadWriteProperty<VimState, Boolean> {

  init {
    map[xmlName] = false
  }

  override fun getValue(thisRef: VimState, property: KProperty<*>): Boolean = map.getOrPut(xmlName) { false }

  override fun setValue(thisRef: VimState, property: KProperty<*>, value: Boolean) {
    map[xmlName] = value
  }
}

private class StringProperty(val propertyName: String, val defaultValue: String) : ReadWriteProperty<VimState, String> {

  init {
    stringMap[propertyName] = defaultValue
  }

  override fun getValue(thisRef: VimState, property: KProperty<*>): String =
    stringMap.getOrPut(propertyName) { defaultValue }

  override fun setValue(thisRef: VimState, property: KProperty<*>, value: String) {
    stringMap[propertyName] = value
  }
}
