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
import at.fabricate.liftdev.common.lib.MappedLanguage

     trait TheGenericTranslation {


		  type TheTranslationType <: Mapper[TheTranslationType]
		  type TheTranslatedItem <: KeyedMapper[Long,TheTranslatedItem]

	    	  
	      val translatedItem : MappedLongForeignKey[TheTranslationType,TheTranslatedItem] 
    	  
		  val language : MappedLanguage[TheTranslationType]
    	  val title : MappedString[TheTranslationType]
    	  val teaser : MappedTextarea[TheTranslationType]
    	  val description : MappedTextarea[TheTranslationType]
		  
		  
	}