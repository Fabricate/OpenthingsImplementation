package at.fabricate.openthings
package snippet

import model.User
import at.fabricate.liftdev.common.snippet.CustomizeUserHandlingSnippet

object LoginSnippet extends CustomizeUserHandlingSnippet[User](User,UserSnippet){

}
