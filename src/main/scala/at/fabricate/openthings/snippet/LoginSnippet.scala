package at.fabricate.openthings
package snippet

import model.User
import at.fabricate.liftdev.common.snippet.{AddSkillsSnippet, CustomizeUserHandlingSnippet}
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
import net.liftweb.http.S

object LoginSnippet extends CustomizeUserHandlingSnippet[User](User,UserSnippet) {


      val contentLanguage = UrlLocalizer.contentLocale
      
    def localDispatch : DispatchIt = {      
    	case "account" => account _
    	case "language" => language _
    }
    
    override def dispatch : DispatchIt = localDispatch orElse(super.dispatch)
      
    def language(xhtml: NodeSeq): NodeSeq = { 
    	("#selectLanguage" #> SHtml.ajaxSelect(
    	    //UrlLocalizer.allLanguages.map(lang => SelectableOption( lang.getDisplayLanguage, lang.getDisplayLanguage) ),
    	    UrlLocalizer.available_locales_templates.map(lang => SelectableOption( lang.getDisplayLanguage, lang.getDisplayLanguage(lang)) ),
    	    Full(UrlLocalizer.getSiteLocale.getDisplayLanguage),
    	    {s => {
//    	      println("language was: "+UrlLocalizer.getSiteLocale);
    	      UrlLocalizer.setSiteLocale(UrlLocalizer.allLanguages.find(_.getDisplayLanguage == s));
//    	      println("switching to language: "+UrlLocalizer.getSiteLocale);
    	      JsCmds.Reload}}
    	    )).apply(xhtml)
    }
      
    def account(xhtml: NodeSeq): NodeSeq = {
      		if (User.loggedIn_? )
            	(
            	 "#login" #> "" &
            	  "#landingsection" #> "" &
            	  "#onlinestatus [class]" #> "loggedIn"
            )(xhtml)
            	else
            	(
            	 "#logout" #> "" &
            	"#account" #> ""&
            	  "#onlinestatus [class]" #> "notLoggedIn"&
            	  "#addproject [onClick]" #> "LoginScreen();"
            	
            )(xhtml)
  }
}
