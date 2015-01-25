package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.MappedLocale
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedTextarea
import net.liftweb.mapper.KeyedMapper

//     trait TheGenericTranslation[TheTranslatedItem <: Mapper[TheTranslatedItem],
//                                 TheTranslation <: TheGenericTranslation[TheTranslatedItem,TheTranslation]] 
//                                		 extends BaseEntity[TheGenericTranslation[TheTranslatedItem,TheTranslation]] with LongKeyedMapper[TheGenericTranslation[TheTranslatedItem,TheTranslation]] with IdPK with EnsureUniqueTextFields[TheGenericTranslation[TheTranslatedItem,TheTranslation]]  {
     trait TheGenericTranslation {
      //extends BaseEntity[TheGenericTranslation] with LongKeyedMapper[TheGenericTranslation] with IdPK with EnsureUniqueTextFields[TheGenericTranslation]  {
	//self: TheTranslation =>
//    	  def getSingleton = TheTranslationMeta
		  //val metaItemToTranslate : BaseMetaEntity[T]//KeyedMetaMapper[Long,T]
		  type TheTranslationType <: Mapper[TheTranslationType]
		  type TheTranslatedItem <: KeyedMapper[Long,TheTranslatedItem]
//		  val titleLength : Int
//		  val teaserLength : Int
//		  val descriptionLength : Int
	    	  
	      val translatedItem : MappedLongForeignKey[TheTranslationType,TheTranslatedItem] // extends MappedLongForeignKey(this,metaItemToTranslate)
    	  
		  val language : MappedLocale[TheTranslationType]
    	  val title : MappedString[TheTranslationType]
    	  val teaser : MappedTextarea[TheTranslationType]
    	  val description : MappedTextarea[TheTranslationType]
		  
//    	  object language extends MappedLocale(this)//Language(this)
//    	  
//    	  object title extends MappedString(this, titleLength)
//    	  object teaser extends MappedTextarea(this, teaserLength)
//    	  object description extends MappedTextarea(this, descriptionLength)
    	  
    	  //TODO: implement that one fully!
    	  //object prettyURL extends MappedString(this, prettyURLLength)
    	  
    	  
//    	   override type TheUniqueTextType = TheGenericTranslation[TheTranslatedItem,TheTranslation]
//
//    	   override def theUniqueFields = List(language) //, prettyURL)
    	  
//	  	  object title extends MappedString(this, 80){    	    
//    	    override def validations = FieldValidation.minLength(this,5) _ :: Nil
//    	  }
//		  object author extends MappedString(this, 40){
//    	    override def validations = FieldValidation.minLength(this,3) _ :: Nil
//    	    override def defaultValue = getCurrentUser.map(user => "%s %s".format(user.firstName, user.lastName )) openOr("")
//    	  }
//		  object comment extends MappedString(this, 140){		    
//    	    override def validations = FieldValidation.minLength(this,10)) _ :: Nil
//		  }
		  
	}