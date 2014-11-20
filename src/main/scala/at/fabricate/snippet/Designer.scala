package at.fabricate 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import at.fabricate.lib._
import Helpers._
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import at.fabricate.model.User
import net.liftweb.mapper.By
import net.liftweb.http.SHtml
import net.liftweb.http.FileParamHolder
import net.liftmodules.imaging._
import net.liftmodules.textile.TextileParser

object Designer extends DispatchSnippet with Logger {
  
  def dispatch : DispatchIt = {
    //case "manage" => manage _
    case "edit" => edit _
    case "view" => view _
  }
  
  private def edit (xhtml: NodeSeq) : NodeSeq =  { 
      User.currentUser match {
        case Full(designer) => {
          var firstName = designer.firstName.toString
          var lastName = designer.lastName.toString
          var aboutMe = designer.aboutMe.toString
          var userImage : Box[FileParamHolder] = Empty
          
          //∗∗Speichert die Änderungen∗/
        def saveChanges = {
            
            designer.firstName.set(firstName)
            designer.lastName.set(lastName)
            designer.aboutMe.set(aboutMe)
            // TODO: what about deleting the image???
            userImage match {
              case Full(FileParamHolder(_,null,_,_)) =>  S.notice("Huch")
              case Full(FileParamHolder(_,mime,_,data))
                if mime.startsWith("image/") => 	{
                  val inputStream = userImage.openOrThrowException("User image should not be Empty!").fileStream
                  var metaImage = ImageResizer.getImageFromStream(inputStream)
                  metaImage = ImageResizer.removeAlphaChannel(metaImage)
                  val image = ImageResizer.max(metaImage.orientation, metaImage.image, User.userImage.maxWidth , User.userImage.maxHeight )
                  val jpg = ImageResizer.imageToBytes(ImageOutFormat.jpeg , image, User.userImage.jpegQuality)
                  designer.userImage.set(jpg)
                }
              case Full(_) => S.error("Invalid attachment")
              case _ => {
                S.error("No attachment")
                warn( "No Attachment: "+userImage )
              }
              
            }
            designer.validate match {
              case Nil => {
                
            designer.save
        	S.notice("Änderungen gespeichert")
        	S.redirectTo("/designer/"+designer.id.toString)
              }
            }
        }

          bind("dsigner", xhtml,
               //"atomLink" -> <link href={"/api/account/" + acct.id} type="application/atom+xml" rel="alternate" title={acct.name + " feed"} />,
               "firstname" -> SHtml.text(firstName, firstName = _) ,
               "lastname" -> SHtml.text(lastName, lastName = _) ,
               "aboutme" -> SHtml.textarea(aboutMe, aboutMe = _) ,
               "image" -> SHtml.fileUpload(fph => userImage = Full(fph)),
               "save" -> SHtml.submit( "Speichern", () => saveChanges )
               
               )
        }
        case _ => warn("You must be logged in!"); Text("You must be logged in!")
      }
  }
    
  def view (xhtml: NodeSeq) : NodeSeq = S.param("id") match {
    case Full(AsLong(designerID)) => {
      User.findAll(By(User.id , designerID)) match {
        case designer :: Nil => {
          
          val currentUser : User = User.find(By(User.id,designerID)) openOrThrowException "User not found!"
          //currentUser.view

          bind("dsigner", xhtml,
               "firstname" -> designer.firstName.asHtml,
               "lastname" -> designer.lastName.asHtml,
               "aboutme" -> TextileParser.toHtml(designer.aboutMe.get),
               "image" -> designer.userImage.asHtml
               )
        }
        case _ => warn("Couldn't locate designer \"%s\"".format(designerID)); Text("Could not locate designer " + designerID)
      }
    }
    case _ => Text("No account name provided")
  }
}

