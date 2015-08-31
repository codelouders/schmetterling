/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.logger

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Console logger implementation
 */
class FileLogger(filePath: String) extends Logger {

  val file = new File(filePath)

  private val dateFormatter: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS")
//  dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))

  override def debug(msg: String, tag: String): Unit = {
    printToFile(file) { p =>
      p.println(s"DEBUG | ${dateFormatter.format(new Date())} | $tag | $msg")
    }
  }

  override def warn(msg: String, tag: String): Unit = {
    printToFile(file) { p =>
      p.println(s"WARN | ${dateFormatter.format(new Date())} | $tag | $msg")
    }
  }

  override def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement]): Unit = {
    printToFile(file) { p =>
      p.println(s"ERROR | ${dateFormatter.format(new Date())} | $tag | MSG   | $msg")
      p.println(s"ERROR | ${dateFormatter.format(new Date())} | $tag | CAUSE | $cause")
      stack.foreach {
        stackElement =>
          p.println(s"ERROR | $tag | STACK | $stackElement")
      }
    }
  }

  override def info(msg: String, tag: String): Unit = {
    printToFile(file) { p =>
      p.println(s"INFO | ${dateFormatter.format(new Date())} | $tag | $msg")
    }
  }


  private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}
