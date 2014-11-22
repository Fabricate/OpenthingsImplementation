package at.fabricate
package model

import net.liftweb.mapper._

class Tool extends LongKeyedMapper[Tool] with ManyToMany{
  def getSingleton = Tool
  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object name extends MappedString(this,100)
  object users extends MappedManyToMany(UserHasTools, UserHasTools.tool, UserHasTools.user, User)
}

object Tool extends Tool with LongKeyedMetaMapper[Tool]

object UserHasTools extends UserHasTools with LongKeyedMetaMapper[UserHasTools]

class UserHasTools extends LongKeyedMapper[UserHasTools] with IdPK {
  def getSingleton = UserHasTools
  object user extends MappedLongForeignKey(this, User)
  object tool extends MappedLongForeignKey(this, Tool)
}
