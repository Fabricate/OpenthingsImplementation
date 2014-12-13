package at.fabricate 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import at.fabricate.lib._
import Helpers._
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import at.fabricate.model.User
import net.liftweb.mapper.By
import net.liftweb.http.SHtml
import net.liftweb.http.FileParamHolder
import net.liftmodules.imaging._
import net.liftmodules.textile.TextileParser
import net.liftmodules.widgets.autocomplete.AutoComplete
import at.fabricate.model.Tool
import scala.collection.mutable.Seq
import net.liftweb.http.js.JsCmd
import net.liftweb.http.PaginatorSnippet
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import scala.xml.Node
import net.liftweb.mapper.OrderBySql
import net.liftweb.mapper.IHaveValidatedThisSQL
import net.liftweb.mapper.QueryParam
import net.liftweb.mapper.Like
import net.liftweb.http.RequestVar
import at.fabricate.model.UserHasTools
import net.liftweb.mapper.In
import net.liftweb.mapper.PreCache
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.LongKeyedMapper

object ListDesigners extends PaginatorSnippet[User] with Logger {
  
  //object QueryParams
  
  private class TypeOfRequest
  private case class UserRequest() extends TypeOfRequest
  private case class ToolRequest() extends TypeOfRequest
  
  // TODO: differentiate AND and OR search
  //private class TypeOfSearch
  //private case class UserRequest() extends TypeOfRequest
  //private case class ToolRequest() extends TypeOfRequest
  
  
  private def listOfQueryParametersUser [T <: Mapper[T]] = List[QueryParam[T]](StartAt(curPage*itemsPerPage),
      MaxRows(itemsPerPage))
      
  private def orderByRand[T <: Mapper[T]] :  QueryParam[T] = OrderBySql("RAND()",IHaveValidatedThisSQL("Johannes Fischer","2014-11-26")) // T: ClassManifest, 

  
  // maybe we need a session var?? or something else?
  private object requestType extends RequestVar(new TypeOfRequest)
  
  private object queryParametersUser extends RequestVar(List[QueryParam[User]]())  
  //private object queryParametersTool extends RequestVar(List[QueryParam[Tool]]())
  
  private object listOfTools extends RequestVar(List(""))

  /*private object requestQueryParametersUserHasTools extends RequestVar(List[QueryParam[UserHasTools]]())

  private var userPage = User.findAll(
      requestQueryParametersUser:_*
      )
      
  private var userHasToolsPage = UserHasTools.findAll(
      requestQueryParametersUserHasTools:_*
      )
  
  private object requestPage extends RequestVar(userPage )

  //By(User.tools, tool)
  //Like(User.tools," tool") // Error - needs string
  
  // Idea: Interface, that User and UserHasTools implement, with a method that returns the type?
   * 
   */
  
  
  private def getUsersByTool(toolName : String ) : List[User] =  Tool.find(By(Tool.name, toolName)) match {
    case Full(tool) => tool.users.all
    // tool does not exist -> no according users
    // TODO: maybe a hint to the user!
    case _ => List[User]()
  }
  
  private def getUsersByAllTools() : List[User] = listOfTools.flatMap(toolName => getUsersByTool(toolName)).distinct //.removeDuplicates
  
  //listOfTools.map(toolName => getUsersByTool(toolName)) // now find out which element is contained in all lists!
  
  private def getSliceOfUsersByAllTools() : List[User] = getUsersByAllTools.slice(curPage*itemsPerPage,(curPage*itemsPerPage)+itemsPerPage) // or drop(curPage*itemsPerPage).take(itemsPerPage)
  
  
  
  override def count = requestType.get match {
    case UserRequest() => User.count
    case ToolRequest() => getUsersByAllTools.length
  }
  
  override def page = requestType.get match {
    case UserRequest() => User.findAll(
      queryParametersUser:_*
      )
      
    case ToolRequest() => getSliceOfUsersByAllTools
  }
    
  private def bindCSS(designer: User) = 
    "#name *" #>  "%s %s".format(designer.firstName.toString, designer.lastName.toString) &
    "img" #>  designer.icon.asHtml &
    "a [href]" #> "/designer/%d".format(designer.id.get)

  
  def renderPage (xhtml: NodeSeq) : NodeSeq = (S.param("type"),S.params("tool")) match {
      case (Full("random"), _) => //println("random ordering")
        requestType(UserRequest())
        queryParametersUser(orderByRand[User]::listOfQueryParametersUser[User])
        page.flatMap( designer =>
        bindCSS(designer)(xhtml)
        )
      case (_, tools:List[String]) if tools.length > 0 => //println("tools %s searching".format( tools.mkString(", ") )) //.mkString(", ")) // generates an error!
        requestType(ToolRequest())
        listOfTools(tools)
        //queryParametersUser(orderByRand[User]::listOfQueryParametersUser[User])
        page.flatMap( designer =>
        bindCSS(designer)(xhtml)
        )        
	  case _ => //println("normal ordering")
	    requestType(UserRequest())
	    queryParametersUser(listOfQueryParametersUser[User])
        page.flatMap( designer =>
        bindCSS(designer)(xhtml)
	    )
      /*
       * case (_,Full(tag)) => //println("random ordering")        
        requestPage(userHasToolsPage)
        requestQueryParameters(
        		In(User.tools,UserHasTools.user,Like(Tool.name,tag))::PreCache(User.tools)::listOfQueryParameters)
        page.flatMap( designer =>
        bindCSS(designer)(xhtml)
        )
        * 
        * 
  
  private val link = <a href="#"> content </a>
  
  private def surroundWithLink(designer: User, content: NodeSeq) = 
    "a [href]" #> "/designer/%d".format(designer.id.get) &
    "a *" #> content 
    
    
          
  private def addQueryParams(otherParams : List[QueryParam[_]]) = //: List[QueryParam[_]]
     StartAt(curPage*itemsPerPage) :: MaxRows(itemsPerPage) :: otherParams
     
  private var listOfQueryParametersUserHasTools : List[QueryParam[UserHasTools]] = List(StartAt(curPage*itemsPerPage),
      MaxRows(itemsPerPage))
    
        * 
        */
    }
  /*
   * 
   * working bind example
   * bind("dsigner", xhtml,
	               "firstname" -> surroundWithLink(designer,designer.firstName.asHtml)(link),
	               "lastname" -> surroundWithLink(designer,designer.lastName.asHtml)(link),
	               "image" -> surroundWithLink(designer,designer.image.asHtml)(link)
	               )
   * 
   * 
   *  		strToCssBindPromoter(
  		    
  		    )
        )
   * 	    bind("dsigner", xhtml,
	               "url" -> Text("href=\"/designer/"+designer.id.asString+"\""),
	               "firstname" -> designer.firstName.asHtml,
	               "lastname" -> designer.lastName.asHtml,
	               "image" -> designer.image.asHtml
	               )
   */
}

