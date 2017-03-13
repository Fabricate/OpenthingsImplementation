package at.fabricate.openthings
package api

import net.liftweb.http.rest.RestHelper
import at.fabricate.openthings.model.Project
import net.liftweb.common.Full
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import net.liftweb.mapper.QueryParam
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionIconAndCommonFields
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import net.liftweb.mapper.Like
import net.liftweb.mapper.In
import net.liftweb.mapper.ByList
import net.liftweb.mapper.By
import net.liftweb.mapper.By_>=
import net.liftweb.json.JValue
import net.liftweb.http.rest.JsonSelect
import net.liftweb.http.rest.XmlSelect
import net.liftweb.json.JArray
import at.fabricate.openthings.lib.ProjectSearch
import scala.xml.NodeSeq
import net.liftweb.http.S
import net.liftweb.json.Xml
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import net.liftweb.http.OkResponse
import net.liftweb.http.OkResponse
import net.liftweb.http.RequestVar
import at.fabricate.liftdev.common.model.LicenceEnum
import at.fabricate.liftdev.common.model.DifficultyEnum
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Descending
import at.fabricate.liftdev.common.model.StateEnum
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import net.liftweb.mapper.Ascending

object ProjectREST extends RestHelper { 
  
    val defaultItemsPerPage = 9

    val itemsPerPageParam = "nr_of_items"
    object itemsPerPage extends RequestVar(S.param(itemsPerPageParam).map(value => value.toInt
    ) openOr(defaultItemsPerPage)) 
    
    val defaultPage = 0

    val currentPageParam = "current_page"
    object currentPageNumber extends RequestVar(S.param(currentPageParam).map(value => value.toInt
    ) openOr(defaultPage)) 
    
        val titleParam = "title"
    object title extends RequestVar(S.param(titleParam) openOr "") //  
      
    val descriptionParam = "description"
    object description extends RequestVar(S.param(descriptionParam) openOr "") //  
    
       
    val difficultyParam = "difficulty"
    object difficulty extends RequestVar(S.param(difficultyParam).map ( 
        argument => argument.split(",").toList.map(
            aNumber => DifficultyEnum.numberStrToDifficulty(aNumber)).
            // filter empty boxes and open the remaining ones
            filter(_.isDefined  ).map (full => full.get) )
     openOr (Nil) )
    // DifficultyEnum.upToGeniusList
       
    val stateParam = "state"
    object state extends RequestVar(S.param(stateParam).map ( 
        argument => argument.split(",").toList.map(
            aNumber => StateEnum.numberStrToState(aNumber)).
            // filter empty boxes and open the remaining ones
            filter(_.isDefined ).map (full => full.get) )
     openOr (Nil) )
    //StateEnum.downToConcept
    
   val creatorParam = "creator"
    object creator extends RequestVar(S.param(creatorParam).map ( 
        argument => argument.split(",").toList.map(
            input => Helpers.tryo(input.toLong)).//Box.apply()).
            // filter empty boxes and open the remaining ones
            filter(_.isDefined ).map (full => full.openOrThrowException("Empty Box opened")) )
     openOr (Nil) )
    
    val tagParam = "tag"
    object tag extends RequestVar(S.param(tagParam).map ( 
        argument => argument.split(",").toList.map(
            input => Helpers.tryo(input.toLong)).//Box.apply()).
            // filter empty boxes and open the remaining ones
            filter(_.isDefined ).map (full => full.openOrThrowException("Empty Box opened")) )
     openOr (Nil) )  
 
    val ratingParam = "rating"
    object rating extends RequestVar(S.param(ratingParam).map ( 
        argument => argument.toDouble)
     openOr (0.0) )  
    
    val allLicenceString = "all"
    val commercialLicencesString = "com"
    val derivableLicencesString = "deriv"
    
    val licenceParam = "licence"
    object licence extends RequestVar(S.param(licenceParam).map(_ match {
          case commercial if commercial == commercialLicencesString  => LicenceEnum.commercialLicences
          case derivable if derivable == derivableLicencesString  => LicenceEnum.derivableLicences
          case _  => Nil 
    }) openOr(Nil))
        
