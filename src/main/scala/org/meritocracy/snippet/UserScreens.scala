package org.meritocracy.snippet

import org.meritocracy.model._
import org.meritocracy.lib._
import scala.xml._
import net.liftweb._
import common._
import http.{ LiftScreen, S }
import util.FieldError
import util.Helpers._

trait BaseScreen extends LiftScreen {
  override val cancelButton = super.cancelButton % ("class" -> "btn") % ("tabindex" -> "1")
  override val finishButton = super.finishButton % ("class" -> "btn btn-primary") % ("tabindex" -> "1")

  override def additionalAttributes: MetaData = {
    val cssCls = new UnprefixedAttribute("class", "form-horizontal", Null)
    cssCls.append(super.additionalAttributes)
  }

  def displayOnly(fieldName: => String, html: NodeSeq) =
    new Field {
      type ValueType = String
      override def name = fieldName
      override implicit def manifest = buildIt[String]
      override def default = ""
      override def toForm: Box[NodeSeq] = Full(html)
    }
}
/*
* Use for editing the currently logged in user only.
*/
sealed trait BaseCurrentUserScreen extends BaseScreen {
  object userVar extends ScreenVar(User.currentUser.open_!)

  override def localSetup {
    Referer(Site.account.url)
  }
}

object AccountScreen extends BaseCurrentUserScreen {
  addFields(() => userVar.is.accountScreenFields)

  def finish() {
    userVar.is.save
    S.notice("Account settings saved")
  }
}

sealed trait BasePasswordScreen {
  this: LiftScreen =>

  def pwdName: String = "Password"
  def pwdMinLength: Int = 6
  def pwdMaxLength: Int = 32

  val passwordField = password(pwdName, "", trim,
    valMinLen(pwdMinLength, "Password must be at least " + pwdMinLength + " characters"),
    valMaxLen(pwdMaxLength, "Password must be " + pwdMaxLength + " characters or less"),
    ("tabindex" -> "1"))
  val confirmPasswordField = password("Confirm Password", "", trim, ("tabindex" -> "1"))

  def passwordsMustMatch(): Errors = {
    if (passwordField.is != confirmPasswordField.is)
      List(FieldError(confirmPasswordField, "Passwords must match"))
    else Nil
  }
}

object PasswordScreen extends BaseCurrentUserScreen with BasePasswordScreen {
  override def pwdName = "New Password"
  override def validations = passwordsMustMatch _ :: super.validations

  def finish() {
    userVar.is.password(passwordField.is)
    userVar.is.password.hashIt
    userVar.is.save
    S.notice("New password saved")
  }
}

/*
* Use for editing the currently logged in user only.
*/
object ProfileScreen extends BaseCurrentUserScreen {

  addFields(() => userVar.is.profileScreenFields)

  val day = super.select("Day", 1, 1 to 30)
  val month = super.select("Month", 1, 1 to 12)
  val year = super.select("Year", 1988, 1960 to 2000)
  val sex = super.select("Sex", "Male", Seq("Male", "Female"))
  
  def finish() {
    import java.util.{ Calendar, GregorianCalendar }
    val birthday: Calendar = new java.util.GregorianCalendar()
    birthday.set(Calendar.YEAR, year.toInt)
    birthday.set(Calendar.MONTH, month.toInt - 1)
    birthday.set(Calendar.DAY_OF_MONTH, day.toInt)

    userVar.birthday(birthday.getTime())
    if (sex.equals("Male")) {
      userVar.sex(User.Sex.Male)
    } else {
      userVar.sex(User.Sex.Female)
    }
    
   
    userVar.is.save

    S.notice("Profile settings saved")
  }
}

// this is needed to keep these fields and the password fields in the proper order
trait BaseRegisterScreen extends BaseScreen {
  object userVar extends ScreenVar(User.regUser.is)

  addFields(() => userVar.is.registerScreenFields)
}

/*
* Use for creating a new user.
*/
object RegisterScreen extends BaseRegisterScreen with BasePasswordScreen {
  override def validations = passwordsMustMatch _ :: super.validations

  val rememberMe = builder("", User.loginCredentials.is.isRememberMe, ("tabindex" -> "1"))
    .help(Text("Remember me when I come back later."))
    .make

  override def localSetup {
    Referer(Site.home.url)
  }

  def finish() {
    val user = userVar.is
    user.password(passwordField.is)
    user.password.hashIt
    user.save
    User.logUserIn(user, true)
    if (rememberMe) User.createExtSession(user.id.is)
    S.notice("Thanks for signing up!")
  }
}