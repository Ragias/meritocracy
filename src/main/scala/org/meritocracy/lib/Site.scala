package org.meritocracy.lib
import net.liftweb._
import common._
import http.S
import sitemap._
import sitemap.Loc._
import net.liftmodules.mongoauth.Locs
import org.meritocracy.model._

case class MenuLoc(menu: Menu) {
  lazy val url: String = S.contextPath + menu.loc.calcDefaultHref
  lazy val fullUrl: String = S.hostAndPath + menu.loc.calcDefaultHref
}

object MenuGroups {
  val SettingsGroup = LocGroup("settings")
  val TopBarGroup = LocGroup("topbar")

}
object Site extends Locs {
  import MenuGroups._
  val home = MenuLoc(Menu("Home") / "index")

  val loginToken = MenuLoc(buildLoginTokenMenu)
  val logout = MenuLoc(buildLogoutMenu)
  private val profileParamMenu = Menu.param[User]("User", "Profile",
    User.findByUsername _,
    _.username.is) / "user" >> Loc.CalcValue(() => User.currentUser)

  lazy val profileLoc = profileParamMenu.toLoc
  val password = MenuLoc(Menu.i("Password") / "settings" / "password" >> RequireLoggedIn >> SettingsGroup)
  val account = MenuLoc(Menu.i("Account") / "settings" / "account" >> SettingsGroup >> RequireLoggedIn)
  val editProfile = MenuLoc(Menu("EditProfile", "Profile") / "settings" / "profile" >> SettingsGroup >> RequireLoggedIn)

  val login = MenuLoc(Menu("Login")  / "login" >> RequireNotLoggedIn)
  val register = MenuLoc(Menu("Register")  / "register" >> RequireNotLoggedIn >> TopBarGroup)

  private val messagesParam = Menu.param[UserMessageBase]("Messages", "Message",
    s => UserMessageBase.find(s), ub => ub.id.is.toString) / "messages";

  val messagesLoc = messagesParam.toLoc
  
  val listOfUsers = MenuLoc(Menu.i("List of users")/"users" >> RequireLoggedIn)
  val messenger = MenuLoc(Menu.i("Messenger")/ "messenger" >> RequireLoggedIn)
  private def menu = List(home.menu,
    login.menu,
    register.menu,
    loginToken.menu,
    logout.menu,
    profileParamMenu,
    password.menu,
    account.menu,
    editProfile.menu,
    messagesParam,
    listOfUsers.menu,
    messenger.menu
    )

  def siteMap = SiteMap(menu: _*)
}