package at.fabricate.liftdev.common

package snippet

import net.liftweb._
import http._
import common._
import scala.xml._

/*
 * author: Denis Nevmerzhitsky
 */

object L10n extends DispatchSnippet {
  def dispatch: DispatchIt = {
    case "render" => ns => replace(ns)
    case "i"      => ns => inject(ns)
    case str      => ns => attr(ns, str)
  }

  def inject(ns: NodeSeq): NodeSeq = ns match {
    case Elem(prefix, label, attribs, scope , child @ _*) =>
      Elem(prefix, label, attribs, scope, S.loc(ns.text, Text(ns.text)): _*)
    case Seq(Elem(prefix, label, attribs, scope , child @ _*)) =>
      Elem(prefix, label, attribs, scope, S.loc(ns.text, Text(ns.text)): _*)
    case _ => ns
  }

  def replace(ns: NodeSeq): NodeSeq = S.loc(ns.text, Text(ns.text))

  def attr(ns: NodeSeq, attrName: String) = ns match {
    case Elem(prefix, label, attribs, scope, child @ _*) =>
      Elem(prefix, label, locAttrib(attribs, attrName), scope, child: _*)      
    case Seq(Elem(prefix, label, attribs, scope, child @ _*)) =>
      Elem(prefix, label, locAttrib(attribs, attrName), scope, child: _*)
    case _ => ns
  }

  private def locAttrib(attribs: MetaData, attrName: String) = attribs.get(attrName) match {
    case Some(Text(str)) =>
      attribs.append(new UnprefixedAttribute(attrName, S.loc(str, Text(str)), Null))
    case _ => attribs
  }
  
  /*
   * 
   Usage examples are:
      <p class="lift:L10n.i">res.name</p> => <p>Localized</p>
      <p><span class="lift:L10n">res.name</span></p> => <p>Localaized</p>
      <a href="#" title="res.name" class="lift:L10n.title">Title</a> => <a href="#" title="Localized">Title</a>
      <input class="lift:L10n.placeholder" placeholder="res.name" /> => <input placeholder="Localized" />
Only one fail for me, i expected:
      <a href="#" title="res.name" class="lift:L10n.title lift:L10n.i">res.name</a> =>  <a href="#" title="Localized">Localized</a>
but as i understand, only one snippet can be invoked.
   */
  
}