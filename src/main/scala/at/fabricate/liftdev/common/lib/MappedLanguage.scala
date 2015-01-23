package at.fabricate.liftdev.common
package lib

import net.liftweb.mapper._
import java.util.Locale
import net.liftweb.common.Box
import scala.xml.Elem
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb._
import net.liftweb.http._

abstract class MappedLanguage[T <: Mapper[T]](owner: T) extends MappedString[T](owner,16) {
  override def defaultValue = Locale.getDefault.toString
  
  def availableLanguages : List[Locale] = Locale.getAvailableLocales.toList.filter(locale => Locale.getISOLanguages.contains(locale.toString))

  def isAsLocale: Locale = availableLanguages.filter(_.toString == get) match {
    case Nil => Locale.getDefault
    case x :: xs => x
  }

  override def _toForm: Box[Elem] =
  Full(SHtml.select(availableLanguages.
                    sortWith(_.getDisplayName < _.getDisplayName).
                    map(lo => (lo.toString, lo.getDisplayName)),
                    Full(this.get), set) % ("id" -> fieldId))
}