package at.fabricate
package model

import net.liftweb.mapper._

 	
 	
	class Comment extends LongKeyedMapper[Comment] with IdPK{
	  
	  def getSingleton = Comment
	
	
	  object commentedItem extends MappedLongForeignKey(this, Project)
	  object title extends MappedString(this, 40)
	  object author extends MappedString(this, 40)
	  object comment extends MappedString(this, 140)
	}
	
	object Comment  extends Comment with LongKeyedMetaMapper[Comment]{
	  	  override def dbTableName =  "project_comments" // define the DB table name TheCommentType

	}


