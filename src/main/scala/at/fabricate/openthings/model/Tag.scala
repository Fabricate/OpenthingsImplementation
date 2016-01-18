package at.fabricate.openthings
package model

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.ManyToMany
import at.fabricate.liftdev.common.model.GeneralTagMeta
import at.fabricate.liftdev.common.model.GeneralTag


object Tag extends Tag with BaseMetaEntity[Tag] with GeneralTagMeta[Tag] {
  
}


class Tag extends BaseEntity[Tag] with GeneralTag[Tag] 
with ManyToMany 
{
  
  
    override val titleMinLength = 3

    def getSingleton = Tag

    // a link to all Tags
  val mappingToProjectTags  = Project.getTagMapper
  
  	// ManyToMany mapping
  object projectTags extends MappedManyToMany(mappingToProjectTags, mappingToProjectTags.theTag, mappingToProjectTags.taggedItem, Project)   

}