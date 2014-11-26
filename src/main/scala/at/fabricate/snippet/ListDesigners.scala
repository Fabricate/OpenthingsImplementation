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

object ListDesigners extends PaginatorSnippet[User] with Logger {
  
  override def count = User.count
  
  override def page = User.findAll(StartAt(curPage*itemsPerPage),MaxRows(itemsPerPage))
  
  private val link = <a href="#"> content </a>
  
  private def surroundWithLink(designer: User, content: NodeSeq) = 
    "a [href]" #> "/designer/%d".format(designer.id.get) &
    "a *" #> content 
    
  private def bindCSS(designer: User) = 
    "#name *" #>  "%s %s".format(designer.firstName.toString, designer.lastName.toString) &
    "img" #>  designer.userImage.asHtml &
    "a [href]" #> "/designer/%d".format(designer.id.get)

  
  def renderPage (xhtml: NodeSeq) : NodeSeq = page.flatMap( designer =>
  			bindCSS(designer)(xhtml)
	             )
  /*
   * 
   * working bind example
   * bind("dsigner", xhtml,
	               "firstname" -> surroundWithLink(designer,designer.firstName.asHtml)(link),
	               "lastname" -> surroundWithLink(designer,designer.lastName.asHtml)(link),
	               "image" -> surroundWithLink(designer,designer.userImage.asHtml)(link)
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
	               "image" -> designer.userImage.asHtml
	               )
   */
}

