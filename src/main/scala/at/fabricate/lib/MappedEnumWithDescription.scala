package at.fabricate
package lib

import net.liftweb.mapper.MappedEnum
import scala.xml.Elem
import net.liftweb.mapper.BaseMapper

 abstract class MappedEnumWithDescription[ElemTyp <: Elem, T <: BaseMapper] (obj : T, theEnum: EnumWithDescriptionAndObject[ElemTyp], defaultString : String = "Not Found", defaultElem : ElemTyp = <span class="notFound">Not Found</span>.asInstanceOf[ElemTyp]) 
 			extends MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]](obj, theEnum  ){
    self: MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]] =>
      // just private is possible here, as theEnum is also private
      private type ExtendedValue = theEnum.ExtendedValue
      private val default : ExtendedValue = theEnum.Value(defaultString, defaultElem)
      
	private def findEnumValue(text : String ) : ExtendedValue = theEnum.valueOf(text).getOrElse(default).asInstanceOf[ExtendedValue]
    private def getWrapped = findEnumValue(get.toString).wrapped
    private def getDescription = findEnumValue(get.toString).description
    override def asHtml = getWrapped
    override def buildDisplayList: List[(Int, String)] = enum.values.toList.map(a => (a.id, findEnumValue(a.toString).description))
  }