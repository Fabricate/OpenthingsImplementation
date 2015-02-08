package at.fabricate.openthings
package snippet

import net.liftweb.http.DispatchSnippet
import scala.xml.NodeSeq
import net.liftweb.sitemap.Menu
import net.liftweb.util.Mailer.Subject
import net.liftweb.util.Mailer.From
import net.liftweb.util.Mailer
import net.liftweb.util.Mailer.To
import net.liftweb.http.S
import net.liftweb.util.Mailer.PlainMailBodyType
import net.liftweb.http.RequestVar
import net.liftweb.http.SHtml
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.util.Mailer.ReplyTo
import net.liftweb.common.Empty

object FeedbackSnippet  extends DispatchSnippet {
  
  def dispatch : DispatchIt = {    
    case "mail" => mail _
//    case "logout" => logout _
//    case "edit" => edit _
//    case "signup" => signup _
//    case "validateUser" => validateUser _
//    case "changePassword" => changePassword _
//    case "lostPassword" => lostPassword _
//    case "resetPassword" => resetPassword _
  }
  
    def contactTitle = "Contact"      
    def contactTemlate = "contact" 
    def feedbackTitle = "Feedback"      
    def feedbackTemlate = "feedback" 
//    
////    def loggedInMessage : NodeSeq = Text("ERROR - You are already logged in!")
////    def notLoggedInMessage : NodeSeq = Text("ERROR - You are not logged in!")
//
//
    def getMenu = 
       List[Menu](
               Menu.i(contactTitle) / contactTemlate ,
               Menu.i(feedbackTitle) / feedbackTemlate 
     )
//	  def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
//	      case RewriteRequest(ParsePath(MatchResetPassword(resetPath):: anID :: Nil, _, _, _), _, _) => { 
//	        requestedID.set(anID)
//	        RewriteResponse(resetPath :: Nil)
//	      }
//	      case RewriteRequest(ParsePath(MatchValidateUser(validatePath):: anID :: Nil, _, _, _), _, _) =>{ 
//	        requestedID.set(anID)
//		      RewriteResponse(validatePath :: Nil)	  	  
//	      }
//	    }
     
    val recipients = List("openthingsteam@gmail.com","hello@martinr.nl")
     
    def sendPlainMail(from : String, replyTo : String, subject : String, to : List[String], content : String) = 
            to.map(recipient => Mailer.sendMail(From(from),Subject(subject), To(recipient), PlainMailBodyType(content), ReplyTo(replyTo))
                         )
                         
    def mail(xhtml : NodeSeq): NodeSeq = {
      object from extends RequestVar("")
      object subject extends RequestVar("")
      object message extends RequestVar("")
      object options extends RequestVar("")
      val selectableOptions = List(
          "It's awesome"->"It's awesome",
          "I don't see the use of it"->"I don't see the use of it"
          )
      
       ("#message" #> SHtml.textareaElem(message) &
        "#from" #> SHtml.textElem(from) &
        "#subject" #> SHtml.textElem(subject) &
        "#options" #> SHtml.select(selectableOptions, Empty, options.set(_)) &
        "#mailaction" #> SHtml.hidden(() => {
          sendPlainMail("openthingsserver@gmail.com",from.get, subject.get, recipients , "Option: "+options.get+"\n\n Message: \n"+message.get)
          S.notice("Thank you for your message!")
          S.redirectTo("/index")
          })
    		   ).apply(xhtml)
//
      
    }

}