package at.fabricate.openthings.lib

import at.fabricate.liftdev.common.lib.UrlLocalizer
import net.liftweb.http.RequestVar
import net.liftweb.http.S
import at.fabricate.liftdev.common.model.DifficultyEnum
import at.fabricate.liftdev.common.model.StateEnum
import at.fabricate.liftdev.common.model.LicenceEnum
import scala.util.Sorting
import java.util.Locale
import net.liftweb.common.Full
import net.liftweb.mapper.QueryParam
import net.liftweb.mapper.StartAt
import at.fabricate.openthings.model.Project
import net.liftweb.mapper.Like
import net.liftweb.mapper.In
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionIconAndCommonFields
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import net.liftweb.mapper.ByList
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.MaxRows

trait ProjectSearch  {

        val contentLanguage = UrlLocalizer.contentLocale
    
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
          case kids if kids == kidsDiffString  => DifficultyEnum.upToKidsList
          case starter if starter == starterDiffString  => DifficultyEnum.upToStarterList
          case average if average == averageDiffString  => DifficultyEnum.upToAverageList
          case advanced if advanced == advancedDiffString  => DifficultyEnum.upToAdvancedList
          case expert if expert == expertDiffString  => DifficultyEnum.upToExpertList
          case _  => DifficultyEnum.upToGeniusList
    }) openOr(DifficultyEnum.upToGeniusList)) 
        
        
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
    
        
    val allLicenceString = "all"
    val commercialLicencesString = "com"
    val derivableLicencesString = "deriv"
    
    val licenceParam = "licence"
    object licence extends RequestVar(S.param(licenceParam).map(_ match {
          case commercial if commercial == commercialLicencesString  => LicenceEnum.commercialLicences
          case derivable if derivable == derivableLicencesString  => LicenceEnum.derivableLicences
          case _  => LicenceEnum.allLicences 
    }) openOr(LicenceEnum.allLicences))
        
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
    
      def addLikeCharFrontAndBack(queryParam:String) : String = "%"+queryParam.replaceAll("\\*", "%")+"%"

  
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] // = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
  
  def queryItems[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]](itemToQuery: BaseMetaEntityWithTitleDescriptionIconAndCommonFields[T] with BaseEntityWithTitleDescriptionIconAndCommonFields[T], otherQueryParams : List[QueryParam[T]] = List() ) = { // , otherQueryParams = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
    val query = 

        In(itemToQuery.primaryKeyField,itemToQuery.TheTranslationMeta.translatedItem,Like(itemToQuery.TheTranslationMeta.title,addLikeCharFrontAndBack(title.get))) ::
        In(itemToQuery.primaryKeyField,itemToQuery.TheTranslationMeta.translatedItem,Like(itemToQuery.TheTranslationMeta.description,addLikeCharFrontAndBack(description.get))) ::
        In(itemToQuery.primaryKeyField,itemToQuery.TheTranslationMeta.translatedItem,Like(itemToQuery.TheTranslationMeta.language,addLikeCharFrontAndBack(languageToSearchFor.get))) ::
    	ByList(itemToQuery.state,state.get) ::
    	ByList(itemToQuery.difficulty,difficulty.get) ::
    	ByList(itemToQuery.licence,licence.get) ::
    	otherQueryParams
    	
    itemToQuery.findAll(
        query :_*
        )
  }
  
               // define the page
  def numberOfItems = queryItems[Project](Project).length

  def listOfCurrentItems = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])

        /*
       // define the page
  override def count = queryItems[Project](Project).length

  override def page = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])

*/
        
  
}