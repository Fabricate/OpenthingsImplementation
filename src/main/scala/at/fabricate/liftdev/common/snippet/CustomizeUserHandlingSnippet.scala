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
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import at.fabricate.liftdev.common.lib.UrlLocalizer


/**
 * Snippet object that configures template-login and connects it to Login.auth
 */
class CustomizeUserHandlingSnippet[T <: MegaProtoUser[T] with BaseEntityWithTitleAndDescription[T]](userObject : MetaMegaProtoUser[T] with CustomizeUserHandling[T] with BaseMetaEntityWithTitleAndDescription[T], userSnippet : BaseEntityWithTitleAndDescriptionSnippet[T]) extends DispatchSnippet {

  
  def dispatch : DispatchIt = {    
    case "login" => login _
    case "logout" => logout _
    case "edit" => edit _
    case "signup" => signup _
    case "validateUser" => validateUser _
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
    
    def validateUserTitle = "Validate"      
    def validateUserTemlate = "validate_user" 

    def allTemplates : List[String] = List(loginTemlate,logoutTemlate,signUpTemlate,lostPasswordTemlate,resetPasswordTemlate,validateUserTemlate)
    
    def loggedInMessage : NodeSeq = Text("ERROR - You are already logged in!")
    def notLoggedInMessage : NodeSeq = Text("ERROR - You are not logged in!")


    def getMenu = 
       List[Menu](
               Menu.i(loginTitle) / loginTemlate ,
               Menu.i(signUpTitle) / signUpTemlate ,
               Menu.i(logoutTitle) / logoutTemlate  >> Hidden ,
               Menu.i(lostPasswordTitle) / lostPasswordTemlate ,
               Menu.i(resetPasswordTitle) / resetPasswordTemlate  >> Hidden ,
               Menu.i(validateUserTitle) / validateUserTemlate  >> Hidden
     )

     
     object requestedID extends RequestVar[String]("")
     
      object MatchResetPassword extends MatchString(resetPasswordTemlate)
      object MatchValidateUser extends MatchString(validateUserTemlate)


	  def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
	      case RewriteRequest(ParsePath(MatchResetPassword(resetPath):: anID :: Nil, _, _, _), _, _) => { 
	        requestedID.set(anID)
	        RewriteResponse(resetPath :: Nil)
	      }
	      case RewriteRequest(ParsePath(MatchValidateUser(validatePath):: anID :: Nil, _, _, _), _, _) =>{ 
	        requestedID.set(anID)
		      RewriteResponse(validatePath :: Nil)	  	  
	      }
	    }

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
    	  
  def validateUser(xhtml : NodeSeq): NodeSeq = {
    	  userObject.customValidateUser(requestedID.get)
    	}

  /**
   * This is the part of the snippet that creates the form elements and connects the client side components to
   * server side handlers.
   *
   * @param xhtml - the raw HTML that we are going to be manipulating.
   * @return NodeSeq - the fully rendered HTML
   */
  def login(xhtml: NodeSeq): NodeSeq = {
		if (!userObject.loggedIn_?)
            userObject.customLogin{
  			("#email" #> <input type="text" name="username" placeholder="Your mail address"/> & //FocusOnLoad()
  			"#password" #> <input type="password" name="password" placeholder="Your password"/> &
  			"#loginform [action]" #> S.uri
  			).apply(xhtml)
            }
		else
		  loggedInMessage

  }
  
  def signup(xhtml: NodeSeq): NodeSeq = {
    
		if (!userObject.loggedIn_?)
            userObject.customSignup({
             (user, action) =>   
               ("#txtEmail" #> user.email.toForm & 
  			"#txtPassword" #> S.fmapFunc({s: List[String] => user.password.setFromAny(s)}){funcName =>
  					Full(<span><input type="password" name={funcName} value="" placeholder="Password" id="txtPassword"/>
  					<input type="password" name={funcName} value="" placeholder="Repeat Password" id="txtPassword"/></span>)
} &

  			"#txtFirstName" #> user.firstName.toForm &
  			"#txtLastName" #> user.lastName.toForm &
  			"#nickName" #> user.defaultTranslation.obj.openOrThrowException("Empty Box opened").title.toForm &
  			"#signuphidden" #> SHtml.hidden(action )&
  			"#signupform [action]" #> S.uri
  			).apply(xhtml)
            },List(validateUserTemlate),()=>userObject.createNewEntity(UrlLocalizer.contentLocale))
		else
		  loggedInMessage

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
   		      "#lostpasswordhidden" #> SHtml.hidden(() => action(mailAddress,List(resetPasswordTemlate),List(validateUserTemlate)) ) &
  			  "#lostpasswordform [action]" #> S.uri
   		          ).apply(xhtml)
   		    }
   		  }
    	else
          loggedInMessage
	
    

    def resetPassword(xhtml: NodeSeq): NodeSeq =
   		if (!userObject.loggedIn_? )
   		  userObject.customPasswordReset({
   		    (user, finish ) => 
   		      val passwordInput = SHtml.password_*("", LFuncHolder(s => user.password.setList(s) ))
   		      ("#reset-password" #> passwordInput &
   		        "#resetpasswordhidden" #> SHtml.hidden( finish ) &
   		        "#resetpasswordform [action]" #> S.uri
   		      	).apply(xhtml)
   		}, requestedID.get)   
    	else
          loggedInMessage
  
  
    def edit(xhtml: NodeSeq): NodeSeq = {
      		if (userObject.loggedIn_? )
            	userSnippet.toForm(userObject.currentUser.getOrElse(userObject.create)).apply(xhtml)
            	else
            	  notLoggedInMessage
  }

    def logout(xhtml: NodeSeq): NodeSeq = {
      		if (userObject.loggedIn_? ){
            	("#logouthidden" #> SHtml.hidden(()=>userObject.customLogout(S.uri)) &
            	 "#logoutform [action]" #> S.uri).apply(xhtml)
      		}
            	else
            	  notLoggedInMessage
  }
  
}
