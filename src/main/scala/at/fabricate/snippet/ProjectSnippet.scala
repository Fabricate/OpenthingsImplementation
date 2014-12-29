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
import net.liftweb.mapper.Mapper
import at.fabricate.model.BaseEntity
import at.fabricate.model.BaseRichEntity



object ProjectSnippet extends AjaxPaginatorSnippet[Project] with DispatchSnippet with Logger {
  
  // Problem with User: User Item with negative ID matches to the actual user (or is that just because the field was null?)
  private val ID_NOT_SUPPLIED = "-1"
    
    val TheItem = Project
    
    type ItemType = Project
    
    val itemBaseUrl = "project"
    
  def dispatch : DispatchIt = {
    case "list" => renderIt(_)
    case "renderIt" => renderIt(_)
    case "edit" => edit _
    case "create" => create _
    case "view" => view(_)
    case "paginate" => paginate _
    case "paginatecss" => paginatecss(_) 
  }
  
  // define the page
  override def count = TheItem.count

  override def page = TheItem.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(Project.id, Descending))

  
  private def doWithMatchedItem(op : Project => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    (S.param("id") openOr ID_NOT_SUPPLIED) match {
      case TheItem.MatchItemByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    
  }
  
   private def save[T](item : Mapper[_], successAction : () => T, errorAction : List[FieldError] => T) : T = 
            item.validate match {
              case Nil => {
	            item.save	        	
	        	successAction()
              }
              case errors => {
                errorAction(errors)
              }
            }
                
   // TODO:
   // for a more detailed error message have a look at
   // https://groups.google.com/forum/#!topic/liftweb/4LCWldUaUVA
   private def saveAndDisplayAjaxMessages(item : Mapper[_], 
       successAction : () => JsCmd = () => JsCmds.Noop, 
       errorAction : List[FieldError] => JsCmd = errors => JsCmds.Noop, 
       idToDisplayMessages : String, 
       successMessage : String  = "Saved changes!", errorMessage: String  = "Error saving item!") : JsCmd =
		   save[JsCmd](item,
		    () => {
		      // TODO: maybe decide which one to use?
		      // S.xxx or DisplayMessage
			   S.notice(successMessage)
			   DisplayMessage(idToDisplayMessages, <span class="message">{successMessage}</span>, 5 seconds, 1 second) &
			   successAction()
		     },
		     errors => {
		      // TODO: maybe decide which one to use?
		      // S.xxx or DisplayMessage
		       S.error(errorMessage)
               S.error(errors)
               DisplayMessage(idToDisplayMessages, <span class="message error">{errorMessage}<br></br> <ul> {errors.map(error => <li> {error.msg } </li>) } </ul> </span>, 5 seconds, 1 second) &
               errorAction(errors)
                }
		     )

	private def saveAndDisplayMessages(item : Mapper[_], 
       successAction : () => Unit = () => Unit, 
       errorAction : List[FieldError] => Unit = errors => Unit, 
       idToDisplayMessages : String, 
       successMessage : String  = "Saved changes!", errorMessage: String  = "Error saving item!") : Unit =
		   save[Unit](item,
		    () => {
			   S.notice(successMessage)
			   successAction()
		     },
		     errors => {
		       S.error(errorMessage)
               S.error(errors)
               errorAction(errors)
                }
		     )
   
   private def saveAndRedirectToNewInstance[T](saveOp : ( Mapper[_], () => T,  List[FieldError] => T) => T, item : BaseRichEntity[_], 
       successAction : () => T = () => (), 
       errorAction : List[FieldError] => T = (errors : List[FieldError]) => ()) : T = 
     saveOp(item, () => {
    	 S.redirectTo("/%s/%s".format(itemBaseUrl, item.id.toString))
    	 successAction()
    	 },
    	 errors => errorAction(errors))
   
  
  private def create(xhtml: NodeSeq) : NodeSeq  = toForm(TheItem.create)(xhtml)
  
//   abstract override
  private def toForm(item : ItemType) : CssSel = {

         "#item" #> {MapperBinder.bindMapper(item,{
             "#save" #> SHtml.submit( "save", () => 
//               saveAndRedirectToNewInstance((item, success: () => Unit, errors: List[FieldError] => Unit) => saveAndDisplayMessages(item,success,errors, "") , item,
               saveAndRedirectToNewInstance(saveAndDisplayMessages(_,_:()=>Unit,_:List[FieldError]=>Unit, "") , item)
               )
        }) _ } 
  }
	
  private def edit(xhtml: NodeSeq) : NodeSeq  =  { 
    doWithMatchedItem{
      project => toForm(project)
    }(xhtml)        
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
		     "#newcomtitle" #> SHtml.text(newComment.title.get, value => {newComment.title.set(value);JsCmds.Noop}, "default"->"Title" )&
		     "#newcomauthor" #> SHtml.text(newComment.author.get, value => {newComment.author.set(value);JsCmds.Noop}, "default"->"Name"  )&     
		     "#newcommessage" #> SHtml.textarea(newComment.comment.get, value => {newComment.comment.set(value);JsCmds.Noop}, "default"->"Your comment" ) & // rows="6"
		     "#newcomsubmithidden" #> SHtml.hidden(() => {
		       saveAndDisplayAjaxMessages(newComment, 
		           () => {
		            var newCommentHtml = bindCommentCSS(newComment)(commentTemplate)
		        	newComment = project.TheComment.create.commentedItem(project)
					 // add the new comment to the list of comments
					AppendHtml("comments", newCommentHtml) &
					 // clear the form
					JsCmds.SetValById("newcomtitle", "") &
					JsCmds.SetValById("newcommessage", "")
		           }, 
		           errors => {
		             JsCmds.Alert("adding comment '"+newComment.title.get+"' failed" )
		           },"commentMessages")
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