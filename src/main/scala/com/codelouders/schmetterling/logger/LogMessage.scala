/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.schmetterling.logger


/**
 * Log messages
 *
 * @param msg - message to be logged
 * @param tag - tag
 * @param logType - type of message
 */
private[logger] sealed class LogMessage(val msg: String, val className: String, val tag: String, logType: LogLevel)

case class Debug(override val msg: String, override val className: String, override val tag: String) extends LogMessage(msg, className, tag, DebugLevel)
case class Info(override val msg: String, override val className: String, override val tag: String) extends LogMessage(msg, className, tag, InfoLevel)
case class Warning(override val msg: String, override val className: String, override val tag: String) extends LogMessage(msg, className, tag, WarningLevel)
case class Error(override val msg: String, override val className: String, override val tag: String, cause: Throwable, stack: Array[StackTraceElement])
  extends LogMessage(msg, className, tag, ErrorLevel)