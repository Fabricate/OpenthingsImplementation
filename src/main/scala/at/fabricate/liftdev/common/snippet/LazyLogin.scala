package at.fabricate.liftdev.common.snippet

import net.liftweb.http.SessionVar
import net.liftweb.mapper.MetaMapper
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import net.liftweb.mapper.Mapper
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.http.S
import at.fabricate.openthings.model.User
import at.fabricate.liftdev.common.model.AddCreatedByUser
import at.fabricate.liftdev.common.model.AddCreatedByUserMeta
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.MetaMegaProtoUser
import at.fabricate.liftdev.common.model.BaseEntity
import net.liftweb.common.Box

trait LazyLoginForSave[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T] with AddCreatedByUser[T] ]  extends BaseEntityWithTitleAndDescriptionSnippet[T] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[T]{
  
  def checkIfUserCanSave[U <: Mapper[U]](item: U) : Boolean
  val loginLocation : String
 
  
   override def doSave[U <: Mapper[U]](item: U): Any = {
    if (checkIfUserCanSave(item)){
      item match {
         case anItem : ItemType  => { 
           // TODO: This should go to the AddCreatedBySnippet
           if (anItem.createdByUser.isEmpty)
        	   anItem.createdByUser(anItem.theUserObject.currentUser.openOrThrowException("Empty Box opened").primaryKeyField.get)
           anItem.save
         }
         case _ => // too bad we can not set the account link now!
           	item.save
      }
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