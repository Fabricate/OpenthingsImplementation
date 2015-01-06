package at.fabricate.liftdev.common.model
package samples

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription


object SampleItem extends SampleItem with BaseMetaEntity[SampleItem] with BaseMetaEntityWithTitleAndDescription[SampleItem] with AddTagsMeta[SampleItem] {
  
}


class SampleItem extends BaseEntity[SampleItem] with BaseEntityWithTitleAndDescription[SampleItem] with AddTags[SampleItem] {
  def getSingleton = SampleItem
  
  // definitions for AddTag
  type Z = SampleTag
  def theTagObject = SampleTag

}