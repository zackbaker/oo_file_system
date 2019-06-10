package com.zackonengineering.oo_file_system

import com.zackonengineering.oo_file_system.types.{Directory, Drive, Entry, File, Zip}
import org.scalatest.{BeforeAndAfter, FunSuite}

class FileSystemBuilderTests extends FunSuite with BeforeAndAfter {
  var fileSystemBuilder: FileSystemBuilder = _

  before {
    fileSystemBuilder = new FileSystemBuilder()
  }

  test("Making sure the root directory exists") {
    assert(fileSystemBuilder.fileStore.contains("/"))
  }

  test("Exceptions is throw with invalid type creation") {
    intercept[Exception] {
      fileSystemBuilder.create("foo", "bar")
    }
  }

  test("Adding a drive with a path") {
    fileSystemBuilder.create("drive", "my_drive", "/")
    assert(fileSystemBuilder.fileStore.contains("/my_drive"))
  }

  test("Adding a drive without a path") {
    fileSystemBuilder.create("drive", "my_drive")
    assert(fileSystemBuilder.fileStore.contains("/my_drive"))
    assert(fileSystemBuilder.fileStore("/my_drive").isInstanceOf[Drive])
  }

  test("Can't add a drive to another entity") {
    fileSystemBuilder.create("drive", "my_drive")

    intercept[Exception] {
      fileSystemBuilder.create("drive", "fail", "/my_drive")
    }
  }

  test("Directory can be created") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("directory", "bar", "/foo/")

