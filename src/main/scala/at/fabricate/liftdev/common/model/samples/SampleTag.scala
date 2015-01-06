package at.fabricate.liftdev.common.model
package samples

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.ManyToMany
import net.liftweb.mapper.OneToMany


object SampleTag extends SampleTag with BaseMetaEntity[SampleTag] with TagsMeta[SampleTag] {
  
}


class SampleTag extends BaseEntity[SampleTag] with Tags[SampleTag] 
with ManyToMany 
//with OneToMany[Long,SampleTag] 
{
  def getSingleton = SampleTag

    // a link to all Tags
  val mappingToTags  = SampleItem.getTagMapper // : LongKeyedMetaMapper[Samp.TheMapping]
  
  	// ManyToMany example
  object tags extends MappedManyToMany(mappingToTags, mappingToTags.theTag, mappingToTags.taggedItem, SampleItem)   

//  // OneToMany example -> does not work
//  object tags extends MappedOneToMany(mappingToTags, mappingToTags.theTag, OrderBy(mappingToTags.primaryKeyField, Ascending))   
//
//  //  with Owned[mappingToTags] with Cascade[mappingToTags]
////  MappedOneToMany(TheTags, TheTags.taggedItem, OrderBy(TheTags.primaryKeyField, Ascending))  with Owned[TheTags]
////with Cascade[TheTags]
}