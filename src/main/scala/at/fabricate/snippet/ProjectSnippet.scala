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



object ProjectSnippet extends DispatchSnippet with Logger {
    
  def dispatch : DispatchIt = {
    case "list" => list(_)
    case "edit" => edit _
    case "view" => view(_)
  }
  
  private def withObject(op : Project => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    S.param("id").get match {
      case Project.FindByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    }
	
  private def edit(xhtml: NodeSeq) : NodeSeq  =  { 
    // just a dummy implementation
        ("#designername *" #>  "%s %s".format("", "") &
    		"#designerimage" #>  "" &
    		"#designerpage [href]" #> "/designer/%d".format(0)
    )(xhtml)
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
   
    private def list:  CssSel =   
    // just a dummy implementation
//   "#item" #> Project.findAll.map(project => bindListCSS(project))
   "#designer" #> Project.findAll.map(project => bindListCSS(project))
   
  private def view (xhtml: NodeSeq) :  NodeSeq  =  {
    var commentTitle = ""
    var commentAuthor :  String = if (User.loggedIn_?){
      User.currentUser.map(user => "%s %s".format(user.firstName, user.lastName )).get
    } else ""
    var commentMessage = ""
    var commentTemplate = xhtml
    var theTemplate = xhtml
    val newComment: Comment = Comment.create
    // get just the comment section
    ("#comment" #> {(node : NodeSeq) => {
      commentTemplate = node
      Text("removed")}}).apply(theTemplate)
//    println("template: "+theTemplate)
//    println("template cssselected:" +commentTemplate )
   
   def bindCommentCSS(comment: Comment) : CssSel= {
     "#commenttitle *" #> comment.title.asHtml &
     "#commentauthor *" #> "Posted by: %s".format(comment.author.asHtml) &     
     "#commentmessage *" #> comment.comment.asHtml
   }
      
    def addNewComment(project : Project) = {
      newComment.commentedItem(project).
//      val newComment: Comment = Comment.create.commentedItem(project).
      title(commentTitle).
      author(commentAuthor).
      comment(commentMessage)
      newComment.save
    }
    
    def bindNewCommentCSS(project : Project) : CssSel= {
     "#newcomtitle" #> SHtml.ajaxText(commentTitle, commentTitle = _ )&
     "#newcomauthor" #> SHtml.ajaxText(commentAuthor, commentAuthor = _ )&     
     "#newcommessage" #> SHtml.ajaxTextarea(commentMessage, commentMessage = _ ) & // rows="6"
     "#newcomsubmit [onclick]" #> SHtml.ajaxInvoke(() => {
			            if (addNewComment(project)) {
			              JqId("comments") ~> JqAppend( bindCommentCSS(newComment)(commentTemplate) ) &
			              JsCmds.Noop
//			                  ("#comment" #> bindCommentCSS(newComment)).apply(commentTemplate))
//			              JsRaw("$('#comments').append( '<li>Test</li>' );")
//			              JsCmds.Alert("added comment "+commentTitle)

			            }
			            else
			              JsCmds.Alert("adding comment '"+commentTitle+"' failed" )
			          } )
   } 

    S.param("id").get match {
      case Project.FindByID(project) => { 
        ("#dbcontent" #> { MapperBinder.bindMapper(project, {
     "#icon [src]" #> project.icon .url &
     "#comment" #> project.comments.map(comment => bindCommentCSS(comment))  &
     "#newcomment" #> bindNewCommentCSS(project)
   }) _ }
     ).apply (xhtml)
    }
      case _ => Text("Object not found!")
    }
   }
}