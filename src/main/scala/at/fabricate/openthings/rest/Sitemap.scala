package at.fabricate.openthings
package rest

import net.liftweb.http._
import net.liftweb.http.rest.RestHelper

object Sitemap extends RestHelper {
  serve {
    case "sitemap" :: Nil Get req =>
      XmlResponse(
        S.render(<lift:embed what="sitemap" />, req.request).head
      )
  }
}
