package at.fabricate.openthings
package api

import net.liftweb.http.rest.RestHelper
import at.fabricate.openthings.model.Project
import net.liftweb.common.Full
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import net.liftweb.mapper.QueryParam
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionIconAndCommonFields
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

object ProjectREST extends RestHelper with ProjectSearch{  
  
  /*
   * Version with implicit changes to XML or JSON
  implicit def projectToRestResponse : JxCvtPF[Project] = {
  	case (JsonSelect, p, _) => p.toJSON
  	case (XmlSelect, p, _) => p.toXML
  }
  
  serveJx {
	case Get(List("api", "projects", Project(aProject)), _) => Full(aProject)
  }
  * 
  */
  
    implicit def projectListToJSON(someProjects : List[Project]) : JValue = {
      JArray(someProjects.map(aProject => aProject.toJSON))	  	
  }

  
  serve {
  	//case "api" :: "projects" :: "list" :: Nil Get _ => anyToJValue(Project.findAll.map(aProject => aProject.toJSON))
  	
  	case "api" :: "projects" :: "list" :: "all" :: Nil Get _ => Project.findAll : JValue

  	case "api" :: "projects" :: Project.MatchItemByID(aProject) :: Nil Get _ =>  aProject : JValue
  	
  	case "api" :: "projects" :: "search" :: Nil Get _ =>  page : JValue

  }
  
  // because pagination snippet defines it
  override def renderIt(in : NodeSeq) = in
  
  
}