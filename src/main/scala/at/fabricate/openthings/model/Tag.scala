package at.fabricate.openthings
package model

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.ManyToMany
import at.fabricate.liftdev.common.model.TagsMeta
import at.fabricate.liftdev.common.model.Tags


object Tag extends Tag with BaseMetaEntity[Tag] with TagsMeta[Tag] {
  
}


class Tag extends BaseEntity[Tag] with Tags[Tag] 
with ManyToMany 
//with OneToMany[Long,SampleTag] 
{
  def getSingleton = Tag

    // a link to all Tags
  val mappingToProjectTags  = Project.getTagMapper // : LongKeyedMetaMapper[Samp.TheMapping]
  
  	// ManyToMany example
  object projectTags extends MappedManyToMany(mappingToProjectTags, mappingToProjectTags.theTag, mappingToProjectTags.taggedItem, Project)   

//  // OneToMany example -> does not work
//  object tags extends MappedOneToMany(mappingToTags, mappingToTags.theTag, OrderBy(mappingToTags.primaryKeyField, Ascending))   
//
//  //  with Owned[mappingToTags] with Cascade[mappingToTags]
////  MappedOneToMany(TheTags, TheTags.taggedItem, OrderBy(TheTags.primaryKeyField, Ascending))  with Owned[TheTags]
////with Cascade[TheTags]
}