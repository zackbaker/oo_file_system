package com.zackonengineering.oo_file_system.types

/**
  * File Object
  */
class File extends Entry {
  /**
    * Override create to set type
    * @param name
    * @param path
    * @return Entry object
    */
  override def create(name: String, path: String): Any = {
    super.create(name, path)
    attributes += ("type" -> "file")
    this
  }
}
