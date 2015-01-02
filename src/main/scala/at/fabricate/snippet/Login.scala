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
import net.liftweb.http.S.LFuncHolder
import at.fabricate.lib.MatchString
import at.fabricate.lib.MatchPath
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.Loc.Hidden
import net.liftweb.sitemap.LocPath
import net.liftweb.sitemap.*


/**
 * Snippet object that configures template-login and connects it to Login.auth
 */
object Login extends DispatchSnippet {
//  private object user extends RequestVar("")
//  private object pass extends RequestVar("")

//  def auth() = {
//    logger.debug("[Login.auth] enter.")

    // validate the user credentials and do a bunch of other stuff
//    User.logUserIn(who)

//    logger.debug("[Login.auth] exit.")
//  }
  
  def dispatch : DispatchIt = {    
    case "login" => login _
    case "logout" => logout _
    case "edit" => edit _
    case "account" => account _
    case "signup" => signup _
    case "changePassword" => changePassword _
    case "lostPassword" => lostPassword _
    case "resetPassword" => resetPassword _
  }
  
    def loginTitle = "Login"
    def loginTemlate = "login"  
      
    def logoutTitle = "Logout"      
    def logoutTemlate = "logout"

    def signUpTitle = "Sign up"          
    def signUpTemlate = "sign_up"    

    def lostPasswordTitle = "Lost password"      
    def lostPasswordTemlate = "lost_password"    

    def resetPasswordTitle = "Reset password"      
    def resetPasswordTemlate = "reset_password"
      

    def allTemplates : List[String] = List(loginTemlate,logoutTemlate,signUpTemlate,lostPasswordTemlate,resetPasswordTemlate)
    
