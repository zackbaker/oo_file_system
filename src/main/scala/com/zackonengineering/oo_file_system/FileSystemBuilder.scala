package com.zackonengineering.oo_file_system

import com.zackonengineering.oo_file_system.error.Handler
import com.zackonengineering.oo_file_system.exceptions.IllegalFileSystemOperation
import com.zackonengineering.oo_file_system.types.{Directory, Drive, Entry, File, Zip}

import scala.collection.mutable

/**
  * The access point for external systems
  * @constructor Instantiates FileStore Class
  */
class FileSystemBuilder {
  val fileStore: mutable.Map[String, Entry] = mutable.Map("/" -> new Entry())

  /**
    * create object
    * @param createType options are drive, directory, zip, or file
    * @param name
    * @param path
    */
  def create(createType: String, name: String, path: String = "/"): Unit = {
    Handler.checkFullPath(path)

    val storeName: String = path + name
    val parent: String = path.substring(0, path.lastIndexOf("/"))

    if (path != "/" && path != "") {
      Handler.pathExists(parent, fileStore)
      Handler.addToFile(parent, fileStore)
    }

    Handler.pathDoesNotExist(storeName, fileStore)

    val obj = createType.toLowerCase() match {
      case "drive" => new Drive().create(name, path)
      case "directory" => new Directory().create(name, path)
      case "zip" => new Zip().create(name, path)
      case "file" => new File().create(name, path)
      case _ => throw IllegalFileSystemOperation("Type is not recognized")
    }

    fileStore(storeName) = obj.asInstanceOf[Entry]
  }

  /**
    * Writes content to files
    * @param path
    * @param content
    */
  def writeToFile(path: String, content: String): Unit = {
    Handler.pathExists(path, fileStore)

    if (!fileStore(path).isInstanceOf[File]) {
      throw IllegalFileSystemOperation("You can only write content to Files")
    }

    fileStore(path).addContent(content)
    adjustSize(content.length, path)
  }

  /**
    * Moves path and all contents
    * @param oldPath
    * @param newPath
    */
  def move(oldPath: String, newPath: String): Unit = {
    val newPathParent: String = newPath.split("/").dropRight(1).mkString("/")
    Handler.pathExists(oldPath, fileStore)
    Handler.pathExists(newPathParent, fileStore)
    Handler.pathDoesNotExist(newPath, fileStore)
    Handler.addToFile(newPathParent, fileStore)

    if (fileStore(oldPath).isInstanceOf[Drive]) {
      throw IllegalFileSystemOperation("Drives cannot be contained by another entity")
    }

    val sizeOfMove: Int = fileStore(oldPath).getSize()

    adjustSize(-sizeOfMove, oldPath.split("/").dropRight(1).mkString("/"))
    adjustSize(sizeOfMove, newPathParent)

    fileStore.par.foreach {
      file: (String, Any) => {
        if (file._1.contains(oldPath)) {
          val newEntryPath: String = file._1.replace(oldPath, newPath)
          val entry: Entry = file._2.asInstanceOf[Entry]
          entry.updateAttribute("path", newEntryPath)
          fileStore -= file._1
          fileStore += (newEntryPath -> entry)
        }
      }
    }
  }

  /**
    * Deletes path and all contents
    * @param path
    */
  def delete(path: String): Unit = {
    Handler.pathExists(path, fileStore)

    val sizeAdjust: Int = fileStore(path).getSize()
    adjustSize(-sizeAdjust, path.split("/").dropRight(1).mkString("/"))

    fileStore.keySet.par.foreach {
      file: String => {
        if (file.contains(path)) {
          fileStore -= file
        }
      }
    }
  }

  /**
    * Helper function to adjust entry size
    * @param size
    * @param path
    */
  private def adjustSize(size: Int, path: String): Unit = {
    var contentSize: Int = size
    val pathCount: List[Int] = (0 until path.count(_ == '/')).toList

    pathCount.foreach {
      count: Int => {
        val newPath: String = path.split("/").dropRight(count).mkString("/")
        contentSize = fileStore(newPath).adjustSize(contentSize)
      }
    }
  }
}