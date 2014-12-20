package at.fabricate.model

import net.liftweb.mapper.MetaMegaProtoUser
import net.liftweb.http.SessionVar
import net.liftweb.http.S
import net.liftweb.proto.{ProtoUser => GenProtoUser}
import net.liftweb.mapper.ProtoUser


// has to be mixed in to the meta object
trait RedirectAfterLogin[T <: ProtoUser[T] ] extends GenProtoUser with ProtoUser[T] {
  self: T => 
  // referrer after login
  object loginReferrer extends SessionVar("/")
  
  final override def homePage = {
    var ret = loginReferrer.get
    loginReferrer.remove()
    ret
  }
  
  final override def login = {
    println("login performed")
    for (r <- S.referer if loginReferrer.get == "/") loginReferrer.set(r)
    println("redirect to "+loginReferrer.get)
    super.login
  }

}