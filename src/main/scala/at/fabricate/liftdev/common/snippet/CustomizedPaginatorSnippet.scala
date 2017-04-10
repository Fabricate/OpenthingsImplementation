package at.fabricate.liftdev.common
package snippet

import net.liftweb.http.PaginatorSnippet
import net.liftweb.http.SHtml
import scala.xml.NodeSeq
import net.liftweb.http.SortedPaginator
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.util.CSSHelpers._
import net.liftweb.util.CssBind
import net.liftweb.mapper._
import net.liftweb.http.Paginator
import net.liftweb.http.SortedPaginatorSnippet
import scala.xml.Text


// WARNING: pagination links with ajax dont work unfortunately, would need some bugfixing
trait CustomizedPaginatorSnippet[T] extends PaginatorSnippet[T] {

  def renderIt(in: NodeSeq): NodeSeq
  def render(html: NodeSeq): NodeSeq = renderIt(html)
  
  
  	override def itemsPerPage = 12
  	override def prevXml: NodeSeq = Text("<")
	override def nextXml: NodeSeq = Text(">")
	override def firstXml: NodeSeq = Text("<<")
	override def lastXml: NodeSeq = Text(">>")
	override def currentXml: NodeSeq = Text("Displaying records "+(first+1)+"-"+(first+itemsPerPage min count)+" of "+count)

	//	Also if you pass your own parameters they will be eaten to avoid that override page url.
//	override def pageUrl(offset: Long): String = appendParams(super.pageUrl(offset), List("your param" -> "value"))

//  other name for the offset parameter:
//	override def offsetParam = "offset"
	    
  def paginatecss : CssSel = {
        "#first" #> pageXml(0, firstXml) &
        "#prev" #> pageXml(first-itemsPerPage max 0, prevXml) &
        "#allpages" #> {(n:NodeSeq) => pagesXml(0 until numPages)(n)} &
        "#zoomedpages" #> {(ns: NodeSeq) => pagesXml(zoomedPages)(ns)} &
        "#next" #> pageXml(first+itemsPerPage min itemsPerPage*(numPages-1) max 0, nextXml) &
        "#last" #> pageXml(itemsPerPage*(numPages-1), lastXml) &
        "#records" #> currentXml &
        "#recordsFrom" #> recordsFrom &
        "#recordsTo" #> recordsTo &
        "#recordsCount" #> count.toString
    }

}
