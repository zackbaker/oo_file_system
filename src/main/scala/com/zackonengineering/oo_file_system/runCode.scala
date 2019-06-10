package com.zackonengineering.oo_file_system

object runCode {
  def main(args: Array[String]): Unit = {
    val fileSystemBuilder: FileSystemBuilder = new FileSystemBuilder()
    fileSystemBuilder.create("drive", "my_drive", "/")
    fileSystemBuilder.create("directory", "my_directory", "/my_drive/")
    fileSystemBuilder.create("zip", "my_zip", "/my_drive/my_directory/")
    fileSystemBuilder.create("file", "my_file", "/my_drive/my_directory/my_zip/")

    fileSystemBuilder.writeToFile("/my_drive/my_directory/my_zip/my_file", "This is my content...")

    fileSystemBuilder.create("drive", "my_second_drive", "/")
    fileSystemBuilder.create("directory", "my_second_directory", "/my_second_drive/")
    fileSystemBuilder.create("file", "my_other_file", "/my_second_drive/my_second_directory/")
    fileSystemBuilder.writeToFile("/my_second_drive/my_second_directory/my_other_file", "This is some mroe contents that needs to go into this file...")
    fileSystemBuilder.move("/my_second_drive/my_second_directory", "/my_drive/my_directory/my_second_directory")

    fileSystemBuilder.create("directory", "delete_directory", "/my_second_drive/")
    fileSystemBuilder.create("file", "delete_file", "/my_second_drive/delete_directory/")
    fileSystemBuilder.writeToFile("/my_second_drive/delete_directory/delete_file", "Contents!!!!")

    var files = fileSystemBuilder.fileStore
//    files.keys.foreach(println)
    files.values.foreach(file => println(file.getAttribute()))

    fileSystemBuilder.delete("/my_second_drive/delete_directory")

    println("-----------------------------------------------------------------------------------------------------------")
    files = fileSystemBuilder.fileStore
    files.values.foreach(file => println(file.getAttribute()))


    // stress test
    val max: Int = 100
    val half: Int = 50
    var loop: List[Int] = (1 to max).toList
    fileSystemBuilder.create("drive", "foobar")
    fileSystemBuilder.create("drive", "bar")
    println("Creating Files\r\n")
    loop.foreach {
      count: Int => {
        fileSystemBuilder.create("file", s"file_$count", "/foobar/")
        fileSystemBuilder.writeToFile(s"/foobar/file_$count", "123456789")
        print(".")
      }
    }

    println("\r\nresults:")
    println(fileSystemBuilder.fileStore("/bar").getAttribute())
    println(fileSystemBuilder.fileStore("/foobar").getAttribute())

    loop = (1 to half).toList
    println("\r\nMoving Files\r\n")
    loop.foreach {
      count: Int => {
        print(".")
        try {
          fileSystemBuilder.move(s"/foobar/file_$count", s"/bar/file_$count")
        } catch {
          case _: Throwable => throw new Exception(s"Paths: /foobar/file_$count to /bar/file_$count")
        }
      }
    }

    println("\r\nresults:")
    println(fileSystemBuilder.fileStore("/bar").getAttribute())
    println(fileSystemBuilder.fileStore("/foobar").getAttribute())
  }
}
