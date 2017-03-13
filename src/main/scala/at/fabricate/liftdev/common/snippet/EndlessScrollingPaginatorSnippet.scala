/*package at.fabricate.liftdev.common
package snippet

import net.liftweb.http.PaginatorSnippet
import net.liftweb.http.SHtml
import scala.xml.NodeSeq
import net.liftweb.http.SortedPaginator
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.mapper._
import net.liftweb.http.Paginator
import net.liftweb.http.SortedPaginatorSnippet
import scala.xml.Text


trait EndlessScrollingPaginatorSnippet[T] extends PaginatorSnippet[T] {
  private lazy val pagMemo = SHtml.idMemoize(ignored => super.paginate _)

  /**
   * The pagination binding
   */

  override def paginate(ns: NodeSeq): NodeSeq = pagMemo(ns)

  def rerender = memo.setHtml() & pagMemo.setHtml()

  override def pageXml(newFirst: Long, ns: NodeSeq): NodeSeq =
    if (first == newFirst || newFirst < 0 || newFirst >= count)
      ns
    else
      SHtml.a(() => { _first = newFirst; rerender }, ns)

  lazy val memo = SHtml.idMemoize(ignored => renderIt _)
  def renderIt(in: NodeSeq): NodeSeq
  def render(html: NodeSeq): NodeSeq = memo(html)

  /**
   * Overrides the super's implementation so the first record can be overridden by a URL query parameter.
   */
  override var first = 0L
  
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
        "#allpages" #> {(n:NodeSeq) => this.pagesXml(0 until numPages,n)} &
        "#zoomedpages" #> {(ns: NodeSeq) => this.pagesXml(zoomedPages,ns)} &
        "#next" #> pageXml(first+itemsPerPage min itemsPerPage*(numPages-1) max 0, nextXml) &
        "#last" #> pageXml(itemsPerPage*(numPages-1), lastXml) &
        "#records" #> currentXml &
        "#recordsFrom" #> recordsFrom &
        "#recordsTo" #> recordsTo &
        "#recordsCount" #> count.toString
    }

}

// maybe copy the other stuff from AjaxPaginator once!
*/