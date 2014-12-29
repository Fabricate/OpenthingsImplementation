package at.fabricate
package snippet

import net.liftweb.common.Logger
import net.liftweb.http.DispatchSnippet
import model.User
import net.liftweb.http.S
import scala.xml.NodeSeq
import lib.MapperBinder
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.StartAt
import model.Project
import scala.xml.Text

object UserSnippet extends AjaxPaginatorSnippet[User] with DispatchSnippet with Logger {
  
   // define the page
  override def count = User.count

  override def page = User.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage), OrderBy(User.id, Descending))

  
  def dispatch : DispatchIt = {
//    case "list" => renderIt(_)
//    case "renderIt" => renderIt(_)
//    case "edit" => edit _
    case "view" => view(_)
    case "paginate" => paginate _
    case "paginatecss" => paginatecss(_)
  }
  
      def renderIt (in: scala.xml.NodeSeq) : scala.xml.NodeSeq =   
    // just a dummy implementation
   ("#item" #> {page.map(user => MapperBinder.bindMapper(user, {
     "#toitem [href]" #> "/designer/%s".format(user.id.toString) &      
     "#toitem *" #> "View Item"
   }) _)}).apply(in)
   
    private def view (xhtml: NodeSeq) :  NodeSeq  =  {

	S.param("id").get match {
	      case User.MatchItemByID(theUser) => {   
	        
	    	  println("user with id %d found".format(theUser.id.get))        
	    	  println("user title: %s".format(theUser.title.get))
	    	 ("#dbcontent *" #> {
     "#icon [src]" #> theUser.icon .url //&
//      ".left" #> { "#title" #> "%s %s".format(theUser.firstName , theUser.lastName ) }
	    	 }).apply(
	        ("#dbcontent *" #> { MapperBinder.bindMapper(theUser) _ }
     ).apply (xhtml))
	      }	      
      case _ => Text("Object not found!")
	}
      }
}