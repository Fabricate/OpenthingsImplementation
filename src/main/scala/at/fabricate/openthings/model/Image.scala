package at.fabricate.openthings
package model

import net.liftweb.mapper._
import at.fabricate.liftdev.common.model._
import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleAndDescriptionSnippet

object Image extends Image with BaseMetaEntity[Image] with BaseMetaEntityWithTitleAndDescription[Image] with GeneralImageMeta[Image] 
{
  
}


class Image extends GeneralImage[Image] with BaseEntity[Image] with BaseEntityWithTitleAndDescription[Image]
with ManyToMany 
//with KeyedMetaMapper[Image] 
{
  def getSingleton = Image

    // a link to all Images
  val mappingToProjectImages  = Project.getImageMapper
  
  	// ManyToMany mapping
  object projectImages extends MappedManyToMany(mappingToProjectImages, mappingToProjectImages.image, mappingToProjectImages.taggedItem, Project)   

}
