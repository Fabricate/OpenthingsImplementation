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
import Helpers._
import net.liftweb.http.SHtml.ElemAttr
import net.liftweb.http.SHtml.BasicElemAttr
import at.fabricate.model.Project
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.RequestVar

object Repository extends DispatchSnippet with Logger {
  
  //val id = S.param("id") openOr "No parameter given"
  object id extends RequestVar(S.param("id") openOr "No parameter given")
  
  def dispatch : DispatchIt = {
    //case "listtools" => listtools _
    case "edit" => edit _
    case "button" => button
    //case "view" => view _
  }
  
  /*
  private def getAllToolNames = Tool.findAll.map(tool => tool.name.toString)
  
  private def bindToolsCSS(toolname : String, checkbox: NodeSeq) = 
      ":checkbox" #>  checkbox &
      "id=toolname" #>  toolname
      
    
  private def listtools(xhtml: NodeSeq) : NodeSeq =  
           getAllToolNames.flatMap( toolName =>
           		bindToolsCSS(toolName, SHtml.checkbox(false, (_) => () , BasicElemAttr("value",toolName)) )(xhtml) 
           		)
          

  
  private def edit(xhtml: NodeSeq) : NodeSeq  =  { 
      User.currentUser match {
        case Full(designer) => {
          var firstName = designer.firstName.toString
          var lastName = designer.lastName.toString
          var aboutMe = designer.aboutMe.toString
          var userImage : Box[FileParamHolder] = Empty
          var userTools = designer.tools.map(tool => tool.name.toString)          
          val allTools = getAllToolNames
          var deleteImage = false

          
          //∗∗Speichert die Änderungen∗/
        def saveChanges = {            
            designer.firstName.set(firstName)
            designer.lastName.set(lastName)
            designer.aboutMe.set(aboutMe)
            if (deleteImage) {
            	designer.icon.set(Array[Byte]())
            } else {
            	designer.icon.setFromUpload(userImage)
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
          
        def bindFieldsCSS =  {
                    //bind("dsigner", xhtml,
               //"atomLink" -> <link href={"/api/account/" + acct.id} type="application/atom+xml" rel="alternate" title={acct.name + " feed"} />,
               "#firstname" #> SHtml.text(firstName, firstName = _) &
               "#lastname" #> SHtml.text(lastName, lastName = _) &
               "#aboutme" #> SHtml.textarea(aboutMe, aboutMe = _) &
               "#showimage" #> designer.icon.asHtml &
               "#newimage" #> SHtml.fileUpload(fph => userImage = Full(fph)) &
               "#deleteimage" #> SHtml.checkbox(false, deleteImage = _ ) &
               "#listtools" #> listTools _ &
               ////"tools" -> AutoComplete("", suggest, newTool = _ ),
               "#addtool" #> <a href="/tool/create">Create new tool</a>  &
               //"savetool" -> SHtml.ajaxSubmit("Hinzufügen", addTool ),
               "#save" #> SHtml.submit( "Speichern", () => saveChanges )
               //"tags" -> Text(entry.tags.map(_.name.is).mkString(", ")),
               //"tags" -> tags,
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
            allTools.flatMap( toolName =>
           		bindToolsCSS(toolName, SHtml.checkbox(userTools.contains(toolName), toolSelected(toolName) _ ))(template) 
           		)          

              bindFieldsCSS(xhtml)
               
             //  )
        }
        case _ => warn("You must be logged in!"); Text("You must be logged in!")
      }
  }
  * 
  */
  
  def callback(id: String)() : JsCmd = {
    id match {
      case AsLong(projectID) =>
        Project.find(By(Project.id , projectID)) match {
	        case Full(project)  => {
	          		    //project.repository.createNewRepo
	        	project.repository.getRepo
	        	project.repository.initialCommit
	            // Thread.sleep(1000)
			    JsCmds.Alert("Created repo for project "+projectID)
	        }
	        
	        case _ => JsCmds.Alert("Project not found")
	      }
        case _ => JsCmds.Alert("No Project ID supplied")
    }
      
  }

  def button  = 
//    S.param("id") match {
//    case Full(AsLong(projectID)) => { 
      "button [onclick]" #> SHtml.ajaxInvoke(callback(id))
//    }
      
//    case _ => JsCmds.Alert("No Project ID supplied")projectID
//    }
    
  def edit (xhtml: NodeSeq) : NodeSeq = S.param("id") match {
    case Full(AsLong(projectID)) => {
      Project.find(By(Project.id , projectID)) match {
        case Full(project)  => {
          /*
                    //∗∗Speichert die Änderungen∗/
	        def saveChanges = {            
	            designer.firstName.set(firstName)
	            designer.lastName.set(lastName)
	            designer.aboutMe.set(aboutMe)
	            if (deleteImage) {
	            	designer.icon.set(Array[Byte]())
	            } else {
	            	designer.icon.setFromUpload(userImage)
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
          //val currentUser : User = User.find(By(User.id,designerID)) openOrThrowException "User not found!"
          //currentUser.view
          val userTools = designer.tools.map(tool => tool.name.toString)
          
          def listTools(template: NodeSeq) : NodeSeq  =             
            userTools.flatMap( toolName =>
           		bindToolsCSS(toolName, SHtml.checkbox(true, (_) => () ))(template) 
           		) 
           		*/
        def createRepo() : JsCmd = {
          Thread.sleep(400)
          project.repository.createNewRepo
          JsRaw("alert(’Created Repository’)")
        }
         def bindFieldsCSS =  {  
           "#process" #> SHtml.hidden(createRepo)
//           "#firstname" #> SHtml.ajaxCall(Str("create repo"), createRepo _ )
           /*
               "#firstname" #> designer.firstName.asHtml &
               "#lastname" #> designer.lastName.asHtml &
               "#aboutme" #> TextileParser.toHtml(designer.aboutMe.get) &
               "#showimage" #> designer.icon.asHtml &
               "#listtools" #> listTools _ &
               "#membersince" #> designer.createdAt.asHtml   
               * 
               */             
          }

           bindFieldsCSS(xhtml)

        }
        case _ => warn("Couldn't locate designer \"%s\"".format(projectID)); Text("Could not locate designer " + projectID)
      }
    }
    case _ => Text("No account name provided")
  }
}

