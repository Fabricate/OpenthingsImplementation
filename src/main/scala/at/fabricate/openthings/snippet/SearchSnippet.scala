package at.fabricate.openthings
package snippet

import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleAndDescriptionSnippet
import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.mapper.Descending
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import at.fabricate.openthings.model.Project
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.Loc._
import net.liftweb.http.RewriteRequest
import net.liftweb.http.ParsePath
import net.liftweb.http.RewriteResponse
import net.liftweb.http.S
import net.liftweb.mapper.By
import net.liftweb.mapper.Like
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.KeyedMapper
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel

object SearchSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

  
    val query = S.param(queryParam)  openOr ""

    
    val queryParam = "query"
      
  // will not be used hopefully
    override val TheItem = Project
  override def itemBaseUrl = "search"
//    Dont change anything as it is hardcoded atm
//  override def itemViewUrl = "view"
//  override def itemListUrl = "list"
//  override def itemEditUrl = "edit"
  def searchTemplate = "globalSearch"
  def searchTitle = "Search"
    
//  override def listTemplate = "listProject"
//  override def editTemplate = "editProject"
//  override def listTitle = "List Project"
//  override def editTitle = "Edit Project"
    
      def theUserSnippet = UserSnippet
  
  val notAvailable = Text("Not avaliable here!")
  
    override def getMenu : List[Menu] = 
     List[Menu](
               Menu.i(searchTitle) / searchTemplate  >> Hidden
//               Menu.i(listTitle) / listTemplate ,
//               Menu.i(editTitle) / editTemplate  >> Hidden
     )
      // generate the url rewrites
  override def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] =  {
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath)), _, _, _), _, _) =>
	      RewriteResponse(searchTemplate :: Nil)
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath), "index"), _, _, _), _, _) =>
	      RewriteResponse(searchTemplate :: Nil)
     }

// remove this features
  override def create(xhtml: NodeSeq) : NodeSeq  = notAvailable
  
  override def edit(xhtml: NodeSeq) : NodeSeq  =  notAvailable
  
  override def view(xhtml : NodeSeq) : NodeSeq  =  notAvailable
  
  
     // ### methods that will be stacked ###
   override def localDispatch : DispatchIt = {    
    case "form" => form _ 
    case "list" => renderIt(_)
  }
  
  override def urlToViewItem(item: KeyedMapper[_,_]) : String = item match {
    case project:Project => ProjectSnippet.urlToViewItem(project)
    case _ => "#"
  }
  
  override def urlToEditItem(item: KeyedMapper[_,_]) : String = item match {
    case project:Project => ProjectSnippet.urlToEditItem(project)
    case _ => "#"
  }
  
  def form(xhtml : NodeSeq) : NodeSeq  =  {
    (
        "#searchform [action]"  #> "/%s".format(itemBaseUrl) &
        "#easysearch [name]"  #> queryParam
        ).apply(xhtml)
  }
  
  
//  val description = S.param("description")  openOr ""
//  val description = S.param("description")  openOr ""

  
       // define the page
  override def count = Project.findAll(Like(Project.title,query)).length

  override def page = Project.findAll(Like(Project.title,query), StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(Project.primaryKeyField, Descending))
//      StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(TheItem.primaryKeyField, Descending))

//  override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List("your param" -> "value"))
  
}