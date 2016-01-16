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

object ProjectREST extends RestHelper with ProjectSearch{  
  
    val defaultItemsPerPage = 9

    val itemsPerPageParam = "nr_of_items"
    object itemsPerPage extends RequestVar(S.param(itemsPerPageParam).map(value => value.toInt
    ) openOr(defaultItemsPerPage)) 
    
    val defaultPage = 0

    val currentPageParam = "current_page"
    object currentPageNumber extends RequestVar(S.param(currentPageParam).map(value => value.toInt
    ) openOr(defaultPage)) 
    
        
    implicit def projectListToJSON(someProjects : List[Project]) : JValue = {
      JArray(someProjects.map(aProject => aProject.toJSON))	  	
  }
    
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(currentPageNumber*itemsPerPage), MaxRows(itemsPerPage.get))

    
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