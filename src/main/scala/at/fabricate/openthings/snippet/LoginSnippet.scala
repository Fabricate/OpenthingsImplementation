package at.fabricate.openthings
package snippet

import model.User
import at.fabricate.liftdev.common.snippet.CustomizeUserHandlingSnippet
import scala.xml.NodeSeq
import net.liftweb.common._
import net.liftweb._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import at.fabricate.liftdev.common.lib.UrlLocalizer
import net.liftweb.http.SHtml
import java.util.Locale
import net.liftweb.http.js.JsCmds
import net.liftweb.http.SHtml.SelectableOption

object LoginSnippet extends CustomizeUserHandlingSnippet[User](User,UserSnippet){

//    override def loginTitle = "Custom Login"
//    override def logoutTitle = "Custom Logout"      
//    override def signUpTitle = "Custom Sign up"          
//    override def lostPasswordTitle = "Custom Lost password"      
//    override def resetPasswordTitle = "Custom Reset password" 
      
   
      
      val contentLanguage = UrlLocalizer.contentLocale
      
      val allLanguages = Locale.getISOLanguages().toList.map(new Locale(_))

    def localDispatch : DispatchIt = {      
    	case "account" => account _
    	case "language" => language _
    }
    
    override def dispatch : DispatchIt = localDispatch orElse(super.dispatch)
      
    def language(xhtml: NodeSeq): NodeSeq = { // UrlLocalizer.
//      ajaxSelect(AjaxForm.states.map(s => (s, s)), Full(state), { s =>
//      state = s; After(200, replace(state)) })
    	("#selectLanguage" #> SHtml.ajaxSelect(
    	    allLanguages.map(lang => SelectableOption( lang.getDisplayLanguage, lang.getDisplayLanguage) ),
    	    Full(UrlLocalizer.contentLocale.get.getDisplayLanguage),
    	    {s => UrlLocalizer.contentLocale.set(new Locale(s));JsCmds.Noop}
    	    )).apply(xhtml)
    }
      
    def account(xhtml: NodeSeq): NodeSeq = {
      		// does that make sense?
      		if (User.loggedIn_? )
            	(
            	 "#login" #> "" &
            	 //"#login *" #> "Logout" //&
//            	 "#logoutBtn [href]" #> "/%s".format(userObject.logoutPath.mkString("/")) &
//            	 "#account" #> <span class="icon-"></span> Account  
            	  "#landingsection" #> ""
            )(xhtml)
            	else
            	(
            	 //"#login *" #> "Login" &
            	 "#logout" #> "" &
//            	 "#logoutBtn [href]" #> "/%s".format(userObject.logoutPath.mkString("/")) &
            	"#account" #> ""
            )(xhtml)
  }
}
