package at.fabricate.openthings
package lib

import model.User
import net.liftweb.sitemap.Loc._
import net.liftweb.http.RedirectResponse

object AccessControl {
  /** Zugriffsbedingung: Benutzer ist eingeloggt. */
  val loggedIn = If(() => User.loggedIn_?,
                  () => RedirectResponse("/login"))

}
