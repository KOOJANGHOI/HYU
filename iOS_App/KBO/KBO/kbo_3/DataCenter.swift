//
//  DataHouse.swift
//  NSURLExample
//
//  Created by KIMTAEHO on 2017. 5. 20..
//  Copyright © 2017년 KIMTAEHO. All rights reserved.
//

import Foundation

let urlStr_hitter = "http://127.0.0.1:8000/hitters/"
let urlStr_pitcher = "http://127.0.0.1:8000/pitchers/"
let urlStr_team = "http://127.0.0.1:8000/teams/"

struct Player {
    var id:Int?
    var hit:Int?
    var number:Int?
}

struct Hitter {
    var name:String?
    var rate:Float?
    var homerun:Int?
    var team:String?
    var point:Int?
    var rank:Int?

}

struct Pitcher {
    var name:String?
    var team:String?
    var era:Float?
    var win:Int?
    var so:Int?
    var rank:Int?
}

struct Team {
    var name:String?
    var win:Int?
    var draw:Int?
    var lose:Int?
    var diff:Float?
    var rate:Float?
    var rank:Int?
    
}
var players = [Player]()

var hitters = [Hitter]()
var pitchers = [Pitcher]()
var teams = [Team]()


