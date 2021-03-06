package at.fabricate.liftdev.common
package snippet

import net.liftweb.http.PaginatorSnippet
import net.liftweb.http.SHtml
import scala.xml.NodeSeq
import net.liftweb.http.SortedPaginator
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.BindHelpers._
import net.liftweb.mapper._
import net.liftweb.http.Paginator
import net.liftweb.http.SortedPaginatorSnippet
import scala.xml.Text


// WARNING: pagination links with ajax dont work unfortunately, would need some bugfixing
trait AjaxPaginatorSnippet[T] extends PaginatorSnippet[T] {
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

//Define your actual render of rows with renderIt, and use the
//render-method to do the actual bind.

//For completeness, this is how the rest look in my code now:

trait AjaxSortedPaginatorSnippet[T, C] extends SortedPaginator[T, C]
with AjaxPaginatorSnippet[T] {
  def sortPrefix = "sort"

  /**
   * The sort links binding
   */
  def sortColumns(ns: NodeSeq) = sortMemo(ns)

  override def rerender = sortMemo.setHtml() & super.rerender
  private lazy val sortMemo = SHtml.idMemoize(ignored => _sortColumns _)

  private def _sortColumns(xhtml: NodeSeq): NodeSeq = {
    val result = bind(sortPrefix, xhtml,
      headers.zipWithIndex.map {
        case ((binding, _), colIndex) =>
          FuncBindParam(binding, (ns: NodeSeq) => SHtml.a(() => { sort
= sortedBy(colIndex); rerender }, ns))
      }.toSeq: _*)
    result
  }
}

//and then the Mapper-implementations:

abstract class AjaxSortedMapperPaginatorSnippet[T <: Mapper[T]](meta:
MetaMapper[T],
  initialSort: net.liftweb.mapper.MappedField[_, T],
  _headers: (String, MappedField[_, T])*) extends
MySortedMapperPaginator[T](meta, initialSort, _headers: _*)
  with AjaxSortedPaginatorSnippet[T, MappedField[_, T]] {
}

abstract class AjaxMapperPaginatorSnippet[T <: Mapper[T]](meta:
MetaMapper[T]) extends MyMapperPaginator[T](meta)
  with AjaxPaginatorSnippet[T] {
}

/**
 * Instead of Lifts prebaked to have constantParams be a method for
further flexibility
 */
class MyMapperPaginator[T <: Mapper[T]](val meta: MetaMapper[T])
extends Paginator[T] {
  def constantParams: Seq[QueryParam[T]] = Nil
  def count = meta.count(constantParams: _*)
  def page = meta.findAll(constantParams ++
Seq[QueryParam[T]](MaxRows(itemsPerPage), StartAt(first)): _*)
}

class MySortedMapperPaginator[T <: Mapper[T]](meta: MetaMapper[T],
  initialSort: net.liftweb.mapper.MappedField[_, T],
  _headers: (String, MappedField[_, T])*)
  extends MyMapperPaginator[T](meta) with SortedPaginator[T,
MappedField[_, T]] {

  val headers = _headers.toList
  sort = (headers.indexWhere { case (_, `initialSort`) => true; case _
=> false }, true)

  override def page = meta.findAll(constantParams ++
Seq[QueryParam[T]](mapperSort, MaxRows(itemsPerPage), StartAt(first)):
_*)
  protected def mapperSort = sort match {
    case (fieldIndex, ascending) =>
      OrderBy(
        headers(fieldIndex) match { case (_, f) => f },
        if (ascending) Ascending else Descending)
  }
}

class SortedMapperPaginatorSnippet[T <: Mapper[T]](
  meta: MetaMapper[T],
  initialSort: net.liftweb.mapper.MappedField[_, T],
  headers: (String, MappedField[_, T])*) extends
MySortedMapperPaginator[T](meta, initialSort, headers: _*)
  with SortedPaginatorSnippet[T, MappedField[_, T]]

