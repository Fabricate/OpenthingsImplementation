package at.fabricate
package snippet

import net.liftweb.http.DispatchSnippet
import net.liftweb.common.Logger
import scala.xml.NodeSeq
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import at.fabricate.model.Project
import scala.xml.Text
import at.fabricate.lib.MapperBinder
import at.fabricate.model.Comment
import net.liftweb.http.SHtml
import at.fabricate.model.User
import net.liftweb.http.js.JsCmds
import net.liftweb.http.RequestVar
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js.jquery.JqJE.JqId
import net.liftweb.http.js.jquery.JqJE.JqAppend
import net.liftweb.http.js.jquery.JqJE.JqAppendTo
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Descending
import net.liftweb.http.SHtmlJ
import net.liftweb.http.SHtmlJBridge
import net.liftweb.http.SHtml.ElemAttr



object ProjectSnippet extends AjaxPaginatorSnippet[Project] with DispatchSnippet with Logger {
  
  private val ID_NOT_SUPPLIED = "-1"
    
  def dispatch : DispatchIt = {
    case "list" => renderIt(_)
    case "renderIt" => renderIt(_)
    case "edit" => edit _
    case "view" => view(_)
    case "paginate" => paginate _
    case "paginatecss" => paginatecss(_)
  }
  
  // define the page
  override def count = Project.count

  override def page = Project.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(Project.id, Descending))

  
  private def withObject(op : Project => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    S.param("id").get match {
      case Project.MatchItemByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    
  }
	
  private def edit(xhtml: NodeSeq) : NodeSeq  =  { 
    // just a dummy implementation
       val project : Project = (S.param("id") openOr ID_NOT_SUPPLIED) match {
      case Project.MatchItemByID(editProject) => {
        println("project with id %d found".format(editProject.id.get))        
        println("project title: %s".format(editProject.title.get))
        editProject
      }
      case _ => {
        // create a new project
        Project.create
      }
        }
       def saveChanges = {      
        println("project with id %d created".format(project.id.get))        
        println("project title: %s".format(project.title.get))      
            project.validate match {
              case Nil => {
	            project.save
	        	S.notice("Änderungen gespeichert")
	        	S.redirectTo("/project/"+project.id.toString)
              }
              case _ => warn("Error validating designer!")
            }
        }
         ("#item" #> {MapperBinder.bindMapper(project,{
             "#save" #> SHtml.submit( "save", () => saveChanges )
        }) _ } 
         ).apply(xhtml)
        
  }
    private def list_removed:  CssSel =   
    // just a dummy implementation
   "#item" #> { Project.findAll.map(project => MapperBinder.bindMapper(project, {
     "#toitem [href]" #> project.id &      
     "#toitem *" #> "View Item"
   }) _)}
   
   private def bindListCSS(project: Project) : CssSel= {
     "#designerimage [src]" #> project.icon .url &
     "#designerpage [href]" #> "/project/%s".format(project.id.toString) &     
     "#designername *" #> "%s".format(project.id) &
     "#description *" #> project.teaser.asHtml &
     "#designerpage *" #> "View Item"
   }
   
    def renderIt (in: scala.xml.NodeSeq) : scala.xml.NodeSeq =   
    // just a dummy implementation
   ("#item" #> {page.map(project => MapperBinder.bindMapper(project, {
     "#toitem [href]" #> "/project/%s".format(project.id.toString) &      
     "#toitem *" #> "View Item"
   }) _)}).apply(in)
//   ("#designer" #> page.map(project => bindListCSS(project))).apply(in)
   
  private def view (xhtml: NodeSeq) :  NodeSeq  =  {
    var commentTemplate = xhtml
    var theTemplate = xhtml
    // get just the comment section
    ("#comment" #> {(node : NodeSeq) => {
      commentTemplate = node
      Text("removed")}}).apply(theTemplate)
//    println("template: "+theTemplate)
//    println("template cssselected:" +commentTemplate )
//      AppendHtml()
    S.param("id").get match {
      case Project.MatchItemByID(project) => {         
    	  var newComment: project.TheComment = project.TheComment.create.commentedItem(project)
    	 def bindCommentCSS(comment: project.TheComment) : CssSel= {
		     "#commenttitle *" #> comment.title.asHtml &
		     "#commentauthor *" #> "Posted by: %s".format(comment.author.asHtml) &     
		     "#commentmessage *" #> comment.comment.asHtml
		   }
    	 def bindNewCommentCSS : CssSel= {
		     "#newcomtitle" #> SHtml.ajaxText(newComment.title.get, value => {newComment.title.set(value);JsCmds.Noop}, "default"->"Title" )&
		     "#newcomauthor" #> SHtml.ajaxText(newComment.author.get, value => {newComment.author.set(value);JsCmds.Noop}, "default"->"Name"  )&     
		     "#newcommessage" #> SHtml.ajaxTextarea(newComment.comment.get, value => {newComment.comment.set(value);JsCmds.Noop}, "default"->"Your comment" ) & // rows="6"
		     "#newcomsubmit [onclick]" #> SHtml.ajaxInvoke(() => {
//		       newComment.validate &&
					            if ( newComment.save) {
					              var newCommentHtml = bindCommentCSS(newComment)(commentTemplate)
					              newComment = project.TheComment.create.commentedItem(project)
					              // add the new comment to the list of comments
					              AppendHtml("comments", newCommentHtml) &
					              // clear the form
					              JsCmds.SetValById("newcomtitle", "") &
					              JsCmds.SetValById("newcommessage", "")
					              // TODO: maybe clear the form or remove the latest JScommand?
		//			                  ("#comment" #> bindCommentCSS(newComment)).apply(commentTemplate))
		//			              JsRaw("$('#comments').append( '<li>Test</li>' );")
		//			              JsCmds.Alert("added comment "+commentTitle)
		
					            }
					            else
					              JsCmds.Alert("adding comment '"+newComment.title.get+"' failed" )
					          } )
		   } 
        ("#dbcontent" #> { MapperBinder.bindMapper(project, {
     "#icon [src]" #> project.icon .url &
     "#comment" #> project.comments.map(comment => bindCommentCSS(comment))  &
     "#newcomment" #> bindNewCommentCSS
   }) _ }
     ).apply (xhtml)
    }
      case _ => Text("Object not found!")
    }
   }
}