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
import at.fabricate.openthings.model.User
import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleAndDescriptionSnippet
import at.fabricate.openthings.model.Project
import net.liftweb.common.Full

object ContactMakerSnippet  extends DispatchSnippet {
  
  def dispatch : DispatchIt = {    
    case "mail" => mail _
  }
  /*
    def contactTitle = "Contact"      
    def contactTemlate = "contact" 
    def feedbackTitle = "Feedback"      
    def feedbackTemlate = "feedback" 

    def getMenu = 
       List[Menu](
               Menu.i(contactTitle) / contactTemlate ,
               Menu.i(feedbackTitle) / feedbackTemlate 
     )

    val recipients = List("openthingsteam@gmail.com","hello@martinr.nl")
     * 
     */
     
    def sendPlainMail(from : String, replyTo : String, subject : String, to : List[String], content : String) = 
            to.map(recipient => Mailer.sendMail(From(from),Subject(subject), To(recipient), PlainMailBodyType(content), ReplyTo(replyTo))
                         )
                         
    def mail(xhtml : NodeSeq): NodeSeq = {
      object from extends RequestVar("")
      object name extends RequestVar("")
      object subject extends RequestVar("")
      object message extends RequestVar("")
      object options extends RequestVar("")
      
       ("#message" #> SHtml.textareaElem(message) &
        "#from" #> SHtml.textElem(from) &
         "#name" #> SHtml.textElem(name) &
        "#subject" #> SHtml.textElem(subject) &
        "#mailaction" #> SHtml.hidden(() => {
          
         val theProject = (S.param("id") openOr -1) match {
      case Project.MatchItemByID(item) => Full(item)
      case _ => Empty
  }
         
          
            val from = User.currentUser.map { aUser => aUser.email.get }
            
          if (from.isDefined && theProject.isDefined){
            val to = List(theProject.openOrThrowException("Empty Box opened").createdByUser.obj.openOrThrowException("Empty Box opened").email.get) 
              //List("hello")
            
          sendPlainMail("openthingsserver@gmail.com", from.openOrThrowException("Empty Box opened") , subject.get, to ,"Name: "+name.get+"\n\n Option: "+options.get+"\n\n Message: \n"+message.get)
          S.notice("Thank you for your message!")
             println("Message was sent!")
          //S.redirectTo("/index")
          }
          // TODO: else project was not found
          else {
             S.error("Either no Sender Mail available or no Project data available")
             println("Either no Sender Mail available or no Project data available")
          }
          })
    		   ).apply(xhtml)
      
    }

}