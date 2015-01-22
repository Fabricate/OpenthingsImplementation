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

object SearchSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

  

    
    val titleParam = "title"
    object title extends RequestVar(S.param(titleParam) openOr "") //  
      
    val descriptionParam = "description"
    object description extends RequestVar(S.param(descriptionParam) openOr "") //  
    
    val kidsDiffString = "onlyKids"
    val starterDiffString = "upToStarter"
    val averageDiffString = "upToAverage"
    val advancedDiffString = "upToAdvanced"
    val expertDiffString = "upToExpert"
    val geniusDiffString = "upToGenius"
    
       
    val difficultyParam = "difficulty"
    object difficulty extends RequestVar(S.param(difficultyParam).map(value => value match {
//          case genius if genius == geniusString  => geniusList
          case kids if kids == kidsDiffString  => DifficultyEnum.upToKidsList
          case starter if starter == starterDiffString  => DifficultyEnum.upToStarterList
          case average if average == averageDiffString  => DifficultyEnum.upToAverageList
          case advanced if advanced == advancedDiffString  => DifficultyEnum.upToAdvancedList
          case expert if expert == expertDiffString  => DifficultyEnum.upToExpertList
          case _  => DifficultyEnum.upToGeniusList
    }) openOr(DifficultyEnum.upToGeniusList)) 
    
    def generateDiffSelect = 
      <select id="difficulty" required="" name={difficultyParam}>
    	<option value={kidsDiffString} selected={if(difficulty.get == DifficultyEnum.upToKidsList)"selected" else null}>only {DifficultyEnum.kids.description}</option>
    	<option value={starterDiffString} selected={if(difficulty.get == DifficultyEnum.upToStarterList)"selected" else null}>up to {DifficultyEnum.starter.description}</option>
    	<option value={averageDiffString} selected={if(difficulty.get == DifficultyEnum.upToAverageList)"selected" else null}>up to {DifficultyEnum.average.description}</option>
    	<option value={advancedDiffString} selected={if(difficulty.get == DifficultyEnum.upToAverageList)"selected" else null}>up to {DifficultyEnum.advanced.description}</option>
    	<option value={expertDiffString} selected={if(difficulty.get == DifficultyEnum.upToExpertList)"selected" else null}>up to {DifficultyEnum.expert.description}</option>
    	<option value={geniusDiffString} selected={if(difficulty.get == DifficultyEnum.upToGeniusList)"selected" else null}>up to {DifficultyEnum.genius.description}</option>
      </select>
    
    object difficultySelect extends RequestVar ( generateDiffSelect	)
    
    val matureStateString = "onlyMature"
    val advancedStateString = "downToAdvanced"
    val evolvedStateString = "downToEvolved"
    val devAdvancedStateString = "downToDevAdvanced"
    val devEarlyStateString = "downToDevEarly"
    val conceptStateString = "downToConcept"
       
    val stateParam = "state"
    object state extends RequestVar(S.param(stateParam).map(value => value match {
          case mature if mature == matureStateString   => StateEnum.downToMatureList  
          case advanced if advanced == advancedStateString  => StateEnum.downToAdvanced 
          case evolved if evolved == evolvedStateString  => StateEnum.downToEvolved  
          case devAdvanced if devAdvanced == devAdvancedStateString  => StateEnum.downToDevAdvanced 
          case devEarly if devEarly == devEarlyStateString  => StateEnum.downToDevEarly 
          case _  => StateEnum.downToConcept 
    }) openOr(StateEnum.downToConcept))   
    
    def generateStateSelect = 
      <select id="state" required="" name={stateParam}>
    	<option value={matureStateString} selected={if(state.get == StateEnum.downToMatureList)"selected" else null}>only {StateEnum.mature.description}</option>
    	<option value={advancedStateString} selected={if(state.get == StateEnum.downToAdvanced)"selected" else null}>down to {StateEnum.advanced.description}</option>
    	<option value={evolvedStateString} selected={if(state.get == StateEnum.downToEvolved)"selected" else null}>down to {StateEnum.evolved.description}</option>
    	<option value={devAdvancedStateString} selected={if(state.get == StateEnum.downToDevAdvanced)"selected" else null}>down to {StateEnum.dev_advanced .description}</option>
    	<option value={devEarlyStateString} selected={if(state.get == StateEnum.downToDevEarly)"selected" else null}>down to {StateEnum.dev_early .description}</option>
    	<option value={conceptStateString} selected={if(state.get == StateEnum.downToConcept)"selected" else null}>down to {StateEnum.concept .description}</option>
      </select>
    
    object stateSelect extends RequestVar ( generateStateSelect	)
    
    val allLicenceString = "all"
    val commercialLicencesString = "com"
    val derivableLicencesString = "deriv"
//    val allLicences : ( String,String ) = allLicenceString -> "All Licences"
//    val commercialLicences : ( String,String ) = commercialLicencesString -> "Commercial Licences"
//    val derivableLicences : ( String,String ) = derivableLicencesString  -> "Derivable Licences"
//    val licenceSel = List(allLicences , commercialLicences , derivableLicences )
    
    val licenceParam = "licence"
    object licence extends RequestVar(S.param(licenceParam).map(_ match {
          case commercial if commercial == commercialLicencesString  => LicenceEnum.commercialLicences
          case derivable if derivable == derivableLicencesString  => LicenceEnum.derivableLicences
          case _  => LicenceEnum.allLicences 
    }) openOr(LicenceEnum.allLicences))
