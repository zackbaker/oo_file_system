package com.zackonengineering.oo_file_system.types

import com.zackonengineering.oo_file_system.exceptions.IllegalFileSystemOperation

/**
  * Drive object
  */
class Drive extends Entry {
  /**
    * Overrides create functionality
    * @param name
    * @param path
    * @return Entry object
    */
  override def create(name: String, path: String): Drive = {
    if (path != "" && path != "/") {
      throw IllegalFileSystemOperation("Drives cannot be contained by another entity")
    }

    val fullPath = path + name
    attributes += ("type" -> "drive", "name" -> name, "path" -> fullPath)
    this
  }
}