    implicit def projectListToJSON(someProjects : List[Project]) : JValue = {
      JArray(someProjects.map(aProject => aProject.toJSON))	  	
  }
    
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(currentPageNumber*itemsPerPage), MaxRows(itemsPerPage.get))

  
  def addLikeCharFrontAndBack(queryParam:String) : String = "%"+queryParam.replaceAll("\\*", "%")+"%"

    
  def queryItems[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]](itemToQuery: BaseMetaEntityWithTitleDescriptionIconAndCommonFields[T] with BaseEntityWithTitleDescriptionIconAndCommonFields[T], otherQueryParams : List[QueryParam[T]] = List() ) = { // , otherQueryParams = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
    var query = 

        In(itemToQuery.primaryKeyField,itemToQuery.TheTranslationMeta.translatedItem,Like(itemToQuery.TheTranslationMeta.title,addLikeCharFrontAndBack(title.get))) ::
        In(itemToQuery.primaryKeyField,itemToQuery.TheTranslationMeta.translatedItem,Like(itemToQuery.TheTranslationMeta.description,addLikeCharFrontAndBack(description.get))) :: 
//        OrderBy.apply(itemToQuery.id,Ascending) ::
//        OrderBy.apply(itemToQuery.id,Descending) ::
        otherQueryParams 
        
        if (state.get != Nil){
          query = ByList(itemToQuery.state,state.get) :: query
        }
    	  if (difficulty.get != Nil)
    	     query = ByList(itemToQuery.difficulty,difficulty.get) :: query
      	if (licence.get != Nil)  	     
    	     query = ByList(itemToQuery.licence,licence.get) :: query
      	if (creator.get != Nil)  	     
    	     query = ByList(itemToQuery.createdByUser,creator.get) :: query
      	if (tag.get != Nil)  	     
    	     //query = ByList(itemToQuery.tags,tag.get) :: query    	
      	   query =  In(itemToQuery.primaryKeyField,itemToQuery.TheTags.taggedItem,ByList(itemToQuery.TheTags.theTag,tag.get)) :: query
      	if (rating.get > 0.0)  	     
      	   query =  By_>=(itemToQuery.accumulatedRatings,rating.get) :: query
   	
    itemToQuery.findAll(
        query :_*
        )
  }
  
               // define the page
  def numberOfItems = queryItems[Project](Project).length

  def listOfCurrentItems = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])

    
  /*
   * Version with implicit changes to XML or JSON
   * 
   */
  /*
  implicit def projectToRestResponse : JxCvtPF[Project] = {
  	case (JsonSelect, p, _) => p.toJSON
  	case (XmlSelect, p, _) => p.toXML
  }
  
  implicit def projectListToRestResponse : JxCvtPF[List[Project]] = {
  	case (JsonSelect, projectList, _) => JArray(projectList.map(aProject => aProject.toJSON))	
  	case (XmlSelect, projectList, _) => <projects>{Xml.toXml(JArray(projectList.map(aProject => aProject.toJSON)))}</projects>	
  }
  
  serveJx  {
    case Get(List("api", "projects","list"), req) => Full(Project.findAll)
    case Get(List("api", "projects","search"), req) => Full(listOfCurrentItems)

	//case Get(List("api", "projects","list", Project.MatchItemByID(aProject)), _) => Full(aProject)
  }
  
serveJx  {
  case Get(List("api", "projects", Project.MatchItemByID(aProject)), _) => Full(aProject)
}
  

*/
      
  
  serve {
  	//case "api" :: "projects" :: "list" :: Nil Get _ => anyToJValue(Project.findAll.map(aProject => aProject.toJSON))
  	
  	case "api" :: "projects" :: "list" :: Nil Get _ => Project.findAll : JValue

  	case "api" :: "projects" :: "list" :: Project.MatchItemByID(aProject) :: Nil Get _ =>  aProject : JValue 
  	
  	case "api" :: "projects" :: "search" :: Nil Get _ =>  listOfCurrentItems : JValue
  	

  	case "api" :: "projects" :: Nil Get _ =>  OkResponse()

  }

  
  
}