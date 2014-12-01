package at.fabricate
package model

import net.liftweb.mapper._
import net.liftweb.common.Empty
import net.liftweb.util.FieldIdentifier
import net.liftweb.util.FieldError
import scala.xml.Text
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.util.Helpers

class Tool extends LongKeyedMapper[Tool] with IdPK with ManyToMany{
  def getSingleton = Tool
  //def primaryKeyField = id
  //override def dbIndexes = UniqueIndex(name)::super.dbIndexes
  //object id extends MappedLongIndex(this)
  object name extends MappedString(this,100){
    
    /**Liste der durchzufuehrenden Validationen*/  
    override def validations = FieldValidation.notEmpty(this) _ :: this.fieldOwner.existsNot(this) _  :: Nil
  }
  object users extends MappedManyToMany(UserHasTools, UserHasTools.tool, UserHasTools.user, User)

  def existsNot(field: FieldIdentifier)(tool:String)  = 
    this.findByName(tool) match {
    case Nil => List[FieldError]()
    case someThing :: someRest  => List(FieldError(field, Text("Tool existiert bereits")))

  }
  
  def findByName(tool:String) : List[Tool] = Tool.findAll(By(Tool.name,tool))
  
  override def save = 
    this.validate match {
	  			// create new object
              case Nil => super.save
	  			// update object
              case _ if (this.id != Empty ) => super.save
	  			// no new object with same name!
              case _ => {
                println("same object already exists - no saving performed!")
                false
              }
            }
  
  def unapply(id: String) : Option[Tool] = Helpers.tryo {
    Tool.find(By(Tool.id, id.toLong)).toOption
  } openOr None
  
  implicit def toJson(tool: Tool) : JValue =
    ("Tool" -> 
        ("name" -> tool.name.get) ~ 
        ("id" -> tool.id.get) 
        )
  implicit def toJson(tools: List[Tool]) : JValue = 
    ("Tools" ->
    	tools.map(tool => toJson(tool))
    	)
}

object Tool extends Tool with LongKeyedMetaMapper[Tool] with IdPK with CRUDify[Long, Tool]

object UserHasTools extends UserHasTools with LongKeyedMetaMapper[UserHasTools]

class UserHasTools extends LongKeyedMapper[UserHasTools] with IdPK {
  def getSingleton = UserHasTools
  object user extends MappedLongForeignKey(this, User)
  object tool extends MappedLongForeignKey(this, Tool)
}