    def loggedInMessage : NodeSeq = Text("You are already logged in!")
    def notLoggedInMessage : NodeSeq = Text("You are not logged in!")


//    object MatchLogin extends MatchPath(niceLoginwUrl)
    def getMenu = 
       List[Menu](
               Menu.i(loginTitle) / loginTemlate ,
               Menu.i(signUpTitle) / signUpTemlate ,
               Menu.i(logoutTitle) / logoutTemlate  >> Hidden ,
               Menu.i(lostPasswordTitle) / lostPasswordTemlate ,
               Menu.i(resetPasswordTitle) / resetPasswordTemlate / * >> Hidden 
     )
     
//    def userMgtLoginUrl = User.loginPath 
//      
//    def userMgtLogoutUrl = User.logoutPath
//      
//    def userMgtSignUpUrl = User.signUpPath 
//      
//    def userMgtLostPasswordUrl = User.lostPasswordPath 
     
//  def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
//      case RewriteRequest(ParsePath(MatchLogin(loginPath), _, _, _), _, _) =>
//	      RewriteResponse(userMgtLoginUrl)
//      case RewriteRequest(ParsePath(List("logout"), _, _, _), _, _) =>
//	      RewriteResponse(userMgtLogoutUrl)
	      
//      case RewriteRequest(ParsePath(List("sign_up"), _, _, _), _, _) =>
//	      RewriteResponse(userMgtSignUpUrl)
//      case RewriteRequest(ParsePath(List("lost_password"), _, _, _), _, _) =>
//	      RewriteResponse(userMgtLostPasswordUrl)	  	   
//    }
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
/**}
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
}}
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
  
//  def loginAjax(xhtml: NodeSeq): NodeSeq = {
//        /*
//         * TODO: Implement that stuff if necessary, not needed ATM
//    SHtml.ajaxForm(
//      bind("login", xhtml,
//        "user" -> SHtml.text(user.is, user(_), "maxlength" -> "40"),
//        "pass" -> SHtml.password(pass.is, pass(_)),
//        "submit" -> (SHtml.hidden(auth) ++ <input type="submit" value="Login"/>)))
//        * 
//        */
//	  	("user" #> SHtml.text(user.is, user(_), "maxlength" -> "40") &
//        "pass" #> SHtml.text(user.is, user(_), "maxlength" -> "40")
//            )(xhtml)
//  }
     
     

//  def signupMailBody(user: TheUserType, validationLink: String): Elem = {
//    (<html>
//        <head>
//          <title>{S.?("sign.up.confirmation")}</title>
//        </head>
//        <body>
//          <p>{S.?("dear")} {user.getFirstName},
//            <br/>
//            <br/>
//            {S.?("sign.up.validation.link")}
//            <br/><a href={validationLink}>{validationLink}</a>
//            <br/>
//            <br/>
//            {S.?("thank.you")}
//          </p>
//        </body>
//     </html>)
//  }
//
//  def signupMailSubject = S.?("sign.up.confirmation")
//
//  /**
//   * Send validation email to the user.  The XHTML version of the mail
//   * body is generated by calling signupMailBody.  You can customize the
//   * mail sent to users by override generateValidationEmailBodies to
//   * send non-HTML mail or alternative mail bodies.
//   */
//  def sendValidationEmail(user: TheUserType) {
//    val resetLink = S.hostAndPath+"/"+validateUserPath.mkString("/")+
//    "/"+urlEncode(user.getUniqueId())
//
//    val email: String = user.getEmail
//
//    val msgXml = signupMailBody(user, resetLink)
//
//    Mailer.sendMail(From(emailFrom),Subject(signupMailSubject),
//                    (To(user.getEmail) :: 
//                     generateValidationEmailBodies(user, resetLink) :::
//                     (bccEmail.toList.map(BCC(_)))) :_* )
//  }
//
//  /**
//   * Generate the mail bodies to send with the valdiation link.
//   * By default, just an HTML mail body is generated by calling signupMailBody
//   * but you can send additional or alternative mail by override this method.
//   */
//  protected def generateValidationEmailBodies(user: TheUserType,
//                                              resetLink: String):
//  List[MailBodyType] = List(xmlToMailBodyType(signupMailBody(user, resetLink)))
//
//  protected object signupFunc extends RequestVar[Box[() => NodeSeq]](Empty) {
//    override lazy val __nameSalt = Helpers.nextFuncName
//  }     
     
//  /**
//   * Send password reset email to the user.  The XHTML version of the mail
//   * body is generated by calling passwordResetMailBody.  You can customize the
//   * mail sent to users by overriding generateResetEmailBodies to
//   * send non-HTML mail or alternative mail bodies.
//   */
//  def sendPasswordReset(email: String) {
//    findUserByUserName(email) match {
//      case Full(user) if user.validated_? =>
//        user.resetUniqueId().save
//        val resetLink = S.hostAndPath+
//        passwordResetPath.mkString("/", "/", "/")+urlEncode(user.getUniqueId())
//
//        val email: String = user.getEmail
//
//        Mailer.sendMail(From(emailFrom),Subject(passwordResetEmailSubject),
//                        (To(user.getEmail) ::
//                         generateResetEmailBodies(user, resetLink) :::
//                         (bccEmail.toList.map(BCC(_)))) :_*)
//
//        S.notice(S.?("password.reset.email.sent"))
//        S.redirectTo(homePage)
//
//      case Full(user) =>
//        sendValidationEmail(user)
//        S.notice(S.?("account.validation.resent"))
//        S.redirectTo(homePage)
//
//      case _ => S.error(userNameNotFoundString)
//    }
//  }     
     
//     
//  def lostPassword = {
//    val bind =
//      ".email" #> SHtml.text("", sendPasswordReset _) &
//      "type=submit" #> lostPasswordSubmitButton(S.?("send.it"))
//
//    bind(lostPasswordXhtml)
//  }
     
//  def lostPasswordXhtml = {
//    (<form method="post" action={S.uri}>
//        <table><tr><td
//              colspan="2">{S.?("enter.email")}</td></tr>
//          <tr><td>{userNameFieldString}</td><td><input type="text" class="email" /></td></tr>
//          <tr><td>&nbsp;</td><td><input type="submit" /></td></tr>
//        </table>
//     </form>)
//  }     
//
//  def lostPasswordSubmitButton(name: String, func: () => Any = () => {}): NodeSeq = {
//    standardSubmitButton(name, func)
//  }
//
//  def passwordResetXhtml = {
//    (<form method="post" action={S.uri}>
//        <table><tr><td colspan="2">{S.?("reset.your.password")}</td></tr>
//          <tr><td>{S.?("enter.your.new.password")}</td><td><input type="password" /></td></tr>
//          <tr><td>{S.?("repeat.your.new.password")}</td><td><input type="password" /></td></tr>
//          <tr><td>&nbsp;</td><td><input type="submit" /></td></tr>
//        </table>
//     </form>)
//  }
//
//  def passwordReset(id: String) =
//  findUserByUniqueId(id) match {
//    case Full(user) =>
//      def finishSet() {
//        user.validate match {
//          case Nil => S.notice(S.?("password.changed"))
//            user.resetUniqueId().save
//            logUserIn(user, () => S.redirectTo(homePage))
//
//          case xs => S.error(xs)
//        }
//      }
//
//      val bind = {
//        "type=password" #> SHtml.password_*("", { p: List[String] =>
//          user.setPasswordFromListString(p)
//        }) &
//        "type=submit" #> resetPasswordSubmitButton(S.?("set.password"), finishSet _)
//      }
//
//      bind(passwordResetXhtml)
//    case _ => S.error(S.?("password.link.invalid")); S.redirectTo(homePage)
//  }     
  // code from ProtoUser.scala (not exactly sure but assume from the proto package)
  def snarfLastItem: String =
  	(for (r <- S.request) yield r.path.wholePath.last) openOr ""

  def defaultRedirectLocation : String = User.homePage
  
  def getRedirectLocation : String = 
    	if (allTemplates.exists(_ == snarfLastItem))
    	  // you are at a 
    	  defaultRedirectLocation
    	else
    	  S.uri
  /**
   * This is the part of the snippet that creates the form elements and connects the client side components to
   * server side handlers.
   *
   * @param xhtml - the raw HTML that we are going to be manipulating.
   * @return NodeSeq - the fully rendered HTML
   */
  def login(xhtml: NodeSeq): NodeSeq = {
//    logger.debug("[Login.login] enter.")
		if (!User.loggedIn_?)
            User.customLogin{
  			("#email" #> <input type="text" name="username" placeholder="your mail address"/> & //FocusOnLoad()
  			"#password" #> <input type="password" name="password" placeholder="your password"/> &
  			"#loginform [action]" #> S.uri
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
  			).apply(xhtml)
            }
		else
		  loggedInMessage

  }
  
