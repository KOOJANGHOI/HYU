//
//  DataHouse.swift
//  NSURLExample
//
//  Created by KIMTAEHO on 2017. 5. 20..
//  Copyright © 2017년 KIMTAEHO. All rights reserved.
//

import Foundation

let baseip = "http://127.0.0.1:8000/"
let urlStr_hitter = baseip+"hitters/"
let urlStr_pitcher = baseip+"pitchers/"
let urlStr_team = baseip+"teams/"
let urlStr_schedule = baseip+"schedules/"
let urlStr_autheticate = baseip+"authenticate/"
let urlStr_predict = baseip+"prediction/"
let urlStr_entry = baseip+"entry/?username="

//let urlStr_hitter = "http://127.0.0.1:8000/hitters?username="
//let urlStr_pitcher = "http://127.0.0.1:8000/pitchers?username="
//let urlStr_team = "http://127.0.0.1:8000/teams?username="
//let urlStr_schedule = "http://127.0.0.1:8000/schedules/"
//let urlStr_autheticate = "http://127.0.0.1:8000/authenticate/?username="
//let urlStr_predict = "http://127.0.0.1:8000/prediction/"
//let urlStr_entry = "http://127.0.0.1:8000/entry/?username="


let poo:String = "구장회 똥냄새 지림"

var ticketNumber = 0
var ticketback = ""

func mappingName(name: String)->String{
    switch name {
    case "피어밴드":
        return "Feierabend"
    case "박세웅":
        return "PSW"
    case "임기영":
        return "LGY"
    case "양현종":
        return "YHZ"
    case "켈리":
        return "Kelly"
    case "차우찬":
        return "CWC"
    case "이대호":
        return "LDH"
    case "윤석민":
        return "YSM"
    case "서건창":
        return "SGC"
    case "최형우":
        return "CHW"
    case "한동민":
        return "HDM"
    case "나성범":
        return "NSB"
    default:
        return ""
    }
}

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

struct Schedule {
    
    var away_team:String?
    var home_team:String?
    var away_score:Int?
    var home_score:Int?
    var time:String?
    var day:String?
    var stadium:String?
    var state:Int?
    var gameId:Int?
    
}
var schedules = [Schedule]()
var week = [[Schedule](),[Schedule](),[Schedule](),[Schedule](),[Schedule](),[Schedule](),[Schedule]()]

var calendar = [Schedule]()

var players = [Player]()

var hitters = [Hitter]()
var pitchers = [Pitcher]()
var teams = [Team]()



