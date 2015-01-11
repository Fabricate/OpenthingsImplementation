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
import net.liftweb.mapper.By_<
import net.liftweb.mapper.Like
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.KeyedMapper
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel
import net.liftweb.http.RequestVar
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.QueryParam
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionIconAndCommonFields
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedField
import at.fabricate.liftdev.common.model.LicenceEnum
import net.liftweb.http.SHtml
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.mapper.In
import net.liftweb.mapper.ByList

object SearchSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

  

    
    val titleParam = "title"
    object title extends RequestVar(S.param(titleParam) openOr "") //  
      
    val descriptionParam = "description"
    object description extends RequestVar(S.param(descriptionParam) openOr "") //  
       
    val difficultyParam = "difficulty"
    object difficulty extends RequestVar(S.param(difficultyParam).map(value => value match {
      case AsLong(number) if number < 6 => number
      case _ => 5
    }).get) //  
    
    val allLicenceString = "1"
    val commercialLicencesString = "2"
    val derivableLicencesString = "3"
    val allLicences : ( String,String ) = allLicenceString -> "All Licences"
    val commercialLicences : ( String,String ) = commercialLicencesString -> "Commercial Licences"
    val derivableLicences : ( String,String ) = derivableLicencesString  -> "Derivable Licences"
    val licenceSel = List(allLicences , commercialLicences , derivableLicences )
    
    object licence extends RequestVar(LicenceEnum.allLicences  )
//    var licence = allLicences._1
   
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
  
  
  def dispatchForm : DispatchIt = {    
    case "form" => form _ 
//    case "list" => renderIt(_)
  }
  
     // ### methods that will be stacked ###
   override def localDispatch : DispatchIt = dispatchForm orElse super.localDispatch
  
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
        "#easysearch [name]"  #> titleParam &
        "#easysearch [value]"  #> title.get &
        // customize the form elements
        "#iconlabel *" #> "" &
        "#icon" #> "" &
        "#difficultylabel *" #> "Only projects with difficulty not more than" &
        "#difficulty" #> Project.difficulty.toForm &
        "#licence" #> SHtml.select(licenceSel , Empty , _ match {
          case commercial if commercial == commercialLicencesString  => licence.set( LicenceEnum.commercialLicences )
          case derivable if derivable == derivableLicencesString  => licence.set( LicenceEnum.derivableLicences )
          case _  => licence.set( LicenceEnum.allLicences )
        }  ) &
//            {
//          case (licences: ( List[LicenceEnum.Value],String ) ,title:String) => licence.set(licences)
//        }) &  
        // do something like commercializable projects only, or derivable projects
        // for now just remove
//        "#licence" #> "" & 
        "#save [value]" #> "search" & 
        "#formitem [method]" #> "get" &
        // insert the search string into the title box
        "#title [name]"  #> titleParam &
        "#title [value]" #> title.get &
        "#description [name]"  #> descriptionParam &
        "#description *" #> description.get
        ).apply(xhtml)
  }
  
  
//  val description = S.param("description")  openOr ""
//  val description = S.param("description")  openOr ""

  def addLikeCharFrontAndBack(queryParam:String) : String = "%"+queryParam+"%"
  
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
  
  def queryItems[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]](itemToQuery: BaseMetaEntityWithTitleDescriptionIconAndCommonFields[T] with BaseEntityWithTitleDescriptionIconAndCommonFields[T], otherQueryParams : List[QueryParam[T]] = List() ) = { // , otherQueryParams = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
    val query = Like(itemToQuery.title,addLikeCharFrontAndBack(title.get)) ::
    	Like(itemToQuery.description,addLikeCharFrontAndBack(description.get)) ::
//    	By_<(itemToQuery.difficulty.asInstanceOf[MappedField[_,T]],difficulty.get) ::
    	ByList(itemToQuery.licence,licence.get) ::
    	otherQueryParams
    	
    itemToQuery.findAll(
        query :_*
        )
  }
  
       // define the page
  override def count = queryItems[Project](Project).length
//    Project.findAll(Like(Project.title,addLikeCharFrontAndBack(title.get))).length

  override def page = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])
//      StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(TheItem.primaryKeyField, Descending))

//  override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List("your param" -> "value"))
  
}