package at.fabricate.liftdev.common
package lib

import net.liftweb.mapper.MappedEnum
import scala.xml.Elem
import net.liftweb.mapper.Mapper

 abstract class MappedEnumWithDescription[ElemTyp <: Elem, T <: Mapper[T]] (obj : T, theEnum: EnumWithDescriptionAndObject[ElemTyp], defaultString : String = "Not Found", defaultElem : ElemTyp = <span class="notFound">Not Found</span>.asInstanceOf[ElemTyp]) 
 			extends MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]](obj, theEnum  ){
    self: MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]] =>
      // just private is possible here, as theEnum is also private
      private type ExtendedValue = theEnum.ExtendedValue
//      Dont do that here, as this gets added to the content
//      private val default : ExtendedValue = theEnum.Value(defaultString, defaultElem)
      
	private def findEnumValue(text : String ) : ExtendedValue = theEnum.valueOf(text).getOrElse(theEnum.Value(defaultString, defaultElem)).asInstanceOf[ExtendedValue]
    private def getWrapped = findEnumValue(get.toString).wrapped
    private def getDescription = findEnumValue(get.toString).description
    override def asHtml = getWrapped
    override def buildDisplayList: List[(Int, String)] = enum.values.toList.map(a => (a.id, findEnumValue(a.toString).description))
  }
