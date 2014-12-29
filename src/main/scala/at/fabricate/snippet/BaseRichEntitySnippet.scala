package at.fabricate
package snippet

import at.fabricate.model.BaseEntity
import net.liftweb.common.Logger
import net.liftweb.http.DispatchSnippet
import at.fabricate.model.BaseEntityMeta
import scala.xml.NodeSeq
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import model.BaseRichEntityMeta
import model.BaseRichEntity
import net.liftweb.mapper.IdPK
import net.liftweb.http.S
import model.MatchByID
import scala.xml.Text
import net.liftweb.mapper.Mapper
import net.liftweb.util.FieldError
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage
import net.liftweb.util.CssSel
import net.liftweb.http.SHtml
import at.fabricate.lib.MapperBinder
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper
import at.fabricate.model.Project


// if the comments also want to be paginated with the help of this script, 
// a new subtype can be created where T is MappedType and U is MappedMetaType 
// HINT: to redirect and to sort pagination at least a KeyedMapper is needed!
abstract class BaseRichEntitySnippet[T <: BaseRichEntity[T]](TheItem : BaseRichEntityMeta[T] with MatchByID[T] ) extends AjaxPaginatorSnippet[T] with DispatchSnippet with Logger {
//  self: T =>
    
    type ItemType = T
    
  
  // Problem with User: User Item with negative ID matches to the actual user (or is that just because the field was null?)
  private val ID_NOT_SUPPLIED = "-1"
    
    
//    val TheItem = ItemType .getSingleton
    
    val itemBaseUrl = "item"
      
// def edit(xhtml : NodeSeq) : NodeSeq
  
//  def create(xhtml : NodeSeq) : NodeSeq

//  def view(xhtml : NodeSeq) : NodeSeq

      
  def localDispatch : DispatchIt = {    
    case "list" => renderIt(_)
    case "renderIt" => renderIt(_)
    case "edit" => edit _
//    case "create" => create _
    case "view" => view(_)
    case "paginate" => paginate _
    case "paginatecss" => paginatecss(_)
  }
//    orElse super.dispatch
    
  def dispatch : DispatchIt = localDispatch // orElse super.dispatch
  
    
  // define the page
  override def count = TheItem.count

  override def page = TheItem.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(TheItem.primaryKeyField, Descending))
  
//    override def count = 10

//  override def page = List()
 

  
  final def doWithMatchedItem(op : ItemType => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    (S.param("id") openOr ID_NOT_SUPPLIED) match {
      case TheItem.MatchItemByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    
  }
  
   final def save[T](item : Mapper[_], successAction : () => T, errorAction : List[FieldError] => T) : T = 
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
   final def saveAndDisplayAjaxMessages(item : Mapper[_], 
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

	final def saveAndDisplayMessages(item : Mapper[_], 
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
   
   final def saveAndRedirectToNewInstance[T](saveOp : ( Mapper[_], () => T,  List[FieldError] => T) => T, item : KeyedMapper[_,_], 
       successAction : () => T = () => (), 
       errorAction : List[FieldError] => T = (errors : List[FieldError]) => ()) : T = 
     saveOp(item, () => {
    	 S.redirectTo("/%s/%s".format(itemBaseUrl, item.primaryKeyField.toString))
    	 successAction()
    	 },
    	 errors => errorAction(errors))
    	 
  // internal helper fields that will be chained to create the complete css selector
  //   abstract override
  def toForm(item : ItemType) : CssSel = {

         "#item" #> {MapperBinder.bindMapper(item,{
             "#save" #> SHtml.submit( "save", () => 
//               saveAndRedirectToNewInstance((item, success: () => Unit, errors: List[FieldError] => Unit) => saveAndDisplayMessages(item,success,errors, "") , item,
               saveAndRedirectToNewInstance(saveAndDisplayMessages(_,_:()=>Unit,_:List[FieldError]=>Unit, "") , item)
               )
        }) _ } 
  }
  
   //   abstract override
  def asHtml(item : ItemType) : CssSel = {
   "#dbcontent" #> { MapperBinder.bindMapper(item, {"#icon [src]" #> item.icon .url}) _ }
     
//     , {
//     "#icon [src]" #> item.icon .url &
//     "#comment" #> project.comments.map(comment => bindCommentCSS(comment))  &
//     "#newcomment" #> bindNewCommentCSS
//   })
     
//         "#item" #> {MapperBinder.bindMapper(item,{
//             "#save" #> SHtml.submit( "save", () => 
////               saveAndRedirectToNewInstance((item, success: () => Unit, errors: List[FieldError] => Unit) => saveAndDisplayMessages(item,success,errors, "") , item,
//               saveAndRedirectToNewInstance(saveAndDisplayMessages(_,_:()=>Unit,_:List[FieldError]=>Unit, "") , item)
//               )
//        }) _ } 
  }
    	 
  // All the external methodes
  def renderIt (in: scala.xml.NodeSeq) : scala.xml.NodeSeq =   
    // just a dummy implementation
    // later user asHtml
   ("#item" #> {page.map(item => MapperBinder.bindMapper(item, {
     "#toitem [href]" #> "/project/%s".format(item.primaryKeyField.toString) &      
     "#toitem *" #> "View Item"
   }) _)}).apply(in)

  def create(xhtml: NodeSeq) : NodeSeq  = toForm(TheItem.create)(xhtml)
  
	
  def edit(xhtml: NodeSeq) : NodeSeq  =  { 
    doWithMatchedItem{
      item => toForm(item)
    }(xhtml)        
  }    	 
  
  def view(xhtml : NodeSeq) : NodeSeq  =  { 
    doWithMatchedItem{
      item => asHtml(item)
    }(xhtml) 
  }
}