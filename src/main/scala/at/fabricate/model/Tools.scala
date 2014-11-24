package at.fabricate
package model

import net.liftweb.mapper._
import net.liftweb.common.Empty
import net.liftweb.util.FieldIdentifier
import net.liftweb.util.FieldError
import scala.xml.Text

class Tool extends LongKeyedMapper[Tool] with ManyToMany{
  def getSingleton = Tool
  def primaryKeyField = id
  //override def dbIndexes = UniqueIndex(name)::super.dbIndexes
  object id extends MappedLongIndex(this)
  object name extends MappedString(this,100){
    
    /**Liste der durchzufuehrenden Validationen*/  
    override def validations = FieldValidation.notEmpty(this) _ :: this.fieldOwner.existsNot(this) _  :: Nil
  }
  object users extends MappedManyToMany(UserHasTools, UserHasTools.tool, UserHasTools.user, User)

  def existsNot(field: FieldIdentifier)(tool:String)  = 
    this.findByName(tool) match {
    case Nil => List[FieldError]()
    case _ => List(FieldError(field, Text("Tool existiert bereits")))

  }
  
  def findByName(tool:String) : List[Tool] = Tool.findAll(By(Tool.name,tool))
  
  override def save = 
    this.validate match {
              case Nil => super.save
              case _ => false
            }
  
}

object Tool extends Tool with LongKeyedMetaMapper[Tool] with CRUDify[Long, Tool]

object UserHasTools extends UserHasTools with LongKeyedMetaMapper[UserHasTools]

class UserHasTools extends LongKeyedMapper[UserHasTools] with IdPK {
  def getSingleton = UserHasTools
  object user extends MappedLongForeignKey(this, User)
  object tool extends MappedLongForeignKey(this, Tool)
}
