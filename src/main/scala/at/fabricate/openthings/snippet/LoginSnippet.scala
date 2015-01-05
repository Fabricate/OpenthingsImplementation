package at.fabricate.openthings
package snippet

import model.User
import at.fabricate.liftdev.common.snippet.CustomizeUserHandlingSnippet

object LoginSnippet extends CustomizeUserHandlingSnippet[User](User,UserSnippet){

    override def loginTitle = "Custom Login"
    override def logoutTitle = "Custom Logout"      
    override def signUpTitle = "Custom Sign up"          
    override def lostPasswordTitle = "Custom Lost password"      
    override def resetPasswordTitle = "Custom Reset password"      

}
