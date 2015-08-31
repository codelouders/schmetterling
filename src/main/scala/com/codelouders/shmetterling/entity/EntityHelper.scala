/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.codelouders.shmetterling.entity

trait EntityHelper {

  def entityUri(resourceBasePath: String, entity: BaseEntity) = {
    s"$resourceBasePath/${entity.getId}"
  }
}
