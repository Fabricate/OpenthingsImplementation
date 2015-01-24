package at.fabricate.liftdev.common
package snippet

import net.liftweb.http.RequestVar
import scala.xml._
import net.liftweb.http._
import net.liftweb.util._
import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import Helpers._
import net.liftweb.http.S.LFuncHolder
import lib.MatchString
import lib.MatchPath
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.Loc.Hidden
import net.liftweb.sitemap.LocPath
import net.liftweb.sitemap.*
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.MetaMegaProtoUser
import model.CustomizeUserHandling
import model.BaseEntityWithTitleAndDescription


/**
 * Snippet object that configures template-login and connects it to Login.auth
 */
class CustomizeUserHandlingSnippet[T <: MegaProtoUser[T] with BaseEntityWithTitleAndDescription[T]](userObject : MetaMegaProtoUser[T] with CustomizeUserHandling[T], userSnippet : BaseEntityWithTitleAndDescriptionSnippet[T]) extends DispatchSnippet {
//  private object user extends RequestVar("")
//  private object pass extends RequestVar("")

//  def auth() = {
//    logger.debug("[Login.auth] enter.")

    // validate the user credentials and do a bunch of other stuff
//    userObject.logUserIn(who)

//    logger.debug("[Login.auth] exit.")
//  }
  
  def dispatch : DispatchIt = {    
    case "login" => login _
    case "logout" => logout _
    case "edit" => edit _
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
     
//    def userMgtLoginUrl = userObject.loginPath 
//      
//    def userMgtLogoutUrl = userObject.logoutPath
//      
//    def userMgtSignUpUrl = userObject.signUpPath 
//      
//    def userMgtLostPasswordUrl = userObject.lostPasswordPath 
//      
//    def userMgtResetPasswordUrl =      userObject.passwordResetPath 
     
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

  // code from ProtoUser.scala (not exactly sure but assume from the proto package)
  def snarfLastItem: String =
  	(for (r <- S.request) yield r.path.wholePath.last) openOr ""

  def defaultRedirectLocation : String = userObject.homePage
  
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
		if (!userObject.loggedIn_?)
            userObject.customLogin{
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
    
		if (!userObject.loggedIn_?)
            userObject.customSignup{
             (user, action) =>   
               ("#txtEmail" #> user.email.toForm.map(_ % ("placeholder"->"E-mail")) & //(user.email.toForm.map(_ % ("default"->"mail adress")) & //FocusOnLoad()
//  			"#txtPassword" #> user.password.toForm.toList &
  			"#txtPassword" #> S.fmapFunc({s: List[String] => user.password.setFromAny(s)}){funcName =>
  					Full(<span><input type="password" name={funcName} value="" placeholder="Password" id="txtPassword"/>
  					<input type="password" name={funcName} value="" placeholder="Repeat password" id="txtPassword"/></span>)
} &
//               List(SHtml.password("", value => {user.password(value)}, "placeholder" -> "password", "id" -> "txtPassword"),
//  			    SHtml.password("", value => {user.password(value)}, "placeholder" -> "repeat password", "id" -> "txtPassword")) &
  			"#txtFirstName" #> user.firstName.toForm.map(_ % ("placeholder"->"First name")) &
  			"#txtLastName" #> user.lastName.toForm.map(_ % ("placeholder"->"Last name")) &
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
		

   		if (userObject.loggedIn_? )
            userObject.customChangePassword {
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
   		if (!userObject.loggedIn_? )
   		  userObject.customLostPassword {
   		    action => {
   		      var mailAddress = ""
   		      ("#email" #> SHtml.text("", mailAddress = _, "placeholder"->"Your E-mail adress") &
   		      "#lostpasswordhidden" #> SHtml.hidden(() => action(mailAddress,List(resetPasswordTemlate)) ) &
  			  "#lostpasswordform [action]" #> S.uri
   		          ).apply(xhtml)
   		    }
   		  }
    	else
          loggedInMessage
	
    

    def resetPassword(xhtml: NodeSeq): NodeSeq =
   		if (!userObject.loggedIn_? )
   		  userObject.customPasswordReset{
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
      		if (userObject.loggedIn_? )
            	userSnippet.toForm(userObject.currentUser.getOrElse(userObject.create)).apply(xhtml)
            	else
            	  notLoggedInMessage
  }

    def logout(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (userObject.loggedIn_? ){
//      		  println(xhtml)
//      		  xhtml
//      		  Text("I applied logout and user is logged in")
            	("#logouthidden" #> SHtml.hidden(()=>userObject.customLogout(S.uri)) &
            	 "#logoutform [action]" #> S.uri).apply(xhtml)
      		}
            	else
            	  notLoggedInMessage
  }
  
}
