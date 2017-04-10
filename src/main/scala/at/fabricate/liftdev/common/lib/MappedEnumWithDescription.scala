package at.fabricate.liftdev.common
package lib

import net.liftweb.mapper.MappedEnum
import scala.xml.Elem
import net.liftweb.mapper.Mapper
import scala.reflect.runtime.universe._

 abstract class MappedEnumWithDescription[ElemTyp <: Elem, T <: Mapper[T]] (obj : T, theEnum: EnumWithDescriptionAndObject[ElemTyp], defaultString : String = "Not Found", defaultElem : ElemTyp = <span class="notFound">Not Found</span>.asInstanceOf[ElemTyp])(implicit tag: TypeTag[EnumWithDescriptionAndObject[ElemTyp]#Value]) 
 			extends MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]](obj, theEnum  )
 			// fix from https://medium.com/byte-code/overcoming-type-erasure-in-scala-8f2422070d20
{
    self: MappedEnum[T, EnumWithDescriptionAndObject[ElemTyp]] =>
      // just private is possible here, as theEnum is also private
        private type EnumsExtendedValue = theEnum.ExtendedValue
        //private var data: EnumWithDescriptionAndObject[ElemTyp]#Value = defaultValue
        //private var orgData: EnumWithDescriptionAndObject[ElemTyp]#Value = defaultValue
        //override def defaultValue = theEnum.Value(defaultString, defaultElem)        
        //override def dbFieldClass = classOf[Int]
//      Dont do that here, as this gets added to the content
//      private val default : ExtendedValue = theEnum.Value(defaultString, defaultElem)
      
	  private def findEnumValue(text : String ) : EnumsExtendedValue = theEnum.valueOf(text).getOrElse(theEnum.Value(defaultString, defaultElem)).asInstanceOf[EnumsExtendedValue]
    private def getWrapped = findEnumValue(get.toString).wrapped
    private def getDescription = findEnumValue(get.toString).description
    override def asHtml = getWrapped
    override def buildDisplayList: List[(Int, String)] = enum.values.toList.map(a => (a.id, findEnumValue(a.toString).description))
  }
