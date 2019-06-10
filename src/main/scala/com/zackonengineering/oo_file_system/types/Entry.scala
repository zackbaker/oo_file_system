package com.zackonengineering.oo_file_system.types

import com.zackonengineering.oo_file_system.exceptions.IllegalFileSystemOperation

import scala.collection.mutable

/**
  * Parent class to all Entries
  * @constructor Creates attributes map
  */
class Entry {
  protected val attributes: mutable.Map[String, Any] = mutable.HashMap(
    "type" -> "",
    "name" -> "",
    "path" -> "",
    "size" -> 0,
    "contents" -> mutable.HashMap
  )

  /**
    * Creates Entry
    * @param name
    * @param path
    * @return Entry object
    */
  def create(name: String, path: String): Any = {
    if (path.split("/").length == 0) {
      throw IllegalFileSystemOperation("Must be contained by another object (Drive, Directory, Zip)")
    }

    val fullPath = path + name
    attributes += ("name" -> name, "path" -> fullPath)
  }

  // TODO: Is this neeeded?
  /**
    * Update object attribute
    * @param attribute
    * @param value
    */
  def updateAttribute(attribute: String, value: Any): Unit = {
    attributes(attribute) = value
  }

  /**
    * add content to object
    * @param path
    */
  def addContent(path: String): Unit = {
    attributes("contents") = path
  }

  /**
    * Increases size of Entry
    * @param size
    * @return size increase (Zips need to be halfed)
    */
  def adjustSize(size: Int): Int = {
    attributes("size") = attributes("size").asInstanceOf[Int] + size
    size
  }

  def getSize(): Int = {
    attributes("size").asInstanceOf[Int]
  }

  /**
    * get attribute by name
    * @param attribute options type, name, size, contents, leave blank to get all attributes
    * @return attribute requested
    */
  def getAttribute(attribute: String = ""): Any = {
    if (attribute.isEmpty) {
      attributes
    } else {
      attributes(attribute)
    }
  }
}
