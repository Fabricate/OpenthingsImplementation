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
  /*
   def loginFirst = If(
loggedIn_? _,
() => {
import net.liftweb.http.{RedirectWithState, RedirectState}
val uri = S.uriAndQueryString
RedirectWithState(
loginPageURL,
RedirectState( ()=>{loginRedirect.set(uri)})
)
}
)

 def login = {
if (S.post_?) {
S.param("username").
flatMap(username => findUserByUserName(username)) match {
case Full(user) if user.validated_? &&
user.testPassword(S.param("password")) => {
val preLoginState = capturePreLoginState()
val redir = loginRedirect.get match {
case Full(url) =>
loginRedirect(Empty)
url
case _ =>
homePage
}
logUserIn(user, () => {
S.notice(S.?("logged.in"))
preLoginState()
S.redirectTo(redir)
})
}
case Full(user) if !user.validated_? =>
S.error(S.?("account.validation.error"))
case _ => S.error(S.?("invalid.credentials"))
}
}

val bind =
".email" #> FocusOnLoad(<input type="text" name="username"/>) &
".password" #> <input type="password" name="password"/> &
"type=submit" #> loginSubmitButton(S.?("log.in"))
bind(loginXhtml)
}
def loginSubmitButton(name: String, func: () => Any = () => {}): NodeSeq = {
standardSubmitButton(name, func)
}
def standardSubmitButton(name: String, func: () => Any = () => {}) = {
SHtml.submit(name, func)
}

 def signup = {
val theUser: TheUserType = mutateUserOnSignup(createNewUserInstance())
val theName = signUpPath.mkString("")
def testSignup() {
validateSignup(theUser) match {
case Nil =>
actionsAfterSignup(theUser, () => S.redirectTo(homePage))
case xs => S.error(xs) ; signupFunc(Full(innerSignup _))
}
}
def innerSignup = {
("type=submit" #> signupSubmitButton(S ? "sign.up", testSignup _)) apply signupXhtml(theUser)
}
innerSignup
}
def signupSubmitButton(name: String, func: () => Any = () => {}): NodeSeq = {
standardSubmitButton(name, func)
}

 /**
* Override this method to do something else after the user signs up
*/
protected def actionsAfterSignup(theUser: TheUserType, func: () => Nothing): Nothing = {
theUser.setValidated(skipEmailValidation).resetUniqueId()
theUser.save
if (!skipEmailValidation) {
sendValidationEmail(theUser)
S.notice(S.?("sign.up.message"))
func()
} else {
logUserIn(theUser, () => {
S.notice(S.?("welcome"))
func()
})
}
}

* 
*/
  
  def loginAjax(xhtml: NodeSeq): NodeSeq = {
        /*
         * TODO: Implement that stuff if necessary, not needed ATM
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

  /**
   * This is the part of the snippet that creates the form elements and connects the client side components to
   * server side handlers.
   *
   * @param xhtml - the raw HTML that we are going to be manipulating.
   * @return NodeSeq - the fully rendered HTML
   */
  def login(xhtml: NodeSeq): NodeSeq = {
//    logger.debug("[Login.login] enter.")

            User.login 
  }
  
  def signup(xhtml: NodeSeq): NodeSeq = {
            User.signup
  }
  
}