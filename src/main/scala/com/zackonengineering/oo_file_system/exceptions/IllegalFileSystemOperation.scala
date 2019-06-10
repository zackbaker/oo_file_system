package com.zackonengineering.oo_file_system.exceptions

final case class IllegalFileSystemOperation(message: String = "", cause: Throwable = None.orNull) extends Exception(message, cause)
