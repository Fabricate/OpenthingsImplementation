package at.fabricate.openthings.snippet

import java.util.Locale

import at.fabricate.liftdev.common.lib.UrlLocalizer
import at.fabricate.liftdev.common.model.{BaseEntityWithTitleAndDescription, BaseEntityWithTitleDescriptionIconAndCommonFields, BaseMetaEntityWithTitleDescriptionIconAndCommonFields, AddSkills, AddSkillsMeta}
import at.fabricate.liftdev.common.snippet.{AddSkillsSnippet, BaseEntityWithTitleAndDescriptionSnippet, BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet}
import at.fabricate.openthings.model.Project
import net.liftweb.common.Full
import net.liftweb.http.{ParsePath, RequestVar, RewriteRequest, RewriteResponse, S}
import net.liftweb.mapper.{By, Descending, In, KeyedMapper, MaxRows, OrderBy, QueryParam, StartAt}
import net.liftweb.sitemap.Loc._
import net.liftweb.sitemap.Menu
import net.liftweb.util.Helpers._

import scala.util.Sorting
import scala.xml.{NodeSeq, Text}

object SkillSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

      
      val contentLanguage = UrlLocalizer.contentLocale
    
    val skillParam = "skillID"
    object skill extends RequestVar( S.param(skillParam) match {
      case Full(AsLong(anID)) => anID
      case _ => -1L
    }) //get.toLong).openOr(-1L)

	
	def searchableLanguages : List[(String,Locale)] = Sorting.stableSort(
	  			Locale.getISOLanguages.toList.map(l => l -> new Locale(l)),
	  			(e1: Tuple2[String, Locale], e2: Tuple2[String, Locale]) => e1._2.getDisplayLanguage < e2._2.getDisplayLanguage ).toList
	  			
	def findInSearchableLanguages(selectedLanguage : String) : Option[Tuple2[String,Locale]] = searchableLanguages.find({ case ((language:String,locale:Locale)) => language == selectedLanguage})
	  
	  
	val allLanguageString = "*" // else "%"
	val languageParam = "language"

	object languageToSearchFor extends RequestVar[String](S.param(languageParam) match {
	  case Full(selectedLanguage) => {
	    findInSearchableLanguages(selectedLanguage).map(_._1).getOrElse(allLanguageString)
	  }
	  case _ => allLanguageString
	} 
    ) 
	
    
	val generateLanguageSelect = 
	  <select id="language" name={languageParam}>
			<option value={allLanguageString}>all languages</option>
			{
			  searchableLanguages.map(locale => <option value={locale._1 } selected={if(languageToSearchFor.get == locale._1  )"selected" else null}>{ locale._2.getDisplayLanguage() }</option>)
			}
	  </select>
			
	object languageSelect extends RequestVar ( generateLanguageSelect )


	override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List(
    skillParam ->(S.param(skillParam) openOr(""  ))
	    ))

   
    override val TheItem = Project
  override def itemBaseUrl = "skill"
  def skillTemplate = "globalSkill"
  def skillTitle = "Skill"

      def theUserSnippet = UserSnippet
  
  val notAvailable = Text("Not avaliable here!")
  
    override def getMenu : List[Menu] = 
     List[Menu](
               Menu.i(skillTitle) / skillTemplate  >> Hidden
     )
      // generate the url rewrites
  override def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] =  {
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath), AsLong(skillID)), _, _, _), _, _) =>
        RewriteResponse(skillTemplate :: Nil, Map("skillID" -> urlDecode(skillID.toString)))
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath)), _, _, _), _, _) =>
        RewriteResponse(skillTemplate :: Nil)
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath), "index"), _, _, _), _, _) =>
        RewriteResponse(skillTemplate :: Nil)
     }

// remove this features
  override def create(xhtml: NodeSeq) : NodeSeq  = notAvailable
  
  override def edit(xhtml: NodeSeq) : NodeSeq  =  notAvailable
  
  override def view(xhtml : NodeSeq) : NodeSeq  =  notAvailable
  
/*
  def dispatchForm : DispatchIt = {    
    case "form" => form _ 
  }
  
     // ### methods that will be stacked ###
   override def localDispatch : DispatchIt = dispatchForm orElse super.localDispatch
*/

  override def urlToViewItem(item: KeyedMapper[_,_], locale : Locale ) : String = item match {
    case project : Project => {
    		findInSearchableLanguages(languageToSearchFor.get) match {
		      case Some((lang:String,locale:Locale)) => ProjectSnippet.urlToViewItem(project, locale)
		      case _ => ProjectSnippet.urlToViewItem(project,locale)
		    }  		
    }
    case _ => "#"
  }
  
  override def urlToEditItem(item: KeyedMapper[_,_], locale : Locale) : String = item match {
    case project : Project => { 
    		findInSearchableLanguages(languageToSearchFor.get) match {
		      case Some((lang:String,locale:Locale)) => ProjectSnippet.urlToEditItem(project, locale)
		      case None => ProjectSnippet.urlToEditItem(project,locale)
		    }
    }
    case _ => "#"
  }

  /*
  def form(xhtml : NodeSeq) : NodeSeq  =  {
    (
        "#searchform [action]"  #> "/%s".format(itemBaseUrl) &
        "#easysearch [name]"  #> titleParam &
        "#easysearch [value]"  #> title.get &
        // customize the form elements
        "#iconlabel *" #> "" &
        "#icon" #> "" &
        "#difficultylabel *" #> "projects with difficulty" &
        "#difficulty" #> difficultySelect &
        "#statelabel *" #> "projects with state" &
        "#state" #> stateSelect &
        "* [required]" #> (None: Option[String]) &
        "#licencelabel" #> (None: Option[String]) &
        "#licence" #> licenceSelect &
        "#save [value]" #> "search" & 
        "#formitem [method]" #> "get" &
        "#formitem [action]" #> itemBaseUrl &
        // insert the search string into the title box
        "#title [name]"  #> titleParam &
        "#title [value]" #> title.get &
        "#description [name]"  #> descriptionParam &
        "#description *" #> description.get &
        "#language"  #> languageSelect &
        "#languagelabel" #> (None: Option[String]) &
        "#defaultlanguage" #> (None: Option[String]) &
        "#defaultlanguagelabel" #> (None: Option[String]) &
        "#description *" #> description.get
        ).apply(xhtml)
  }
  */

  def addLikeCharFrontAndBack(queryParam:String) : String = "%"+queryParam.replaceAll("\\*", "%")+"%"

  
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
  
  def queryItems[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T] with AddSkills[T]](itemToQuery: BaseMetaEntityWithTitleDescriptionIconAndCommonFields[T] with BaseEntityWithTitleDescriptionIconAndCommonFields[T] with AddSkillsMeta[T] with AddSkills[T], otherQueryParams : List[QueryParam[T]] = List() ) = { // , otherQueryParams = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
    // a link to all Tags
    //val mappingToProjectTags  = itemToQuery.getTagMapper

    val query =
        In(itemToQuery.primaryKeyField,itemToQuery.getSkillMapper.taggedItem,By(itemToQuery.getSkillMapper.theSkill,skill.get)) ::
    //By(itemToQuery.id,tag.get) ::
    	otherQueryParams
    	
    itemToQuery.findAll(
        query :_*
        )
  }
  
       // define the page
  override def count = queryItems[Project](Project).length

  override def page = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])

  
}