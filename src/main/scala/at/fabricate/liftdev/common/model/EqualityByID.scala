package at.fabricate.liftdev.common.model

import net.liftweb.mapper.KeyedMapper

trait EqualityByID[T <: KeyedMapper[_,_]] {
	self: T => 
	  type TheEqualityType = T
    // necessary to remove duplicate elements from lists
  
  override def equals(other:Any) = other match {
    case u:TheEqualityType if u.primaryKeyField.get == this.primaryKeyField.get => true
    case _ => false
  }
  
  override def hashCode = this.primaryKeyField.get.hashCode
}