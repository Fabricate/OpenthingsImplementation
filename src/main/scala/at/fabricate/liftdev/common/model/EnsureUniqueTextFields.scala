package at.fabricate.liftdev.common
package model

import net.liftweb.util.FieldIdentifier
import net.liftweb.util.FieldError
import scala.xml.Text
import net.liftweb.mapper.By
import net.liftweb.common.Empty
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.Mapper

trait EnsureUniqueTextFields[T <: BaseEntity[T]] extends BaseEntity[T] {
  // EnsureUniqueTextFields
  self: T =>
    
  def theUniqueFields : List[MappedString[T]]
    
  def fieldIsUnique  = 
    theUniqueFields.map(theUniqueField => findSameAsUniqueField(theUniqueField) match {
    case Nil => List[FieldError]()
    case someThing :: someRest  => List(FieldError(theUniqueField, Text("Entry with same text field already exists!")))
  }
    )
  
  def findSameAsUniqueField(theUniqueField : MappedString[T]) : List[T] = getSingleton.findAll(By(theUniqueField,theUniqueField.get))
  
  override def save = 
    this.fieldIsUnique match {
	  			// create new object
              case Nil => super.save
	  			// update object
              case _ if (this.primaryKeyField != Empty ) => super.save
	  			// no new object with same name!
              case _ => {
                println("same object already exists - no saving performed!")
                false
              }
            }
}
