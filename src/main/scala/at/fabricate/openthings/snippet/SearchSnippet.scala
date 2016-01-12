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
import at.fabricate.liftdev.common.model.DifficultyEnum
import at.fabricate.liftdev.common.model.StateEnum
import net.liftweb.mapper.By
import net.liftweb.mapper.IHaveValidatedThisSQL
import net.liftweb.mapper.BySql
import net.liftweb.mapper.InRaw
import java.util.Locale
import net.liftweb.common.Box
import scala.util.Sorting
import at.fabricate.liftdev.common.lib.UrlLocalizer
import at.fabricate.openthings.lib.ProjectSearch

object SearchSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with ProjectSearch with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

    def generateDiffSelect = 
      <select id="difficulty" name={difficultyParam}>
    	<option value={kidsDiffString} selected={if(difficulty.get == DifficultyEnum.upToKidsList)"selected" else null}>only {DifficultyEnum.kids.description}</option>
    	<option value={starterDiffString} selected={if(difficulty.get == DifficultyEnum.upToStarterList)"selected" else null}>up to {DifficultyEnum.starter.description}</option>
    	<option value={averageDiffString} selected={if(difficulty.get == DifficultyEnum.upToAverageList)"selected" else null}>up to {DifficultyEnum.average.description}</option>
    	<option value={advancedDiffString} selected={if(difficulty.get == DifficultyEnum.upToAdvancedList)"selected" else null}>up to {DifficultyEnum.advanced.description}</option>
    	<option value={expertDiffString} selected={if(difficulty.get == DifficultyEnum.upToExpertList)"selected" else null}>up to {DifficultyEnum.expert.description}</option>
    	<option value={geniusDiffString} selected={if(difficulty.get == DifficultyEnum.upToGeniusList)"selected" else null}>up to {DifficultyEnum.genius.description}</option>
      </select>
    
    object difficultySelect extends RequestVar ( generateDiffSelect	)
    
    def generateStateSelect = 
      <select id="state" name={stateParam}>
    	<option value={matureStateString} selected={if(state.get == StateEnum.downToMatureList)"selected" else null}>only {StateEnum.mature.description}</option>
    	<option value={advancedStateString} selected={if(state.get == StateEnum.downToAdvanced)"selected" else null}>down to {StateEnum.advanced.description}</option>
    	<option value={evolvedStateString} selected={if(state.get == StateEnum.downToEvolved)"selected" else null}>down to {StateEnum.evolved.description}</option>
    	<option value={devAdvancedStateString} selected={if(state.get == StateEnum.downToDevAdvanced)"selected" else null}>down to {StateEnum.dev_advanced .description}</option>
    	<option value={devEarlyStateString} selected={if(state.get == StateEnum.downToDevEarly)"selected" else null}>down to {StateEnum.dev_early .description}</option>
    	<option value={conceptStateString} selected={if(state.get == StateEnum.downToConcept)"selected" else null}>down to {StateEnum.concept .description}</option>
      </select>
    
    object stateSelect extends RequestVar ( generateStateSelect	)
    
    
    def generateLicenceSelect =
	  <select id="licence" name={licenceParam}>
			<option value={allLicenceString } selected={if(licence.get == LicenceEnum.allLicences )"selected" else null}>all licences</option>
			<option value={commercialLicencesString} selected={if(licence.get == LicenceEnum.commercialLicences  )"selected" else null}>commercial licences</option>
			<option value={derivableLicencesString} selected={if(licence.get == LicenceEnum.derivableLicences  )"selected" else null}>derivable licences</option>
	  </select>
			
	object licenceSelect extends RequestVar ( generateLicenceSelect )
	
    
	val generateLanguageSelect = 
	  <select id="language" name={languageParam}>
			<option value={allLanguageString}>all languages</option>
			{
			  searchableLanguages.map(locale => <option value={locale._1 } selected={if(languageToSearchFor.get == locale._1  )"selected" else null}>{ locale._2.getDisplayLanguage() }</option>)
			}
	  </select>
			
	object languageSelect extends RequestVar ( generateLanguageSelect )
	
	override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List(
	    titleParam ->(S.param(titleParam) openOr(""  )),
	    descriptionParam ->(S.param(descriptionParam) openOr(""  )),
	    difficultyParam->(S.param(difficultyParam) openOr(geniusDiffString  )),	 
	    stateParam->(S.param(stateParam) openOr(conceptStateString )),   
	    licenceParam->(S.param(licenceParam) openOr(allLicenceString )),   
	    languageParam->(S.param(languageParam) openOr(allLanguageString ))
	    ))

   
    override val TheItem = Project
  override def itemBaseUrl = "search"
  def searchTemplate = "globalSearch"
  def searchTitle = "Search"
      def theUserSnippet = UserSnippet
  
  val notAvailable = Text("Not avaliable here!")
  
    override def getMenu : List[Menu] = 
     List[Menu](
               Menu.i(searchTitle) / searchTemplate  >> Hidden
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
  }
  
     // ### methods that will be stacked ###
   override def localDispatch : DispatchIt = dispatchForm orElse super.localDispatch
  
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
        "#save *" #> "Search" & 
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
  

  // things that have to be implemented for the pagination
  
    def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))


       // define the page
  override def count = numberOfItems

  override def page = listOfCurrentItems


}
