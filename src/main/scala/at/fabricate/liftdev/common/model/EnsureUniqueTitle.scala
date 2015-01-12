package at.fabricate.liftdev.common
package model

import net.liftweb.util.FieldIdentifier
import net.liftweb.util.FieldError
import scala.xml.Text
import net.liftweb.mapper.By
import net.liftweb.common.Empty

trait EnsureUniqueTitle[T <: BaseEntityWithTitleAndDescription[T]] extends BaseEntity[T] {
  self: T =>
    
  def existsNot(field: FieldIdentifier)(tool:String)  = 
    findByTitle(tool) match {
    case Nil => List[FieldError]()
    case someThing :: someRest  => List(FieldError(field, Text("Entry with same title already exists!")))

  }
  
  def findByTitle(title : String) : List[T] = getSingleton.findAll(By(getSingleton.title,title))
  
  override def save = 
    this.validate match {
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