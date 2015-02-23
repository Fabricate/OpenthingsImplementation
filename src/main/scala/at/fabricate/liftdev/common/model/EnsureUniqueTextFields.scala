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
import net.liftweb.mapper.LongKeyedMapper

trait EnsureUniqueTextFields[T <: EnsureUniqueTextFields[T]] extends BaseEntity[T] {
  // EnsureUniqueTextFields
  self: T =>
    
  type TheUniqueTextType <: BaseEntity[TheUniqueTextType]
    
  
  def ensureFieldIsUnique(theUniqueField : MappedString[TheUniqueTextType])(value : String) : List[FieldError] = {
    println("checking unique for field "+theUniqueField.dbColumnName+" with value "+value)


    // then theUniqueField is a member of another table that is linked - maybe we want that behavior?
    theUniqueField.fieldOwner.getSingleton.findAll(By(theUniqueField,value)) match {
		    case Nil => List[FieldError]()
		    // update that object
		    case someThing :: Nil if (this.primaryKeyField != Empty && this.primaryKeyField.get == someThing.primaryKeyField.get) => List[FieldError]() 
		    case someThing :: someRest  => List(FieldError(theUniqueField, Text("Entry with same text field already exists!")))
		    }
  }
    
  // make sure that fields are always checked on save!
  abstract override def save = 
    self.validate match {
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
