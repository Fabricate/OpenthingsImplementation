package at.fabricate.liftdev.common.snippet

import net.liftweb.http.SessionVar
import net.liftweb.mapper.MetaMapper
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import net.liftweb.mapper.Mapper
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.http.S

trait LazyLoginForSave[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]]  extends BaseEntityWithTitleAndDescriptionSnippet[T] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[T]{

  def checkIfUserCanSave : Boolean
  val loginLocation : String
  
   override def doSave[T <: Mapper[T]](item: T): Any = {
    if (checkIfUserCanSave){
      item.save
      unsavedContent.set(Empty)
     } else {
       item match {
         case anItem : ItemType => {
           unsavedContent.set(Full(anItem))
           S.redirectTo(loginLocation)
         }
         case _ => {
           //how is that possible?
           println("ERROR - NOT AN ITEMTYPE ELEMENT AND NOT LOGGED IN!!!")
         }
       }
       
       
     }
   }
  
}