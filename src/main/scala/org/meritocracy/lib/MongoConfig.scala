package org.meritocracy.lib

import net.liftweb._
import common._
import json._
import mongodb._
import util.Props
import net.liftweb.mongodb.{MongoDB , DefaultMongoIdentifier , MongoAddress , MongoHost }
import com.mongodb.ServerAddress
import com.mongodb.Mongo

object MongoConfig extends Loggable{
	def init(){
	   MongoDB.defineDb(
        DefaultMongoIdentifier,
        MongoAddress(MongoHost(), "meritocracy")
      )
	}
	def mongolab(){
	  val srvr = new ServerAddress(
       Props.get("mongo.host", "ds037637.mongolab.com"),
       Props.getInt("mongo.port", 37637)
    )
    MongoDB.defineDbAuth(DefaultMongoIdentifier, new Mongo(srvr), "meritocracy", "clog", "341414")
	}
}