  def signup(xhtml: NodeSeq): NodeSeq = {
    
		if (!User.loggedIn_?)
            User.customSignup{
             (user, action) =>   
               ("#txtEmail" #> user.email.toForm.map(_ % ("placeholder"->"mail adress")) & //(user.email.toForm.map(_ % ("default"->"mail adress")) & //FocusOnLoad()
//  			"#txtPassword" #> user.password.toForm.toList &
  			"#txtPassword" #> S.fmapFunc({s: List[String] => user.password.setFromAny(s)}){funcName =>
  					Full(<span><input type="password" name={funcName} value="" placeholder="password" id="txtPassword"/>
  					<input type="password" name={funcName} value="" placeholder="repeat password" id="txtPassword"/></span>)
} &
//               List(SHtml.password("", value => {user.password(value)}, "placeholder" -> "password", "id" -> "txtPassword"),
//  			    SHtml.password("", value => {user.password(value)}, "placeholder" -> "repeat password", "id" -> "txtPassword")) &
  			"#txtFirstName" #> user.firstName.toForm.map(_ % ("placeholder"->"first name")) &
  			"#txtLastName" #> user.lastName.toForm.map(_ % ("placeholder"->"last name")) &
  			"#signuphidden" #> SHtml.hidden(action )&
  			"#signupform [action]" #> S.uri
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
  			).apply(xhtml)
            }
		else
		  loggedInMessage
//              	 def innerSignup = {
//  			 ("type=submit" #> signupSubmitButton(S ? "sign.up", testSignup _)) apply signupXhtml(theUser)
//  	 }
  }
  
  def changePassword(xhtml: NodeSeq): NodeSeq = {
    	var oldPassword = ""
    	var newPassword: List[String] = Nil
   		val passwordInput = SHtml.password_*("", LFuncHolder(s => newPassword = s))
		

   		if (User.loggedIn_? )
            User.customChangePassword {
   		     (user, action) =>   
               ("#old-password" #> SHtml.password("", s => oldPassword = s) &
  			"#new-password" #> passwordInput &
  			"#changepasswordhidden" #> SHtml.hidden(() => action(oldPassword, newPassword) )&
  			"#changepasswordform [action]" #> S.uri
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
  			).apply(xhtml)
   		}
            	else
            	  notLoggedInMessage

  }
  
    def lostPassword(xhtml: NodeSeq): NodeSeq =
   		if (!User.loggedIn_? )
   		  User.customLostPassword {
   		    action => {
   		      var mailAddress = ""
   		      ("#email" #> SHtml.text("", mailAddress = _, "placeholder"->"your mail adress") &
   		      "#lostpasswordhidden" #> SHtml.hidden(() => action(mailAddress,List(resetPasswordTemlate)) ) &
  			  "#lostpasswordform [action]" #> S.uri
   		          ).apply(xhtml)
   		    }
   		  }
    	else
          loggedInMessage
	
    

    def resetPassword(xhtml: NodeSeq): NodeSeq =
   		if (!User.loggedIn_? )
   		  User.customPasswordReset{
   		    user => 
   		      ("#reset-password" #> SHtml.password_*("", { p: List[String] => 
   		        user.password.setList(p) } ) &
   		        "#resetpasswordform [action]" #> S.uri
   		      	).apply(xhtml)
   		}   
    	else
          loggedInMessage
  
//   		        user.setPasswordFromListString(p) 
//   		      "#lostpasswordhidden" #> SHtml.hidden(() => action(mailAddress,List(resetPasswordTemlate)) )

          
//    	      val bind = {
//	        "type=password" #> SHtml.password_*("", { p: List[String] =>
//	          user.setPasswordFromListString(p)
//	        }) &
//	        "type=submit" #> resetPasswordSubmitButton(S.?("set.password"), finishSet _)
//	      }
//	
//	      bind(passwordResetXhtml)
  
    def edit(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? )
            	User.edit
            	else
            	  notLoggedInMessage
  }

    def logout(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? ){
//      		  println(xhtml)
//      		  xhtml
//      		  Text("I applied logout and user is logged in")
            	("#logouthidden" #> SHtml.hidden(()=>User.customLogout(S.uri)) &
            	 "#thelogoutform [action]" #> S.uri).apply(xhtml)
      		}
            	else
            	  notLoggedInMessage
  }
    def account(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? )
            	("#login" #> "" &
            	 "#login *" #> "Logout" //&
//            	 "#logoutBtn [href]" #> "/%s".format(User.logoutPath.mkString("/")) &
//            	 "#account" #> <span class="icon-"></span> Account  
            )(xhtml)
            	else
            	("#login *" #> "Login / register" &
            	 "#logout" #> "" &
//            	 "#logoutBtn [href]" #> "/%s".format(User.logoutPath.mkString("/")) &
            	"#account" #> ""
            )(xhtml)
  }
  
}