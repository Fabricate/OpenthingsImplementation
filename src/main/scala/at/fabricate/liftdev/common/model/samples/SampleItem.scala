package at.fabricate.liftdev.common.model
package samples

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.IdPK


object SampleItem extends SampleItem with BaseMetaEntity[SampleItem] with BaseMetaEntityWithTitleAndDescription[SampleItem] with AddTagsMeta[SampleItem] {
  
}


class SampleItem extends BaseEntity[SampleItem] with BaseEntityWithTitleAndDescription[SampleItem] with AddTags[SampleItem] with IdPK {
  def getSingleton = SampleItem
  
  // definitions for AddTag
  type theTagType = SampleTag
  def theTagObject = SampleTag

}