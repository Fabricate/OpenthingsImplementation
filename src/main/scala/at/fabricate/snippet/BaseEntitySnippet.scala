package at.fabricate
package snippet

import net.liftweb.http.DispatchSnippet
import net.liftweb.common.Logger
import at.fabricate.model.BaseRichEntity
import at.fabricate.model.MatchByID
import at.fabricate.model.BaseRichEntityMeta
import net.liftweb.mapper.Descending
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.OrderBy
import scala.xml.NodeSeq
import net.liftweb.http.S
import scala.xml.Text
import net.liftweb.util.FieldError
import net.liftweb.mapper.Mapper
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage
import net.liftweb.mapper.KeyedMapper
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel
import at.fabricate.model.BaseEntity
import at.fabricate.model.BaseEntityMeta
import net.liftweb.util.CssBind
import net.liftweb.util.CssBindImpl
import net.liftweb.util.CSSHelpers
import net.liftweb.http.SHtml
import net.liftweb.http.RewriteRequest
import net.liftweb.http.ParsePath
import net.liftweb.http.RewriteResponse
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc.Hidden

abstract class BaseEntitySnippet[T <: BaseEntity[T]] extends AjaxPaginatorSnippet[T] with DispatchSnippet with Logger {

  // ### Things that have to be defined/refined in subclasses/traits ###
     type ItemType = T
    
     val TheItem : BaseEntityMeta[T] with MatchByID[T]
    
//    val TheItem = ItemType .getSingleton
    
    def itemBaseUrl = "item"    
      
    def itemViewUrl = "view"    
      
    def itemListUrl = "list"
      
//    val itemAddUrl = "add"
      
    def itemEditUrl = "edit"
      
    def snippetView = "viewItem"
      
    def snippetList = "listItem"
      
    def snippetEdit = "editItem"
      
    def menuNameView = "View Item"
      
    def menuNameList = "List Item"
      
    def menuNameEdit = "Edit Item"
      
      
   //### methods that are fix ###
   final def dispatch : DispatchIt = localDispatch // orElse super.dispatch
   
   // generate the url rewrites
   final def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] =  {
      case RewriteRequest(ParsePath(List(itemBaseUrl, "index"), _, _, _), _, _) =>
	      RewriteResponse(snippetList :: Nil)
	  case RewriteRequest(ParsePath(List(itemBaseUrl, itemListUrl), _, _, _), _, _) =>
	      RewriteResponse(snippetList :: Nil)
	  case RewriteRequest(ParsePath(List(itemBaseUrl, itemEditUrl, AsLong(itemID)), _, _, _), _, _) =>
	      RewriteResponse(snippetEdit :: Nil, Map("id" -> urlDecode(itemID.toString)))
	  case RewriteRequest(ParsePath(List(itemBaseUrl, itemEditUrl), _, _, _), _, _) =>
	      RewriteResponse(snippetEdit :: Nil)
	  case RewriteRequest(ParsePath(List(itemBaseUrl, itemViewUrl, AsLong(itemID)), _, _, _), _, _) =>
	      RewriteResponse(snippetView :: Nil, Map("id" -> urlDecode(itemID.toString)))
     }
     
   final def getMenu : List[Menu] = 
     List[Menu](
               Menu.i(menuNameView) / snippetView / ** >> Hidden,
               Menu.i(menuNameList) / snippetList / ** >> Hidden,
               Menu.i(menuNameEdit) / snippetEdit / ** >> Hidden
     )

   // lean pattern to get the Item from the supplied ID
   final def doWithMatchedItem(op : ItemType => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    (S.param("id") openOr ID_NOT_SUPPLIED) match {
      case TheItem.MatchItemByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    
  }
  
   // some handy saving methodes
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
    	 S.redirectTo(urlToViewItem(item))
    	 successAction()
    	 },
    	 errors => errorAction(errors))

    final def urlToViewItem(item: KeyedMapper[_,_]): String =  "/%s/%s/%s".format(itemBaseUrl, itemViewUrl, item.primaryKeyField.toString)
   
    final def urlToEditItem(item: KeyedMapper[_,_]): String =  "/%s/%s/%s".format(itemBaseUrl, itemEditUrl, item.primaryKeyField.toString)

   // ### methods that might be overridden in subclasses ###
    	     	 
   // Problem with User: User Item with negative ID matches to the actual user (or is that just because the field was null?)
  val ID_NOT_SUPPLIED = "-1"

  // All the external methodes
  def renderIt (in: scala.xml.NodeSeq) : scala.xml.NodeSeq =   
    // just a dummy implementation
    // later user asHtml
   ("#item" #> page.map(item => asHtml(item) ) 
       ).apply(in)

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
   
     // define the page
  override def count = TheItem.count

  override def page = TheItem.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(TheItem.primaryKeyField, Descending))

   
   // ### methods that will be stacked ###
   def localDispatch : DispatchIt = {    
    case "list" => renderIt(_)
    case "renderIt" => renderIt(_)
    case "edit" => edit _
    case "create" => create _
    case "view" => view(_)
    case "paginate" => paginate _
    case "paginatecss" => paginatecss(_)
  }
   
     // internal helper fields that will be chained to create the complete css selector
  //   subclasses will implement that methodes with abstract override
  // this is the selector does nothing hopefully
  def toForm(item : ItemType) : CssSel = {
  
      println("chaining toForm from BaseEntitySnippet")
    
     "#title"  #> item.title.toForm &
     "#teaser"  #> item.teaser.toForm &
     "#description"  #> item.description.toForm &
     "#formitem [action]" #> urlToEditItem(item) &
     "#itemsubmithidden" #> SHtml.hidden(() => saveAndRedirectToNewInstance(saveAndDisplayMessages(_,_:()=>Unit,_:List[FieldError]=>Unit, "itemMessages") , item))

//     "#created *"  #> item.createdAt  &
//     "#updated *"  #> item.updatedAt  &
//     "#edititem [href]" #> urlToEditItem(item) &
//     "#viewitem [href]" #> urlToViewItem(item) &
//     "#viewitem *" #> "View Item"
  }
  
  def asHtml(item : ItemType) : CssSel = {
    
    println("chaining asHtml from BaseEntitySnippet")
    
     "#title *"  #> item.title &
     "#teaser *"  #> item.teaser &
     "#description *"  #> item.description &
     "#created *"  #> item.createdAt  &
     "#updated *"  #> item.updatedAt  &
     "#edititem [href]" #> urlToEditItem(item) &
     "#viewitem [href]" #> urlToViewItem(item) &
     "#viewitem *" #> "View Item"
  }
}