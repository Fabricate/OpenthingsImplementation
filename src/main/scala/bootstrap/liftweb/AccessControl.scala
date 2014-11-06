package bootstrap.liftweb

import at.fabricate.model.User
import net.liftweb.sitemap.Loc._
import net.liftweb.http.RedirectResponse

object AccessControl {
  /** Zugriffsbedingung: Benutzer ist eingeloggt. */
  //val userIsLoggedIn = If(() => 
  //  User.currentUser.isDefined, "Sie sind nicht eingeloggt.")
  val loggedIn = If(() => User.loggedIn_?,
                  () => RedirectResponse("/login"))
  /*
  /** Zugriffsbedingung: Eingeloggter Benutzer hat Rolle User.Teacher */
  val IfIsTeacher = If(() => User.loggedInAs(User.Teacher), "Sie sind keine Lehrkraft!")

  /** Zugriffsbedingung: Eingeloggter Benutzer hat Rolle User.Student */
  val IfIsStudent = If(() => User.loggedInAs(User.Student), "Sie sind kein Student!")

  /** Zugriffsbedingung: Eingeloggter Benutzer hat Rolle User.Teacher oder Rolle User.Student,
   * im Gegensatz zu Admin. */
  val IfIsUser = If(() => User.loggedInAs(User.Student, User.Teacher),
    "Sie sind weder Student noch Lehrkraft!"
  )
  
  /** Zugriffsbedingung: Eingeloggter Benutzer hat Rolle User.Admin */
  val IfIsAdmin	  = If(() => User.loggedInAs(User.Admin), "Sie sind kein Administrator!")

* 
*/
}