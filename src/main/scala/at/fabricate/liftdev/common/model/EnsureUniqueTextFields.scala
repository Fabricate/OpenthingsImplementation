package at.fabricate.liftdev.common
package model

import net.liftweb.util.FieldIdentifier
import net.liftweb.util.FieldError
import scala.xml.Text
import net.liftweb.mapper.By
import net.liftweb.common.Empty
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.KeyedMapper

trait EnsureUniqueTextFields[T <: KeyedMapper[_,T]] extends Mapper[T] {
  // EnsureUniqueTextFields
  self: T =>
    
  def theUniqueFields : List[MappedString[T]]
    
  def fieldsAreUnique : List[FieldError] = 
    theUniqueFields.flatMap(theUniqueField => findSameAsUniqueField(theUniqueField) match {
    case Nil => List[FieldError]()
    // update that object
    case someThing :: Nil if (this.primaryKeyField != Empty && this.primaryKeyField.get == someThing.primaryKeyField.get) => List[FieldError]() 
    case someThing :: someRest  => List(FieldError(theUniqueField, Text("Entry with same text field already exists!")))
  }
    )
  
  def findSameAsUniqueField(theUniqueField : MappedString[T]) : List[T] = {
    println("checking unique for field "+theUniqueField.dbColumnName+" with value "+theUniqueField.get)
    println("got nrofresults: "+getSingleton.findAll(By(theUniqueField,theUniqueField.get)).length)
    getSingleton.findAll(By(theUniqueField,theUniqueField.get))
    // then theUniqueField is a member of another table that is linked - maybe we want that behavior?
    //theUniqueField.fieldOwner.getSingleton.findAll(By(theUniqueField,theUniqueField.get))
  }
  
  override def validate = this.fieldsAreUnique ::: super.validate
  
  // make sure that fields are always checked on save!
  override def save = 
    this.validate match {
	  			// create new object
              case Nil => super.save
	  			// update object
              //case _ if (this.primaryKeyField != Empty ) => super.save
	  			// no new object with same name!
              case _ => {
                println("same object already exists - no saving performed!")
                false
              }
            }
}
