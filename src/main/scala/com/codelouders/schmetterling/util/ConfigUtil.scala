package com.codelouders.schmetterling.util

import com.typesafe.config.Config


/**
 * Created by WiktorT on 31/08/2015.
 *
 * Created on 31/08/2015
 */
object ConfigUtil {

  def getString(configuration: Config, path: String): Option[String] = {
    configuration.hasPath(path) match {
      case true =>
        try {
          Some(configuration.getString(path))
        } catch {
          case e: Exception =>
            None
        }
      case false =>
        None
    }
  }

  def getConf(config: Config, index: String): Option[Config] = {
    config.hasPath(index) match {
      case true => Some(config.getConfig(index))
      case false => None
    }
  }
}
