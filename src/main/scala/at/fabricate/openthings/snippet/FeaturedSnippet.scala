package at.fabricate.openthings.snippet

import java.util.Locale

import at.fabricate.liftdev.common.lib.UrlLocalizer
import at.fabricate.liftdev.common.model.{BaseEntityWithTitleAndDescription, BaseEntityWithTitleDescriptionIconAndCommonFields, BaseMetaEntityWithTitleDescriptionIconAndCommonFields, DifficultyEnum, LicenceEnum, StateEnum}
import at.fabricate.liftdev.common.snippet.{BaseEntityWithTitleAndDescriptionSnippet, BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet}
import at.fabricate.openthings.model.Project
import at.fabricate.openthings.snippet.SearchSnippet._
import net.liftweb.common.Full
import net.liftweb.http.{ParsePath, RequestVar, RewriteRequest, RewriteResponse, S}
import net.liftweb.mapper.{By, Descending, In, KeyedMapper, Like, MaxRows, OrderBy, QueryParam, StartAt}
import net.liftweb.sitemap.Loc._
import net.liftweb.sitemap.Menu
import net.liftweb.util.Helpers._

import scala.util.Sorting
import scala.xml.{NodeSeq, Text}

import net.liftweb.http.RequestVar
import scala.xml._
import net.liftweb.http._
import net.liftweb.util._
import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import Helpers._

object FeaturedSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] {

  override type ItemType = Project


      val contentLanguage = UrlLocalizer.contentLocale

    //object elements extends RequestVar(S.param("elements") openOr "") //

   
    override val TheItem = Project
  override def itemBaseUrl = "project"

      def theUserSnippet = UserSnippet

      // generate the url rewrites
  /*override def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] =  {
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath)), _, _, _), _, _) =>
	      RewriteResponse(searchTemplate :: Nil)
      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath), "index"), _, _, _), _, _) =>
	      RewriteResponse(searchTemplate :: Nil)

     }*/

// remove this features
  override def create(xhtml: NodeSeq) : NodeSeq  = notAvailable
  
  override def edit(xhtml: NodeSeq) : NodeSeq  =  notAvailable
  
  override def view(xhtml : NodeSeq) : NodeSeq  =  notAvailable

  def feature(xhtml : NodeSeq) : NodeSeq  = {

    val elements = 34 :: 35 :: 56 :: Nil
    //println("Featured.test: "+elements.mkString(", "))

    //val query = elements.map(By(Project.id,_))

    //val result = Project.findAll(query :_*)

    val result = elements.map(TheItem.findByKey(_)).filter(_.isDefined).map(_.openOrThrowException("Empty Box opened"))

    //println("Featured.test: "+result.map(_.createdAt.toString()).mkString(", "))

    ("#list_items" #> ("#item" #> result.map(item => asHtml(item) ))).apply(xhtml)

  }

  //return the top three rated projects
  def rated(xhtml: NodeSeq) : NodeSeq =
  {
      //get best three rated projects
      val sorted : List[Project] = TheItem.findAll(
        OrderBy(Project.accumulatedRatings,Descending)
      )

      val sliced : List[Project] = sorted.slice(0,3)
      //println("Featured.rated: " + sorted.mkString(", "))

      ("#list_items" #> ("#item" #> sliced.map(item => asHtml(item) ))).apply(xhtml)
  }

   override def localDispatch : DispatchIt  = {
    case "feature" => feature _
    case "rated" => rated _
  }
  /*
  def dispatch : DispatchIt  = {
    case "feature" => feature _
  }

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
  */

  
  def getPaginationLimit[T <: BaseEntityWithTitleAndDescription[T]] : List[QueryParam[T]] = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))
  
  def queryItems[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]](itemToQuery: BaseMetaEntityWithTitleDescriptionIconAndCommonFields[T] with BaseEntityWithTitleDescriptionIconAndCommonFields[T], otherQueryParams : List[QueryParam[T]] = List() ) = { // , otherQueryParams = List(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))

    //val elements = (S.param("elements") openOr "").split(',').map(input => input match {case  AsInt(number) => number;case _ => -1}).filter( _ > 0)
    val elements = 3 :: 5 :: 8 :: Nil
    println(elements.mkString(", "))

    val query = elements.map(By(itemToQuery.id,_))

    	
    itemToQuery.findAll(
        query :_*
        )
  }
  
       // define the page
  override def count = queryItems[Project](Project).length

  override def page = queryItems[Project](Project,OrderBy(Project.primaryKeyField, Descending)::getPaginationLimit[Project])


}
