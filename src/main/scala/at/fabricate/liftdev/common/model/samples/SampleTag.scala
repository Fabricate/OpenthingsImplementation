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


object SampleTag extends SampleTag with BaseMetaEntity[SampleTag] with GeneralTagMeta[SampleTag] {
  
}


class SampleTag extends BaseEntity[SampleTag] with GeneralTag[SampleTag] 
with ManyToMany 
{
  def getSingleton = SampleTag

    // a link to all Tags
  val mappingToTags  = SampleItem.getTagMapper 
  
  	// ManyToMany example
  object tags extends MappedManyToMany(mappingToTags, mappingToTags.theTag, mappingToTags.taggedItem, SampleItem)   

}