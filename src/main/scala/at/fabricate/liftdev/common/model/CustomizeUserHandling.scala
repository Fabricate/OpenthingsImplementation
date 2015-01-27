package at.fabricate.liftdev.common
package model

import net.liftweb.proto.{ProtoUser => GenProtoUser}
import scala.xml.NodeSeq
import net.liftweb.mapper.MetaMegaProtoUser
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.util.Mailer
import net.liftweb.util.Mailer.From
import net.liftweb.util.Mailer.Subject
import net.liftweb.util.Mailer.To
import net.liftweb.util.Mailer.BCC
import net.liftweb.common.Empty
import net.liftweb.util.Helpers._

trait CustomizeUserHandling[T <: MegaProtoUser[T]] extends MetaMegaProtoUser[T]  {
  self: T =>
  def customLostPassword(selector : ( (String, List[String]) => Unit ) => NodeSeq, defaultRedirectLocation : String = homePage) = {
//    val bind =
//      ".email" #> SHtml.text("", sendPasswordReset _) &
//      "type=submit" #> lostPasswordSubmitButton(S.?("send.it"))

    selector(customSendPasswordReset(_,_,defaultRedirectLocation))
  }
  
  def customSendPasswordReset(email: String, customPasswordResetPath: List[String], defaultRedirectLocation : String = homePage) {
    findUserByUserName(email) match {
      case Full(user) if user.validated_? =>
        user.resetUniqueId().save
        val resetLink = S.hostAndPath+
        customPasswordResetPath.mkString("/", "/", "/")+urlEncode(user.getUniqueId())
        val email: String = user.getEmail

        Mailer.sendMail(From(emailFrom),Subject(passwordResetEmailSubject),
                        (To(user.getEmail) ::
                         generateResetEmailBodies(user, resetLink) :::
                         (bccEmail.toList.map(BCC(_)))) :_*)

        S.notice(S.?("password.reset.email.sent"))
        S.redirectTo(defaultRedirectLocation)

      case Full(user) =>
        sendValidationEmail(user)
        S.notice(S.?("account.validation.resent"))
        S.redirectTo(defaultRedirectLocation)

      case _ => S.error(userNameNotFoundString)
    }
  } 
  
  def customPasswordReset(selector : T => NodeSeq, defaultRedirectLocation : String = homePage) =
	  findUserByUniqueId(snarfLastItem) match {
	    case Full(user) =>
	      def finishSet() {
	        user.validate match {
	          case Nil => S.notice(S.?("password.changed"))
	            user.resetUniqueId().save
	            logUserIn(user, () => S.redirectTo(defaultRedirectLocation))
	
	          case xs => S.error(xs)
	        }
	      }
	      
	      selector(user)
	    case _ => S.error(S.?("password.link.invalid")); S.redirectTo(homePage)
	  }       
	  
   def customSignup(selector : (T, () => Unit ) => NodeSeq, defaultRedirectLocation : String = homePage) = {
	  val theUser: TheUserType = mutateUserOnSignup(createNewUserInstance())
	  val theName = signUpPath.mkString("")
	  def testSignup() {
	  	validateSignup(theUser) match {
	  		case Nil =>
	  			actionsAfterSignup(theUser, () => S.redirectTo(defaultRedirectLocation))
	  		case xs => S.error(xs) ; signupFunc(Full(innerSignup _))
	  	}
  	 }
  	 def innerSignup = {
//  			 ("type=submit" #> signupSubmitButton(S ? "sign.up", testSignup _)) apply signupXhtml(theUser)
  			 selector(theUser, testSignup)
  	 }
  	 innerSignup
  }
  
  
	def customChangePassword(selector : (T, (String, List[String]) => Unit  ) => NodeSeq, defaultRedirectLocation : String = homePage) = {
		val user = currentUser.openOrThrowException("we can do this because the logged in test has happened")
	//	var oldPassword = ""
	//	var newPassword: List[String] = Nil
		def testAndSet(oldPassword : String, newPassword : List[String])() = {
			if (!user.testPassword(Full(oldPassword))) S.error(S.?("wrong.old.password"))
			else {
				user.setPasswordFromListString(newPassword)
				user.validate match {
					case Nil => user.save; S.notice(S.?("password.changed")); S.redirectTo(defaultRedirectLocation)
					case xs => S.error(xs)
				}
			}
		}
	//	val bind = {
	//			// Use the same password input for both new password fields.
	//			val passwordInput = SHtml.password_*("", LFuncHolder(s => newPassword = s))
	//					".old-password" #> SHtml.password("", s => oldPassword = s) &
	//					".new-password" #> passwordInput &
	//					"type=submit" #> changePasswordSubmitButton(S.?("change"), testAndSet _)
	//	}
	//	bind(changePasswordXhtml)
		selector(user, testAndSet)
	}
	  
