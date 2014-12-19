package at.fabricate
package snippet

import net.liftweb.http.RequestVar
import scala.xml._
import net.liftweb.http._
import net.liftweb.util._
import at.fabricate.model.User
import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import Helpers._


/**
 * Snippet object that configures template-login and connects it to Login.auth
 */
object Login {
  private object user extends RequestVar("")
  private object pass extends RequestVar("")

  def auth() = {
//    logger.debug("[Login.auth] enter.")

    // validate the user credentials and do a bunch of other stuff
//    User.logUserIn(who)

//    logger.debug("[Login.auth] exit.")
  }

  /**
   * This is the part of the snippet that creates the form elements and connects the client side components to
   * server side handlers.
   *
   * @param xhtml - the raw HTML that we are going to be manipulating.
   * @return NodeSeq - the fully rendered HTML
   */
  def login(xhtml: NodeSeq): NodeSeq = {
//    logger.debug("[Login.login] enter.")
    /*
    SHtml.ajaxForm(
      bind("login", xhtml,
        "user" -> SHtml.text(user.is, user(_), "maxlength" -> "40"),
        "pass" -> SHtml.password(pass.is, pass(_)),
        "submit" -> (SHtml.hidden(auth) ++ <input type="submit" value="Login"/>)))
        * 
        */
	  	("user" #> SHtml.text(user.is, user(_), "maxlength" -> "40") &
        "pass" #> SHtml.text(user.is, user(_), "maxlength" -> "40")
            )(xhtml)
  }
}