//    var licence = allLicences._1
    
//    	selected="selected" 
    	
    def generateLicenceSelect =
	  <select id="licence" required="" name={licenceParam}>
			<option value={allLicenceString } selected={if(licence.get == LicenceEnum.allLicences )"selected" else null}>all licences</option>
			<option value={commercialLicencesString} selected={if(licence.get == LicenceEnum.commercialLicences  )"selected" else null}>commercial licences</option>
			<option value={derivableLicencesString} selected={if(licence.get == LicenceEnum.derivableLicences  )"selected" else null}>derivable licences</option>
	  </select>
			
	object licenceSelect extends RequestVar ( generateLicenceSelect )
	
	override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List(
	    titleParam ->(S.param(titleParam) openOr(""  )),
	    descriptionParam ->(S.param(descriptionParam) openOr(""  )),
	    difficultyParam->(S.param(difficultyParam) openOr(geniusDiffString  )),	 
	    stateParam->(S.param(stateParam) openOr(conceptStateString )),   
	    licenceParam->(S.param(licenceParam) openOr(allLicenceString ))
	    ))

   
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
        "#difficultylabel *" #> "projects with difficulty" &
//        "#difficulty" #> Project.difficulty.toForm &
        "#difficulty" #> difficultySelect &
        "#statelabel *" #> "projects with state" &
        "#state" #> stateSelect &
//        "#licence" #> SHtml.select(licenceSel , Empty , _ match {
//          case commercial if commercial == commercialLicencesString  => licence.set( LicenceEnum.commercialLicences )
//          case derivable if derivable == derivableLicencesString  => licence.set( LicenceEnum.derivableLicences )
//          case _  => licence.set( LicenceEnum.allLicences )
//        }  ) &
//            {
//          case (licences: ( List[LicenceEnum.Value],String ) ,title:String) => licence.set(licences)
//        }) &  
        // do something like commercializable projects only, or derivable projects
        // for now just remove
//        "#licence" #> "" & 
        "#licence" #> licenceSelect &
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
    val query = 
    	// TODO: this has to be joined later on to get it working again!!!
//      (S.locale)
        //Like(itemToQuery.title,addLikeCharFrontAndBack(title.get)) ::
    	//Like(itemToQuery.description(S.locale),addLikeCharFrontAndBack(description.get)) ::
//      itemToQuery.translations.find(
//    	    By(itemToQuery.TheTranslation.language,S.locale.toString)
//    	By_<(itemToQuery.difficulty.asInstanceOf[MappedField[_,T]],difficulty.get) ::
    	ByList(itemToQuery.state,state.get) ::
    	ByList(itemToQuery.difficulty,difficulty.get) ::
    	ByList(itemToQuery.licence,licence.get) ::
    	//Like[T](itemToQuery.TheTranslation.title,addLikeCharFrontAndBack(title.get)) ::
//    	BySql[T]("? = ?.? AND ?.? LIKE ?",
//    	    IHaveValidatedThisSQL("Johannes Fischer","22-01-2015"),
//    	    itemToQuery.id.dbColumnName,
//    	    itemToQuery.TheTranslation.dbTableName,
//    	    itemToQuery.TheTranslation.translatedItem.dbColumnName,
//    	    itemToQuery.TheTranslation.dbTableName,
//    	    itemToQuery.TheTranslation.title.dbColumnName,
//    	    addLikeCharFrontAndBack(title.get)
//    	    ) ::
//    	BySql[T]("? = ?.? AND ?.? LIKE ?",
//    	    IHaveValidatedThisSQL("Johannes Fischer","22-01-2015"),
//    	    itemToQuery.id.dbColumnName,
//    	    itemToQuery.TheTranslation.dbTableName,
//    	    itemToQuery.TheTranslation.translatedItem.dbColumnName,
//    	    itemToQuery.TheTranslation.dbTableName,
//    	    itemToQuery.TheTranslation.description.dbColumnName,
//    	    addLikeCharFrontAndBack(description.get)
//    	    ) ::    	
    	otherQueryParams
    	
    // quickfix to search in the translation table as well
    val translationQuery = 
      Like(itemToQuery.TheTranslation.title,addLikeCharFrontAndBack(title.get)) ::
      Like(itemToQuery.TheTranslation.description,addLikeCharFrontAndBack(description.get)) ::
      Nil
      
    itemToQuery.TheTranslation.findAll(
        translationQuery :_*
    	).map(_.translatedItem.obj).filter({case Full(foundItem) => true ; case _ => false}).map(_.open_!) :::      
    itemToQuery.findAll(
        query :_*
        )
  }
  
       // define the page
  override def count = queryItems[Project](Project).length
//    Project.findAll(Like(Project.title,addLikeCharFrontAndBack(title.get))).length

  override def page = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])
//      StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(TheItem.primaryKeyField, Descending))

  
}