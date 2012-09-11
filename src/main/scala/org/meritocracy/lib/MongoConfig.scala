package org.meritocracy.lib

import net.liftweb._
import common._
import json._
import mongodb._
import util.Props
import net.liftweb.mongodb.{MongoDB , DefaultMongoIdentifier , MongoAddress , MongoHost }

object MongoConfig extends Loggable{
	def init(){
	   MongoDB.defineDb(
        DefaultMongoIdentifier,
        MongoAddress(MongoHost(), "meritocracy")
      )
	}
}




