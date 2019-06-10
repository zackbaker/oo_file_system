package com.zackonengineering.oo_file_system.error

import com.zackonengineering.oo_file_system.exceptions.IllegalFileSystemOperation
import com.zackonengineering.oo_file_system.types.{Entry, File}

import scala.collection.mutable

/**
  * Handles errors
  */
object Handler {
  /**
    * Checks that the path ends in a /
    * @param path
    */
  def checkFullPath(path: String): Unit = {
    if (path.substring(path.length - 1) != "/") {
      throw new Exception("Path must end with a /")
    }
  }

  /**
    * Checks if path exists throws error if it doesn't
    * @param path
    * @param fileStore
    */
  def pathExists(path: String, fileStore: mutable.Map[String, Entry]): Unit = {
    if (!fileStore.keySet.contains(path)) {
      throw IllegalFileSystemOperation("Path does not exist")
    }
  }

  /**
    * Checks if path doesn't exit throws error if it does
    * @param path
    * @param fileStore
    */
  def pathDoesNotExist(path: String, fileStore: mutable.Map[String, Entry]): Unit = {
    if (fileStore.keySet.contains(path)) {
      throw IllegalFileSystemOperation("Path already exists")
    }
  }

  /**
    * Make sure the entry is not being added to a file
    * @param path
    * @param fileStore
    */
  def addToFile(path: String, fileStore: mutable.Map[String, Entry]): Unit = {
    if (fileStore(path).isInstanceOf[File]) {
      throw IllegalFileSystemOperation("You cannot add files or directories to File's")
    }
  }
}
