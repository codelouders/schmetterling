/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.logger

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.config.ConfigFactory

/**
 * File logger implementation
 *
 * @param filePath - log file path
 * @param logLevel - Not only is possible to set up system or package log level but
 *                  it is also possible to set event lower level just for this file logger
 *
 *                  Please not higher level than system/package won't work
 */
class FileLogger(filePath: String, logLevel: LogLevel) extends Logger {

  def this(filePath: String) = {
    this(filePath, LogLevel.fromString(ConfigFactory.load().getString("logging.level")))
  }

  val file = new File(filePath)

  private val dateFormatter: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS")
//  dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))

  override def debug(msg: String, tag: String): Unit = {
    if (logLevel == DebugLevel){
      printToFile(file) { p =>
        p.println(s"[DEBUG] | ${dateFormatter.format(new Date())} | $tag | $msg")
      }
    }
  }

  override def info(msg: String, tag: String): Unit = {
    if (logLevel == DebugLevel || logLevel == InfoLevel) {
      printToFile(file) { p =>
        p.println(s"[INFO] | ${dateFormatter.format(new Date())} | $tag | $msg")
      }
    }
  }

  override def warn(msg: String, tag: String): Unit = {
    if (logLevel == DebugLevel || logLevel == InfoLevel || logLevel == WarningLevel) {
      printToFile(file) { p =>
        p.println(s"[WARN] | ${dateFormatter.format(new Date())} | $tag | $msg")
      }
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




  private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
}
