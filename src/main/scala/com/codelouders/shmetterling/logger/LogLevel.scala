/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.logger

/**
 * Logging levels
 */
private[logger] sealed trait LogLevel
case object DebugLevel extends LogLevel
case object InfoLevel extends LogLevel
case object ErrorLevel extends LogLevel