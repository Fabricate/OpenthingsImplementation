package at.fabricate.openthings
package model

import net.liftweb.mapper._
import org.joda.time.convert.DateConverter
import net.liftweb.util.JodaHelpers
import java.util.Date


object Waagprojectdocument extends Waagprojectdocument with LongKeyedMetaMapper[Waagprojectdocument]  {
  
  
  override def dbTableName = "openthings_project_documentation_table"
  
}


class Waagprojectdocument extends LongKeyedMapper[Waagprojectdocument] with OneToMany[Long, Waagprojectdocument]
{
  def getSingleton = Waagprojectdocument
  
  object nid extends MappedLongIndex(this)   
  
  def primaryKeyField = nid  
  
  object vid extends MappedLongIndex(this)
    
  object user extends MappedLongForeignKey(this, Waaguser){    
    override def dbColumnName = "uid"
  }
  
  object project extends MappedLongForeignKey(this, Waagproject){    
    override def dbColumnName = "project_nid"
  }  
  
  object title extends MappedTextarea(this, 255)
  
  object versiontitle extends MappedTextarea(this, 255) 
  
  object body extends MappedText(this){    
    override def dbColumnName = "body"
  }
  
  object teaser extends MappedText(this)
  
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
  
   object images extends MappedOneToMany(Waagprojectimage, Waagprojectimage.projectdocument, OrderBy(Waagprojectimage.fid, Ascending))
   
   object files extends MappedOneToMany(Waagprojectfile, Waagprojectfile.projectdocument, OrderBy(Waagprojectfile.fid, Ascending))

}