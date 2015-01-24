package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedTextarea
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.OneToMany
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.MappedLocale
import net.liftweb.mapper.BaseMetaMapper
import java.util.Locale
import net.liftweb.common.Full
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.LongMappedMapper
import net.liftweb.mapper.By

// This is the basic database entity 
// every db object should inherit from that
// specially needed for tags and skills that dont have an icon
// TODO: maybe even tags and skills want to have an icon?

trait BaseEntityWithTitleAndDescription [T <: (BaseEntityWithTitleAndDescription[T]) ] extends BaseEntity[T] with MatchByID[T] with CreatedUpdated with OneToMany[Long, T]
{
  self: T =>
    
//    def defaultLocales : List[Locale] = List(defaultTranslation)
    
    val titleLength = 60
    val teaserLength = 150
    val descriptionLength = 2000
    val prettyURLLength = 30
    
//	object title extends MappedString(this, titleLength)
//	object teaser extends MappedTextarea(this, teaserLength)
//	object description extends MappedTextarea(this, descriptionLength)
    
    // USE WITH CAUTION !!!
    def getTranslationForLocale(locale: Locale) : Box[TheTranslation] = translations.find(_.localeField == locale) 
    
    def getTranslationForLocales(locales : List[Locale], default : TheTranslation) : TheTranslation = //getTranslationForLocale()
    	locales match {
      case Nil => default
      case locale :: Nil if getTranslationForLocale(locale) != Empty => getTranslationForLocale(locale).open_!
      case locale :: Nil => default
      case locale :: otherLocales if getTranslationForLocale(locale) != Empty => getTranslationForLocale(locale).open_!
      case locale :: otherLocales => getTranslationForLocales(otherLocales,default)
    }
    
//    match {
//      case Full(translation) => Full(translation)
//      case _ => getTranslationForLocale( // improve that: 1. userDefault (set in defaultLocalse as first item), then itemDefault, then first in translation list
//      case _ => TheTranslation.create.translatedItem(self).language(defaultTranslation.get).saveMe
//    }
//    
    
//  // only tags is using them atm, TODO: remove as quick as you can
    def getTranslationOrDefaultOrCreate(locale: Locale) : TheTranslation = getTranslationForLocale(locale)
    match {
      case Full(translation) => translation
      case _ => defaultTranslation.getObjectOrHead
//      case _ if (translations.length > 0) => translations.head // improve that: 1. userDefault (set in defaultLocalse as first item), then itemDefault, then first in translation list
//      case _ => TheTranslation.create.translatedItem(self).language(defaultTranslation.get).saveMe
    }
    def title(locale:Locale) = getTranslationOrDefaultOrCreate(locale).title
    def teaser(locale:Locale) = getTranslationOrDefaultOrCreate(locale).teaser
    def description(locale:Locale) = getTranslationOrDefaultOrCreate(locale).description
    
    object defaultTranslation extends LongMappedMapper(this, TheTranslation){
    	override def validSelectValues =
    			Full(TheTranslation.findMap(
    					By(TheTranslation.translatedItem,self.primaryKeyField),
    					OrderBy(TheTranslation.id, Ascending)){
    					case t: TheTranslation => Full(t.id.get -> "%s (%s)".format(t.title,t.language.isAsLocale.getDisplayLanguage))
    			})
    	override def dbNotNull_? = true
    	def getObjectOrHead : self.TheTranslation = obj.getOrElse(self.translations.head)
    } 
//    {
//      //override def dbNotNull_? = true
//      //override def defaultValue = "en"
//    }
    
    // TODO: remove all above this line as soon as possible!
    
    
    
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
	// add Tag, Tool, ...

	// TOD: Clean up that mess!
	// getItemsToSchemify comes from the AddComment trait
	
	def getTranslationMapper : LongKeyedMetaMapper[_] = TheTranslation
	
	  object translations extends MappedOneToMany(TheTranslation, TheTranslation.translatedItem) with Owned[TheTranslation]
with Cascade[TheTranslation]
//	, OrderBy(TheTranslation.primaryKeyField, Ascending)

	  //def getItemsToSchemify = List(TheComment, T)
      
      class TheTranslation extends BaseEntity[TheTranslation] with LongKeyedMapper[TheTranslation] with IdPK with EnsureUniqueTextFields[TheTranslation]  {
    	  def getSingleton = TheTranslation
	    	  
	      object translatedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  
    	  object language extends MappedLocale(this)//Language(this)
    	  
    	  object title extends MappedString(this, titleLength)
    	  object teaser extends MappedTextarea(this, teaserLength)
    	  object description extends MappedTextarea(this, descriptionLength)
    	  
    	  //TODO: implement that one fully!
    	  //object prettyURL extends MappedString(this, prettyURLLength)
    	  
    	  
    	   override type TheUniqueTextType = TheTranslation

    	   override def theUniqueFields = List(language) //, prettyURL)
    	  
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
	
	object TheTranslation  extends TheTranslation with BaseMetaEntity[TheTranslation] with LongKeyedMetaMapper[TheTranslation]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_translation"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheTranslation
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}
}

trait BaseMetaEntityWithTitleAndDescription[ModelType <: ( BaseEntityWithTitleAndDescription[ModelType]) ] extends BaseMetaEntity[ModelType] with CreatedUpdated
{
    self: ModelType  =>
      	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getTranslationMapper :: super.getItemsToSchemify

}