    assert(fileSystemBuilder.fileStore.contains("/foo"))
    assert(fileSystemBuilder.fileStore.contains("/foo/bar"))
    assert(fileSystemBuilder.fileStore("/foo/bar").isInstanceOf[Directory])
  }

  test("Directory cannot be top level and must be contained within another Entity") {
    intercept[Exception] {
      fileSystemBuilder.create("directory", "foo", "/")
    }
  }

  test("Zip can be created") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")

    assert(fileSystemBuilder.fileStore.contains("/foo/bar"))
    assert(fileSystemBuilder.fileStore("/foo/bar").isInstanceOf[Zip])
  }

  test("Zip cannot be top level and must be contained within another Entity") {
    intercept[Exception] {
      fileSystemBuilder.create("zip", "foo", "/")
    }
  }

  test("File can be created") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("file", "bar", "/foo/")

    assert(fileSystemBuilder.fileStore.contains("/foo/bar"))
    assert(fileSystemBuilder.fileStore("/foo/bar").isInstanceOf[File])
  }

  test("File cannot be top level and must be contained within another Entity") {
    intercept[Exception] {
      fileSystemBuilder.create("file", "foo", "/")
    }
  }

  test("File cannot contain any other entities") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("file", "bar", "/foo/")

    intercept[Exception] {
      fileSystemBuilder.create("file", "foobar", "/foo/bar/")
    }
  }

  test("Nothing can be created without an existing parent entity") {
    intercept[Exception] {
      fileSystemBuilder.create("file", "foobar", "foo/bar/")
    }
  }

  test("Writing to a file works") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("file", "bar", "/foo/")
    fileSystemBuilder.writeToFile("/foo/bar", "Content!")

    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 8)
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 8)
  }

  test("You cannot write to a Drive") {
    fileSystemBuilder.create("drive", "foo")

    intercept[Exception] {
      fileSystemBuilder.writeToFile("/foo", "bar")
    }
  }

  test("You cannot write to a directory") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("directory", "bar", "/foo/")

    intercept[Exception] {
      fileSystemBuilder.writeToFile("/foo/bar", "bar")
    }
  }

  test("You cannot write to a zip") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")

    intercept[Exception] {
      fileSystemBuilder.writeToFile("/foo/bar", "bar")
    }
  }

  test("When you write to a file in a zip folder the size decreases by half") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")
    fileSystemBuilder.create("file", "foobar", "/foo/bar/")
    fileSystemBuilder.writeToFile("/foo/bar/foobar", "123456")

    assert(fileSystemBuilder.fileStore("/foo/bar/foobar").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 3)
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 3)
  }

  test("Can't write to a file that doesn't exist") {
    intercept[Exception] {
      fileSystemBuilder.writeToFile("/foo/bar", "123456")
    }
  }

  test("Move doesn't work if old path doesn't exist") {
    fileSystemBuilder.create("drive", "foo")

    intercept[Exception] {
      fileSystemBuilder.move("/bar/foo", "/foo/bar")
    }
  }

  test("Move doesn't work when the new path doesn't exist") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("directory", "bar", "/foo/")

    intercept[Exception] {
      fileSystemBuilder.move("/foo/bar", "bar/bar")
    }
  }

  test("Move won't overwrite a file") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("file", "bar", "/foo/")
    fileSystemBuilder.create("drive", "bar")
    fileSystemBuilder.create("file", "bar", "/bar/")

    intercept[Exception] {
      fileSystemBuilder.move("/foo/bar", "/bar/bar")
    }
  }

  test("Move works properly") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("directory", "bar", "/foo/")
    fileSystemBuilder.create("file", "foobar", "/foo/bar/")
    fileSystemBuilder.writeToFile("/foo/bar/foobar", "123456")
    fileSystemBuilder.create("drive", "bar")

    assert(fileSystemBuilder.fileStore.contains("/foo/bar/foobar"))

    fileSystemBuilder.move("/foo/bar/foobar", "/bar/foobar")

    assert(fileSystemBuilder.fileStore.contains("/bar/foobar"))
    assert(!fileSystemBuilder.fileStore.contains("foo/bar/foobar"))
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 0)
    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 0)
    assert(fileSystemBuilder.fileStore("/bar").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/bar/foobar").getSize() == 6)
  }

  test("Move works properly out of zip files") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")
    fileSystemBuilder.create("directory", "foobar", "/foo/bar/")
    fileSystemBuilder.create("file", "file", "/foo/bar/foobar/")
    fileSystemBuilder.writeToFile("/foo/bar/foobar/file", "123456")

    fileSystemBuilder.create("drive", "bar")

    fileSystemBuilder.move("/foo/bar/foobar", "/bar/foobar")

    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 0)
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 0)
    assert(fileSystemBuilder.fileStore("/bar/foobar/file").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/bar/foobar").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/bar").getSize() == 6)
  }

  test("Move works properly into zip files") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")
    fileSystemBuilder.create("drive", "bar")
    fileSystemBuilder.create("directory", "foobar", "/bar/")
    fileSystemBuilder.create("file", "file", "/bar/foobar/")
    fileSystemBuilder.writeToFile("/bar/foobar/file", "123456")

    fileSystemBuilder.move("/bar/foobar", "/foo/bar/foobar")

    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 3)
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 3)
    assert(fileSystemBuilder.fileStore("/foo/bar/foobar/file").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/foo/bar/foobar").getSize() == 6)
    assert(fileSystemBuilder.fileStore("/bar").getSize() == 0)
  }

  test("Cannot delete path that doesn't exist") {
    intercept[Exception] {
      fileSystemBuilder.delete("/foo/bar")
    }
  }

  test("Delete works") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.delete("/foo")
    assert(!fileSystemBuilder.fileStore.contains("/foo"))
  }

  test("Deletes child directories") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("directory", "bar", "/foo/")
    fileSystemBuilder.delete("/foo")

    assert(!fileSystemBuilder.fileStore.contains("/foo"))
    assert(!fileSystemBuilder.fileStore.contains("/foo/bar"))
  }

  test("Delete updates the size properly without zip files") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("file", "bar", "/foo/")
    fileSystemBuilder.writeToFile("/foo/bar", "123")
    fileSystemBuilder.delete("/foo/bar")
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 0)
  }

  test("Delete updates the size properly with zip files") {
    fileSystemBuilder.create("drive", "foo")
    fileSystemBuilder.create("zip", "bar", "/foo/")
    fileSystemBuilder.create("file", "file", "/foo/bar/")
    fileSystemBuilder.writeToFile("/foo/bar/file", "1234")
    fileSystemBuilder.delete("/foo/bar/file")

    assert(fileSystemBuilder.fileStore("/foo/bar").getSize() == 0)
    assert(fileSystemBuilder.fileStore("/foo").getSize() == 0)
  }
}
