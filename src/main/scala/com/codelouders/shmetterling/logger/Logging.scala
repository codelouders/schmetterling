/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.logger

trait Logging {

  private lazy val logger = LoggingService.getLoggingService
  val className: String = getClass.getName
  val logTag: String


  object L {

    def debug(msg: String) = {
      logger ! Debug(msg, className, logTag)
    }

    def debug(msg: String, logTag: String) = {
      logger ! Debug(msg, className, logTag)
    }

    def info(msg: String) = {
      logger ! Info(msg, className, logTag)
    }

    def info(msg: String, logTag: String) = {
      logger ! Info(msg, className, logTag)
    }

    def warn(msg: String) = {
      logger ! Info(msg, className, logTag)
    }

    def warn(msg: String, logTag: String) = {
      logger ! Info(msg, className, logTag)
    }

    def error(msg: String, e: Exception) = {
      logger ! Error(msg, className, logTag, e.getCause, e.getStackTrace)
    }

    def error(msg: String, logTag: String, e: Exception) = {
      logger ! Error(msg, className, logTag, e.getCause, e.getStackTrace)
    }
  }
}
