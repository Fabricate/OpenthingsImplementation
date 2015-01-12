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
import at.fabricate.liftdev.common.model.GeneralCategoryMeta
import at.fabricate.liftdev.common.model.GeneralCategory


object Category extends Category with BaseMetaEntity[Category] with GeneralCategoryMeta[Category] {
  
}


class Category extends BaseEntity[Category] with GeneralCategory[Category] 
with ManyToMany 
//with OneToMany[Long,SampleTag] 
{
  def getSingleton = Category

    // a link to all Tags
  val mappingToProjectCategories  = Project.getCategoryMapper // : LongKeyedMetaMapper[Samp.TheMapping]
  
  	// ManyToMany example
  object projectCategories extends MappedManyToMany(mappingToProjectCategories, mappingToProjectCategories.theCategory, mappingToProjectCategories.categorizedItem, Project)   

//  // OneToMany example -> does not work
//  object tags extends MappedOneToMany(mappingToTags, mappingToTags.theTag, OrderBy(mappingToTags.primaryKeyField, Ascending))   
//
//  //  with Owned[mappingToTags] with Cascade[mappingToTags]
////  MappedOneToMany(TheTags, TheTags.taggedItem, OrderBy(TheTags.primaryKeyField, Ascending))  with Owned[TheTags]
////with Cascade[TheTags]
}