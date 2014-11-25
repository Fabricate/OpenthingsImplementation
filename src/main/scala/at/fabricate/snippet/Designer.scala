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
import net.liftmodules.widgets.autocomplete.AutoComplete
import at.fabricate.model.Tool
import scala.collection.mutable.Seq
import net.liftweb.http.js.JsCmd

object Designer extends DispatchSnippet with Logger {
  
  def dispatch : DispatchIt = {
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
          var userTools = designer.tools.map(tool => tool.name.toString)          
          var allTools = Tool.findAll.map(tool => tool.name.toString)
          var deleteImage = false

          
          //∗∗Speichert die Änderungen∗/
        def saveChanges = {            
            designer.firstName.set(firstName)
            designer.lastName.set(lastName)
            designer.aboutMe.set(aboutMe)
            if (deleteImage) {
              designer.userImage.set(Array[Byte]())
            } else 
            userImage match {
              case Full(FileParamHolder(_,null,_,_)) => S.error("Sorry - this does not look like an image!")
              case Full(FileParamHolder(_,mime,_,data))
                if mime.startsWith("image/") => 	{
                  val inputStream = userImage.openOrThrowException("User image should not be Empty!").fileStream
                  var metaImage = ImageResizer.getImageFromStream(inputStream)
                  metaImage = ImageResizer.removeAlphaChannel(metaImage)
                  val image = ImageResizer.max(metaImage.orientation, metaImage.image, User.userImage.maxWidth , User.userImage.maxHeight )
                  val jpg = ImageResizer.imageToBytes(ImageOutFormat.jpeg , image, User.userImage.jpegQuality)
                  designer.userImage.set(jpg)
                }
              case Full(FileParamHolder(_,mime,_,data))
                if ! mime.startsWith("image/") =>  S.error("Sorry - this does not look like an image!")
              case Full(_) => S.error("Invalid attachment")
              case _ => warn( "No Attachment: "+userImage )
            }
            
            designer.validate match {
              case Nil => {
	            designer.save
	        	S.notice("Änderungen gespeichert")
	        	S.redirectTo("/designer/"+designer.id.toString)
              }
              case _ => warn("Error validating designer!")
            }
        }
          
          /*
          def suggest(value: String, limit: Int) = 
            allTools.filter(_.toLowerCase.startsWith(value))
            
         def toolSelected(tool: String)(selected: Boolean) : JsCmd = {
            if ( selected ) designer.tools += Tool.create.name(tool)
            else designer.tools -= Tool.create.name(tool)
            designer.save
            net.liftweb.http.js.
          }
            
         def listTools() = {
            allTools.map(tool => SHtml.ajaxCheckbox(userTools.contains(tool), toolSelected(tool)(_))
          }
          * 
          */
          
            
                /*
          def addTool() : JsCmd = {
            Tool.create.name(newTool).save
            net.liftweb.http.js.jquery.JqJsCmds.AppendHtml("tools",
                SHtml.checkbox(userTools.contains(newTool), toolSelected(newTool) _ )
                )
          }
          * 
          */
         def toolSelected(toolName: String)(selected: Boolean) = {
           val tool = Tool.findByName(toolName).head
            if ( selected ) designer.tools += tool
            else designer.tools -= tool
          }
         
          def listTools(template: NodeSeq) : NodeSeq  =             
            allTools.flatMap(tool => bind("tools",template,
                "checkbox" -> SHtml.checkbox(userTools.contains(tool), toolSelected(tool) _ ),
                "name" -> tool
                )
              )
          

          bind("dsigner", xhtml,
               //"atomLink" -> <link href={"/api/account/" + acct.id} type="application/atom+xml" rel="alternate" title={acct.name + " feed"} />,
               "firstname" -> SHtml.text(firstName, firstName = _) ,
               "lastname" -> SHtml.text(lastName, lastName = _) ,
               "aboutme" -> SHtml.textarea(aboutMe, aboutMe = _) ,
               "showimage" -> designer.userImage.asHtml,
               "newimage" -> SHtml.fileUpload(fph => userImage = Full(fph)),
               "deleteimage" -> SHtml.checkbox(false, deleteImage = _ ),
               "listtools" -> listTools _,
               ////"tools" -> AutoComplete("", suggest, newTool = _ ),
               "addtool" -> <a href="/tool/create">Create new tool</a> ,
               //"savetool" -> SHtml.ajaxSubmit("Hinzufügen", addTool ),
               "save" -> SHtml.submit( "Speichern", () => saveChanges )
               //"tags" -> Text(entry.tags.map(_.name.is).mkString(", ")),
               //"tags" -> tags,
               
               )
        }
        case _ => warn("You must be logged in!"); Text("You must be logged in!")
      }
  }
    
  def view (xhtml: NodeSeq) : NodeSeq = S.param("id") match {
    case Full(AsLong(designerID)) => {
      User.findAll(By(User.id , designerID)) match {
        case designer :: Nil => {
          
          //val currentUser : User = User.find(By(User.id,designerID)) openOrThrowException "User not found!"
          //currentUser.view
          val userTools = designer.tools.map(tool => tool.name.toString)
          
          def listTools(template: NodeSeq) : NodeSeq  =             
            userTools.flatMap(tool => bind("tools",template,
                //"checkbox" -> SHtml.checkbox(userTools.contains(tool), _ => true,  disabled="true"),
                "name" -> tool
                )
                )

          bind("dsigner", xhtml,
               "firstname" -> designer.firstName.asHtml,
               "lastname" -> designer.lastName.asHtml,
               "aboutme" -> TextileParser.toHtml(designer.aboutMe.get),
               "image" -> designer.userImage.asHtml,
               "listtools" -> listTools _ ,
               "membersince" -> designer.createdAt.asHtml 
               )
        }
        case _ => warn("Couldn't locate designer \"%s\"".format(designerID)); Text("Could not locate designer " + designerID)
      }
    }
    case _ => Text("No account name provided")
  }
}

