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
import at.fabricate.lib.AccessControl


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


def changePassword = {
val user = currentUser.openOrThrowException("we can do this because the logged in test has happened")
var oldPassword = ""
var newPassword: List[String] = Nil
def testAndSet() {
if (!user.testPassword(Full(oldPassword))) S.error(S.?("wrong.old.password"))
else {
user.setPasswordFromListString(newPassword)
user.validate match {
case Nil => user.save; S.notice(S.?("password.changed")); S.redirectTo(homePage)
case xs => S.error(xs)
}
}
}
val bind = {
// Use the same password input for both new password fields.
val passwordInput = SHtml.password_*("", LFuncHolder(s => newPassword = s))
".old-password" #> SHtml.password("", s => oldPassword = s) &
".new-password" #> passwordInput &
"type=submit" #> changePasswordSubmitButton(S.?("change"), testAndSet _)
}
bind(changePasswordXhtml)
}
def changePasswordSubmitButton(name: String, func: () => Any = () => {}): NodeSeq = {
standardSubmitButton(name, func)
}
def editXhtml(user: TheUserType) = {
(<form method="post" action={S.uri}>
<table><tr><td colspan="2">{S.?("edit")}</td></tr>
{localForm(user, true, editFields)}
<tr><td>&nbsp;</td><td><input type="submit" /></td></tr>
</table>
</form>)
}

 object editFunc extends RequestVar[Box[() => NodeSeq]](Empty) {
override lazy val __nameSalt = Helpers.nextFuncName
}
/**
* If there's any mutation to do to the user on retrieval for
* editing, override this method and mutate the user. This can
* be used to pull query parameters from the request and assign
* certain fields. Issue #722
*
* @param user the user to mutate
* @return the mutated user
*/
protected def mutateUserOnEdit(user: TheUserType): TheUserType = user
def edit = {
val theUser: TheUserType =
mutateUserOnEdit(currentUser.openOrThrowException("we know we're logged in"))
val theName = editPath.mkString("")
def testEdit() {
theUser.validate match {
case Nil =>
theUser.save
S.notice(S.?("profile.updated"))
S.redirectTo(homePage)
case xs => S.error(xs) ; editFunc(Full(innerEdit _))
}
}
def innerEdit = {
("type=submit" #> editSubmitButton(S.?("save"), testEdit _)) apply editXhtml(theUser)
}
innerEdit
}
def editSubmitButton(name: String, func: () => Any = () => {}): NodeSeq = {
standardSubmitButton(name, func)
}

 protected def localForm(user: TheUserType, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq = {
for {
pointer <- fields
field <- computeFieldFromPointer(user, pointer).toList
if field.show_? && (!ignorePassword || !pointer.isPasswordField_?)
form <- field.toForm.toList
} yield <tr><td>{field.displayName}</td><td>{form}</td></tr>
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

            User.customLogin{
  			("#email" #> <input type="text" name="username" default="Your Mail"/> & //FocusOnLoad()
  			"#password" #> <input type="password" name="password" default="Your Passord"/> &
  			"#loginform [action]" #> S.uri
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
  			).apply(xhtml)
            }

  }
  
  def signup(xhtml: NodeSeq): NodeSeq = {
            User.customSignup{
             (user, action) =>   
               ("#txtEmail" #> user.email.toForm.toList & //(user.email.toForm.map(_ % ("default"->"mail adress")) & //FocusOnLoad()
  			"#txtPassword" #> user.password.toForm.toList &
  			"#txtFirstName" #> user.firstName.toForm.toList &
  			"#txtLastName" #> user.lastName.toForm.toList &
  			"#signuphidden" #> SHtml.hidden(action )&
  			"#signupform [action]" #> S.uri
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
  			).apply(xhtml)
            }
//              	 def innerSignup = {
//  			 ("type=submit" #> signupSubmitButton(S ? "sign.up", testSignup _)) apply signupXhtml(theUser)
//  	 }
  }
  
  def changePassword(xhtml: NodeSeq): NodeSeq = {
   		if (User.loggedIn_? )
            User.changePassword
            	else
            	  Text("Not logged in!")
  }
  
    def edit(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? )
            	User.edit
            	else
            	  Text("Not logged in!")
  }
    
    def account(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? )
            	("#login" #> "" &
            	 "#login *" #> "Logout" &
            	 "#logoutBtn [href]" #> "/%s".format(User.logoutPath.mkString("/")) &
            	 "#loginlogout [onClick]" #> "Logout();"  
            )(xhtml)
            	else
            	("#login *" #> "Login / register" &
            	 "#logout" #> "" &
            	 "#logoutBtn [href]" #> "/%s".format(User.logoutPath.mkString("/")) &
            	"#account" #> ""
            )(xhtml)
  }
  
}