  def customLogin(selector : NodeSeq, defaultRedirectLocation : String = homePage) = {
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
    										defaultRedirectLocation
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
//  	val bind =
//  			".email" #> FocusOnLoad(<input type="text" name="username"/>) &
//  			".password" #> <input type="password" name="password"/> &
//  			"type=submit" #> loginSubmitButton(S.?("log.in"))
//  	bind(loginXhtml)
      selector
 }
  
  def customLogout(redirectLocation : String = homePage) = {
    logoutCurrentUser
    S.redirectTo(redirectLocation)
  }
    									
	  
//  override def homePage = "/"
    /*
   override def signupXhtml(user: TheUserType) = {
	(<form method="post" action={S.uri}><table><tr><td
colspan="2">{ S.??("sign.up") }</td></tr>
	{localForm(user, false, signupFields)}
<tr><td>&nbsp;</td><td><user:submit/></td></tr>
</table></form>)
	}
	
	 /**
	* Given an instance of TheCrudType and FieldPointerType, convert
	* that to an actual instance of a BaseField on the instance of TheCrudType
	*/
	protected def computeFieldFromPointer(instance: TheUserType, pointer: FieldPointerType): Box[BaseField]
	protected def localForm(user: TheUserType, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq = {
	for {
	pointer <- fields
	field <- computeFieldFromPointer(user, pointer).toList
	if field.show_? && (!ignorePassword || !pointer.isPasswordField_?)
	form <- field.toForm.toList
	} yield <tr><td>{field.displayName}</td><td>{form}</td></tr>
	}
	protected def wrapIt(in: NodeSeq): NodeSeq =
	screenWrap.map(new RuleTransformer(new RewriteRule {
	override def transform(n: Node) = n match {
	case e: Elem if "bind" == e.label && "lift" == e.prefix => in
	case _ => n
	}
	})) openOr in
	}
	
   override def loginXhtml = {
	(<form method="post" action={S.uri}><table><tr><td
colspan="2">{S.??("log.in")}</td></tr>
<tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
<tr><td>{S.??("password")}</td><td><user:password /></td></tr>
<tr><td><a href={lostPasswordPath.mkString("/", "/", "")}
>{S.??("recover.password")}</a></td><td><user:submit /></td></tr></table>
</form>)
	}
   override  def lostPasswordXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td
colspan="2">{S.??("enter.email")}</td></tr>
<tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
<tr><td>&nbsp;</td><td><user:submit /></td></tr>
</table>
</form>)
	}
   override def passwordResetXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td colspan="2">{S.??("reset.your.password")}</td></tr>
<tr><td>{S.??("enter.your.new.password")}</td><td><user:pwd/></td></tr>
<tr><td>{S.??("repeat.your.new.password")}</td><td><user:pwd/></td></tr>
<tr><td>&nbsp;</td><td><user:submit/></td></tr>
</table>
</form>)
	}
    def changePasswordXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td colspan="2">{S.??("change.password")}</td></tr>
<tr><td>{S.??("old.password")}</td><td><user:old_pwd /></td></tr>
<tr><td>{S.??("new.password")}</td><td><user:new_pwd /></td></tr>
<tr><td>{S.??("repeat.password")}</td><td><user:new_pwd /></td></tr>
<tr><td>&nbsp;</td><td><user:submit /></td></tr>
</table>
</form>)
	}
//    net.liftweb.http.Templates.findAnyTemplate("modules"::"user"::Nil) match {
//    case Full(template) => new Elem(template.theSeq.head)
//  }
  //Finder.findAnyTemplate("modules"::""::Nil) openOr(super.loginXhtml)
//    LiftSession.f
 *    
 */
}
