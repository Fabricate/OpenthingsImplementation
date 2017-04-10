package at.fabricate.openthings
package model

import net.liftweb.mapper._
import org.joda.time.convert.DateConverter
import net.liftweb.util.JodaHelpers
import java.util.Date


object Waaguser extends Waaguser with LongKeyedMetaMapper[Waaguser]  {
  
  
  override def dbTableName = "openthings_users_table"
  
}


class Waaguser extends LongKeyedMapper[Waaguser] with OneToMany[Long, Waaguser]
{
  def getSingleton = Waaguser
  
  object uid extends MappedLongIndex(this){    
    override def dbColumnName = "uid"
  }
  
  def primaryKeyField = uid  
  
  object name extends MappedTextarea(this, 60)
  
  object mail extends MappedTextarea(this, 64) 
  
  object picture extends MappedTextarea(this,255)
  
  object created extends MappedLong(this){
    def toDate: Date = {
      JodaHelpers.toDateTime(this.get*1000).openOrThrowException("Opened empty Box").toDate()
    }
  }
  
  object projects extends MappedOneToMany(Waagproject, Waagproject.user, OrderBy(Waagproject.nid, Ascending))
}