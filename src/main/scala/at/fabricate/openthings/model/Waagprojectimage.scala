package at.fabricate.openthings
package model

import net.liftweb.mapper._
import org.joda.time.convert.DateConverter
import net.liftweb.util.JodaHelpers
import java.util.Date


object Waagprojectimage extends Waagprojectimage with LongKeyedMetaMapper[Waagprojectimage]  {
  
  
  override def dbTableName = "openthings_project_images_table"
  
}


class Waagprojectimage extends LongKeyedMapper[Waagprojectimage] 
{
  def getSingleton = Waagprojectimage
  
  object fid extends MappedLongIndex(this)   
  
  def primaryKeyField = fid  
  
  object vid extends MappedLongIndex(this)
    
  object project extends MappedLongForeignKey(this, Waagproject){    
    override def dbColumnName = "nid"
  }
  
  object projectdocument extends MappedLongForeignKey(this, Waagprojectdocument){    
    override def dbColumnName = "nid"
  } 
  
  object user extends MappedLongForeignKey(this, Waaguser){    
    override def dbColumnName = "uid"
  }
  
  object filename extends MappedTextarea(this, 255)
  
  object filepath extends MappedTextarea(this, 255) 
  
  object filemime extends MappedTextarea(this, 255) 
  
  object filesize extends MappedLong(this)
  
  object status extends MappedLong(this)
  
  object timestamp extends MappedDateTime(this){
    override def dbColumnName = "timestamp"
  }
}