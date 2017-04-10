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
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.By
import net.liftweb.util.FieldError
import at.fabricate.liftdev.common.lib.MappedLanguage
import scala.collection.mutable
import at.fabricate.openthings.model.User
import at.fabricate.liftdev.common.lib.FieldValidation

// This is the basic database entity 
// every db object should inherit from that
// specially needed for tags and skills that dont have an icon
// TODO: maybe even tags and skills want to have an icon?

trait BaseEntityWithTitleAndDescription [T <: (BaseEntityWithTitleAndDescription[T]) ] extends BaseEntity[T] with MatchByID[T] with CreatedUpdated with OneToMany[Long, T]
{
  self: T =>
        
    val titleLength = 60
    val titleMinLength = 5
  //TODO: solve the problem with validation on registration! //final
    val titleValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.minLength(TheTranslationMeta.title,titleMinLength) _ ,
      FieldValidation.maxLength(TheTranslationMeta.title,titleLength) _ )
    val teaserLength = 150
    val teaserMinLength = 10
    val teaserValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.minLength(TheTranslationMeta.teaser,teaserMinLength) _ ,
      FieldValidation.maxLength(TheTranslationMeta.teaser,teaserLength) _ )
    val descriptionLength = 60000
    val descriptionMinLength = 10
    val descriptionValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.minLength(TheTranslationMeta.description,descriptionMinLength) _ ,
      FieldValidation.maxLength(TheTranslationMeta.description,descriptionLength) _ )

    val userHasToBeLoggedInForSave = true;
   
    object defaultTranslation extends MappedLongForeignKey(this, TheTranslationMeta){
    	override def validSelectValues =
    			Full(TheTranslationMeta.findMap(
    					By(TheTranslationMeta.translatedItem,self.primaryKeyField.get),
    					OrderBy(TheTranslationMeta.id, Ascending)){
    					case t: TheTranslation => Full(t.id.get -> "%s (%s)".format(t.title,t.language.isAsLocale.getDisplayLanguage))
    			})
    	override def dbNotNull_? = true
    	def getObjectOrHead : self.TheTranslation = obj.getOrElse(self.translations.head)
    }
    
    object translationToSave extends MappedLongForeignKey(this, TheTranslationMeta)

	def getTranslationMapper : LongKeyedMetaMapper[_] = TheTranslationMeta
	
	  object translations extends MappedOneToMany(TheTranslationMeta, TheTranslationMeta.translatedItem) with Owned[TheTranslation]
with Cascade[TheTranslation]
	
      
	class TheTranslation extends BaseEntity[TheTranslation] with LongKeyedMapper[TheTranslation] with IdPK with TheGenericTranslation {
	  
		  type TheTranslationType = TheTranslation
		  type TheTranslatedItem = T
	  
    	  def getSingleton = TheTranslationMeta
    	  
    	  object translatedItem extends MappedLongForeignKey(this,self.getSingleton)

    	  object language extends MappedLanguage(this)
    	  
    	  object title extends MappedString(this, titleLength){
		    override def validations = titleValidations.toList
		  }
    	  object teaser extends MappedTextarea(this, teaserLength){
		    override def validations = teaserValidations.toList
		  }
    	  object description extends MappedTextarea(this, descriptionLength){
		    override def validations = descriptionValidations.toList
		  }
    	  
	}

	
	object TheTranslationMeta  extends TheTranslation with BaseMetaEntity[TheTranslation] with LongKeyedMetaMapper[TheTranslation] {
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_translation"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheTranslation
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}
	// some convenience methodes to do things in 3 different states (although state notFound might not occur because of notNull)
	def doWithTranslationFor[V](languageExpected:Locale)(foundAction : TheGenericTranslation => V)(defaultTranslationAction : TheGenericTranslation => V)(notFound : V) : V = 
	  getTranslationForItem(languageExpected) match {
                 case Full(translationFound) => foundAction(translationFound)
                 case _ => this.defaultTranslation.obj match {
                   case Full(defaultTranslationFound) => defaultTranslationAction(defaultTranslationFound)
                   case _ => notFound
                 }
	  }
	
	val shortTitleTranslationFound = "%s"
	val shortTitleDefaultTranslationFound = "%s (no translation found, using %s)"
	val noTranslationFound = "No translation information for this item found"
	
	def doDefaultWithTranslationFor(expectedLanguage:Locale) : String = doWithTranslationFor(expectedLanguage)(
           translationFound => shortTitleTranslationFound.format(translationFound.title.get)
           )(
           defaultTranslation => shortTitleDefaultTranslationFound.format(defaultTranslation.title.get,defaultTranslation.language.isAsLocale.getDisplayLanguage)
           )(noTranslationFound)
	
	def getTranslationForItem(language : Locale) : Box[TheTranslation] = this.translations.find(_.language.isAsLocale.getLanguage() == language.getLanguage())

	def getNewTranslation(language : Locale) : TheTranslation = {
	  val translation = this.TheTranslationMeta.create.language(language.toString).translatedItem(this)
	  //this.translations += translation
	  this.translationToSave(translation)
	  translation
	}
	
	lazy val theEmptyTranslation = this.TheTranslationMeta.create.language("en_EN").translatedItem(this).title("NO TRANSLATION FOUND").description("NO TRANSLATION FOUND").teaser("NO TRANSLATION FOUND")

    // special save for MySQL - enforces saving of translation before the entity can be saved 
    abstract override def save = {	  
	    if (!userHasToBeLoggedInForSave || (userHasToBeLoggedInForSave && User.loggedIn_?)){
        // save the default translation
        this.defaultTranslation.obj.map(_.save)
        // save the unsaved translation
        this.translationToSave.obj.map(aTranslation => {
          aTranslation.save
          if (this.translations.find(_.id == aTranslation.id)==Empty)
            this.translations += aTranslation
        }
            )
        // save all the other translations
        //this.translations.map(_.save)
        super.save
	    }
	    else {
	      println("user not logged in")
	      false
	    }
    }
  // validate also all the translations
  abstract override def validate = {
    println("validating translations too")
    var results = List[FieldError]()
    // save the default translation
    this.defaultTranslation.obj.map(translation => results = translation.validate ::: results)
    /* will not work atm
    // validate the unsaved translation
    this.translationToSave.obj.map(aTranslation => {
      aTranslation.validate
    })
    // validate all the other translations
    //this.translations.map(_.validate)
    */
    super.validate ::: results
  }
}

trait BaseMetaEntityWithTitleAndDescription[ModelType <: ( BaseEntityWithTitleAndDescription[ModelType]) ] extends BaseMetaEntity[ModelType] with CreatedUpdated
{
    self: ModelType  =>
      	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getTranslationMapper :: super.getItemsToSchemify
	// some handy and secure create and save methodes
	def createNewEntity(language : Locale,title : String = null, teaser : String = null, description: String = null) : ModelType = {
	      val newItem = this.create
          // create a new translation with the submitted language
          val translation = newItem.TheTranslationMeta.create.language(language.toString)//.saveMe
          translation.title(title).teaser(teaser).description(description).translatedItem(newItem)//.save
          // append the new translation to the translations
          newItem.translations += translation
          // make this translation the default
          newItem.defaultTranslation(translation)//.saveMe
	}
}
