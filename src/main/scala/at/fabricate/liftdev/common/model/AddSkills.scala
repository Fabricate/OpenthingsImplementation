package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.OneToMany
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.Schemifier
import net.liftweb.mapper.MappedLong
import net.liftweb.mapper.MetaMapper
import net.liftweb.mapper.BaseLongKeyedMapper
import net.liftweb.mapper.MappedForeignKey
import net.liftweb.mapper.BaseMetaMapper
import net.liftweb.mapper.ProtoUser
import net.liftweb.common.Box
import net.liftweb.mapper.MappedInt
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.MetaMegaProtoUser
import net.liftweb.common.Empty
import java.util.Locale

trait AddSkills[T <: (AddSkills[T])] extends BaseEntity[T]  with OneToMany[Long, T] { //
	self: T =>
	  	  
	  type TheSkillType <: GeneralSkill[TheSkillType]
	  
	  type TheSkillTranslation = TheGenericTranslation
	  
	  def theSkillObject : GeneralSkillMeta[TheSkillType]
	        
      def getSkillMapper : LongKeyedMetaMapper[TheSkills] = TheSkills
      
      
	def getSingleton : AddSkillsMeta[T]
	  


	        
	  object skills extends MappedOneToMany(TheSkills, TheSkills.taggedItem, OrderBy(TheSkills.primaryKeyField, Ascending))  with Owned[TheSkills]
with Cascade[TheSkills]

      
      class TheSkills extends LongKeyedMapper[TheSkills] with IdPK {
    	  def getSingleton = TheSkills
	    	  
	      object taggedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  object theSkill extends MappedLongForeignKey(this,theSkillObject)
		  
	}
	
	object TheSkills  extends TheSkills with LongKeyedMetaMapper[TheSkills]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_skill"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheSkills
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

      // some helpers for tags
	  def getAllAvailableSkills : List[TheSkillType] = theSkillObject.findAll
	  
	  def getAllSkillsForThisItem : List[TheSkillType] = this.skills.map(
	      // load the connected theTag object
	      _.theSkill.obj).filter(
	          // filter out all empty boxes
	          _ != Empty).map(
	              // open the boxes now that only full boxes are available (hopefully now failure)
	              skillBox => skillBox.openOrThrowException("Empty Box opened")).
	              // convert to a list again
	              toList
	              
	  def addNewSkillToItem(language: Locale, skillName:String, skillTeaser : String=null, skillDescription:String = null) : TheSkillType = {
	    val newSkill = theSkillObject.createNewEntity(language, title=skillName, teaser=skillTeaser,description=skillDescription)
          
          // create a link between the item and the new tag
          TheSkills.create.taggedItem(this).theSkill(newSkill).saveMe
	    newSkill
	  }


}

trait AddSkillsMeta[ModelType <: (AddSkills[ModelType]) ] extends BaseMetaEntity[ModelType] {
	self: ModelType =>
	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getSkillMapper :: super.getItemsToSchemify
	  	  
}
