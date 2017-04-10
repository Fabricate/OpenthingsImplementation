package at.fabricate.openthings
package model

import net.liftweb.mapper._
import org.joda.time.convert.DateConverter
import net.liftweb.util.JodaHelpers
import java.util.Date


object Waagproject extends Waagproject with LongKeyedMetaMapper[Waagproject]  {
  
  
  override def dbTableName = "openthings_projects_table"
  
}


class Waagproject extends LongKeyedMapper[Waagproject] with OneToMany[Long, Waagproject]
{
  def getSingleton = Waagproject
  
  object nid extends MappedLongIndex(this)   
  
  def primaryKeyField = nid  
  
  object vid extends MappedLongIndex(this)
  
  /*
  object uid extends MappedLongIndex(this){    
    override def dbColumnName = "uid"
  }
  * 
  */
    
  object user extends MappedLongForeignKey(this, Waaguser){    
    override def dbColumnName = "uid"
  }
  
  object title extends MappedTextarea(this, 255)
  
  object versiontitle extends MappedTextarea(this, 255) 
  
  object body extends MappedText(this){    
    override def dbColumnName = "body"
  }
  
  object teaser extends MappedText(this)
  
  object license extends MappedTextarea(this,12)
  
  object format extends MappedInt(this)
  
  object created extends MappedLong(this){
    def toDate: Date = {
      JodaHelpers.toDateTime(this.get*1000).openOrThrowException("Opened empty Box").toDate()
    }
  }
  
  object changed extends MappedDateTime(this){
    override def get = super.get
  }
  
  object timestamp extends MappedDateTime(this){
    override def dbColumnName = "timestamp"
  }
  
   object images extends MappedOneToMany(Waagprojectimage, Waagprojectimage.project, OrderBy(Waagprojectimage.fid, Ascending))
   
   object documents extends MappedOneToMany(Waagprojectdocument, Waagprojectdocument.project, OrderBy(Waagprojectdocument.nid, Ascending))
   
   object files extends MappedOneToMany(Waagprojectfile, Waagprojectfile.project, OrderBy(Waagprojectfile.fid, Ascending))
   
}