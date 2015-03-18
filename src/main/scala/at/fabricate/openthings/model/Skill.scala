package at.fabricate.openthings
package model

import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleAndDescription
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleAndDescription
import net.liftweb.mapper.ManyToMany
import at.fabricate.liftdev.common.model.GeneralSkillMeta
import at.fabricate.liftdev.common.model.GeneralSkill


object Skill extends Skill with BaseMetaEntity[Skill] with GeneralSkillMeta[Skill] {
  
}


class Skill extends BaseEntity[Skill] with GeneralSkill[Skill]
with ManyToMany 
{
  def getSingleton = Skill

    // a link to all Skills
  val mappingToProjectSkills  = Project.getSkillMapper
  
  	// ManyToMany mapping
  object projectSkills extends MappedManyToMany(mappingToProjectSkills, mappingToProjectSkills.theSkill, mappingToProjectSkills.taggedItem, Project)

}