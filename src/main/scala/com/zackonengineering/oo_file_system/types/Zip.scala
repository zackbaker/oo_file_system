package com.zackonengineering.oo_file_system.types

/**
  * Zip object
  */
class Zip extends Entry {
  /**
    * Override create to set type
    * @param name
    * @param path
    * @return Entry object
    */
  override def create(name: String, path: String): Any = {
    super.create(name, path)
    attributes += ("type" -> "zip")
    this
  }

  /**
    * Override size to half it for Zip
    * @param size
    * @return size increase (Zips need to be halfed)
    */
  override def adjustSize(size: Int): Int = {
    val newSize: Int = size / 2
    super.adjustSize(newSize)
  }
}
