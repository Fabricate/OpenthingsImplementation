package at.fabricate.openthings
package lib

import model.Image
import net.liftweb.mapper.By
import net.liftweb.http._
import net.liftweb.common._
import model.User
import net.liftweb.mapper.By
import net.liftweb.util.BasicTypesHelpers._
import net.liftweb.util._

object ImageLogic {
  
  object UserImage{
    def unapply(in: String): Option[User] =
    		User.find(By(User.id, in.toInt ))
  }
  
    def matcher: LiftRules.DispatchPF = {
    case r @ Req("serve" :: "userimage" :: UserImage(user) ::
                 Nil, _, GetRequest) => () => Full(InMemoryResponse(user.icon.get,
                               List("Content-Type" -> "image/jpeg",
                                    "Content-Length" ->
                                    user.icon.get.length.toString), Nil, 200))  
  }
    
}
