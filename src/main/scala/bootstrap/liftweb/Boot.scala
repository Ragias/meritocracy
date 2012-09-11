package bootstrap.liftweb

import net.liftweb._
import http.{LiftRules, NotFoundAsTemplate, ParsePath , Req , Html5Properties}
import sitemap.{SiteMap, Menu, Loc}
import util.{ NamedPF }
import net.liftweb._
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.{Props}
import common.{Full}
import http.{S}
import org.meritocracy.model._
import _root_.net.liftweb.sitemap.Loc._
import org.meritocracy.lib._
import org.meritocracy.model._
import net.liftmodules.mongoauth._


class Boot {
  def boot {
   MongoConfig.mongolab()
    MongoAuth.authUserMeta.default.set(User)
    MongoAuth.siteName.default.set("$clog$")

    MongoAuth.systemEmail.default.set(SystemUser.user.email.is)
    MongoAuth.systemUsername.default.set(SystemUser.user.name.is)

    // where to search snippet
    LiftRules.addToPackages("org.meritocracy")

   
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
        // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() =>User.isLoggedIn)
    
    LiftRules.setSiteMap(Site.siteMap)
    
    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
   
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

  }
}