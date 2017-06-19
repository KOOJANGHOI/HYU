//
//  Tab_1_ViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 5..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class Tab_1_ViewController: UIViewController, UITableViewDelegate{

    var valueToPass:Int = 0
    var index:Int = 3
    let date = Date()
    let ago1 = Calendar.current.date(byAdding: .day, value: -1, to: Date())
    let ago2 = Calendar.current.date(byAdding: .day, value: -2, to: Date())
    let ago3 = Calendar.current.date(byAdding: .day, value: -3, to: Date())
    let tomorrow1 = Calendar.current.date(byAdding: .day, value: 1, to: Date())
    let tomorrow2 = Calendar.current.date(byAdding: .day, value: 2, to: Date())
    let tomorrow3 = Calendar.current.date(byAdding: .day, value: 3, to: Date())
    
    let dateFormatter = DateFormatter()
    let dateFormatter2 = DateFormatter()
    
    var dateString:String=""
    var dateString_ago1:String=""
    var dateString_ago2:String=""
    var dateString_ago3:String=""
    var dateString_tomorrow1:String=""
    var dateString_tomorrow2:String=""
    var dateString_tomorrow3:String=""
    
    func getFromJSON(){
        
        let url = URL(string: urlStr_schedule)!
        
        let data = try! Data(contentsOf: url)

        let deviceID = UIDevice.current.identifierForVendor!.uuidString
        let authenticate = URL(string: urlStr_autheticate+"?username="+deviceID)!

        let task = URLSession.shared.dataTask(with: authenticate as URL) { data, response, error in
            
            guard let data = data, error == nil else { return }
            
            let str = NSString(data: data, encoding: String.Encoding.utf8.rawValue)!
            print((str as String) + " test ")
            ticketNumber = Int(str as String)!

        }
        
        task.resume()

        //JSON PARSING
        if let result = try! JSONSerialization.jsonObject(with: data, options: []) as? [ [String:Any] ]
        {
            
            for one in result {
                
                var schedule = Schedule()
                
                let state = one["state"] as? Int
                let time = one["time"] as? String
                let day = one["day"] as? String
                let home_team = one["home_team"] as? String
                let home_score = one["home_score"] as? Int
                let away_team=one["away_team"] as? String
                let away_score=one["away_score"] as? Int
                let stadium = one["stadium"] as? String
                let gameId = one["game_id"] as? Int
                
                schedule.state = state
                schedule.time = time
                schedule.day = day
                schedule.home_team = home_team
                schedule.home_score = home_score
                schedule.away_team = away_team
                schedule.away_score = away_score
                schedule.stadium = stadium
                schedule.gameId = gameId

                if dateString_ago3==day {
                    week[0].append(schedule)
                }
                if dateString_ago2==day {
                    week[1].append(schedule)
                }
                if dateString_ago1==day {
                    week[2].append(schedule)
                }
                if dateString==day {
                    week[3].append(schedule)
                }
                if dateString_tomorrow1==day {
                    week[4].append(schedule)
                }
                if dateString_tomorrow2==day {
                    week[5].append(schedule)
                }
                if dateString_tomorrow3==day {
                    week[6].append(schedule)
                }
                schedules.append(schedule)

            }
        }
    }
    
    // 날짜 정보
    @IBOutlet weak var DayInfo: UILabel!
    // 이전날짜 버튼 클릭시
    @IBAction func DayBeforeButton(_ sender: Any) {
        
        if(index == 0) {
            index = 6
        }else if(index == 1) {
            index = 0
        }else if(index == 2) {
            index = 1
        }else if(index == 3) {
            index = 2
        }else if(index == 4) {
            index = 3
        }else if(index == 5) {
            index = 4
        }else if(index == 6) {
            index = 5
        }
        
        switch index {
        case 0: DayInfo.text = dateFormatter2.string(from: ago3! as Date)
            
        case 1: DayInfo.text = dateFormatter2.string(from: ago2! as Date)
            
        case 2: DayInfo.text = dateFormatter2.string(from: ago1! as Date)
            
        case 3: DayInfo.text = dateFormatter2.string(from: date as Date)
            
        case 4: DayInfo.text = dateFormatter2.string(from: tomorrow1! as Date)
            
        case 5: DayInfo.text = dateFormatter2.string(from: tomorrow2! as Date)
            
        case 6: DayInfo.text = dateFormatter2.string(from: tomorrow3! as Date)
            
        default: DayInfo.text = "오류"
        }
        self.viewWillAppear(true)
        
    }
    // 다음날짜 버튼 클릭시
    @IBAction func DayAfterButton(_ sender: Any) {
        index = (index+1)%7
        switch index {
        case 0: DayInfo.text = dateFormatter2.string(from: ago3! as Date)
        case 1: DayInfo.text = dateFormatter2.string(from: ago2! as Date)
        case 2: DayInfo.text = dateFormatter2.string(from: ago1! as Date)
        case 3: DayInfo.text = dateFormatter2.string(from: date as Date)
        case 4: DayInfo.text = dateFormatter2.string(from: tomorrow1! as Date)
        case 5: DayInfo.text = dateFormatter2.string(from: tomorrow2! as Date)
        case 6: DayInfo.text = dateFormatter2.string(from: tomorrow3! as Date)
        default: DayInfo.text = "오류"
        }
        self.viewWillAppear(true)
    }
    // 일정/결과 테이블 뷰
    @IBOutlet weak var ScheduleTableView: UITableView!
    
    @IBOutlet weak var GameTime: UILabel!       // 경기시간
    
    //원정팀 정보
    @IBOutlet weak var AwayTeamName: UILabel!
    @IBOutlet weak var AwayTeamPitcherStatus: UILabel!
    @IBOutlet weak var AwayTeamPitcherName: UILabel!
    @IBOutlet weak var AwayTeamScore: UILabel!
    
    //홈팀 정보
    @IBOutlet weak var HomeTeamScore: UILabel!
    @IBOutlet weak var HomeTeamName: UILabel!
    @IBOutlet weak var HomeTeamPitcherStatus: UILabel!
    @IBOutlet weak var HomeTeamPitcherName: UILabel!
    
    @IBOutlet weak var GamePlace: UILabel!      // 경기 장소
  
    override func viewDidLoad() {
        super.viewDidLoad()
        index = 3
       
        dateFormatter.dateFormat = "MM.dd"
        dateFormatter2.dateFormat = "MM.dd (EEE)"
        
        dateString = dateFormatter.string(from:date as Date)
        dateString_ago1 = dateFormatter.string(from:ago1! as Date)
        dateString_ago2 = dateFormatter.string(from:ago2! as Date)
        dateString_ago3 = dateFormatter.string(from:ago3! as Date)
        dateString_tomorrow1 = dateFormatter.string(from:tomorrow1! as Date)
        dateString_tomorrow2 = dateFormatter.string(from:tomorrow2! as Date)
        dateString_tomorrow3 = dateFormatter.string(from:tomorrow3! as Date)

        DayInfo.text = dateFormatter2.string(from: date as Date)
        
        self.getFromJSON()
        
        ScheduleTableView.reloadData()
        
        ScheduleTableView.dataSource = self
        ScheduleTableView.delegate = self
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        ScheduleTableView.reloadData()
        
        let backgroundImage = #imageLiteral(resourceName: "background3")
        let imageView = UIImageView(image: backgroundImage)
        ScheduleTableView.backgroundView = imageView
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}

extension Tab_1_ViewController: UITableViewDataSource{
    
   
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return week[index].count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->
        UITableViewCell {
            let cell = ScheduleTableView.dequeueReusableCell(withIdentifier: "scheduleCell", for: indexPath) as? ScheduleTableViewCell
            cell?.backgroundColor = UIColor(white: 0, alpha: 0.2)
            cell?.HomeTeamScore.textColor = UIColor.white
            cell?.AwayTeamScore.textColor = UIColor.white

            // let schedule = schedules[indexPath.row]
            let home:Int
            let away:Int
            
            
            let schedule = week[index][indexPath.row]
            cell?.HomeTeamName.text = schedule.home_team
            if schedule.home_score != nil {
                cell?.HomeTeamScore.text = String(schedule.home_score!)
                home=schedule.home_score!
            } else {
                cell?.HomeTeamScore.text = String("0")
                home=0
            }
            cell?.AwayTeamName.text = schedule.away_team
            if schedule.away_score != nil {
                cell?.AwayTeamScore.text = String(schedule.away_score!)
                away=schedule.away_score!
            } else {
                cell?.AwayTeamScore.text = String("0")
                away=0
            }
            
            
            if away>home {
                cell?.AwayTeamScore.textColor=UIColor.red
            } else if away<home {
                cell?.HomeTeamScore.textColor=UIColor.red
            }
            //cell?.GameTime.text = schedule.time
            //cell?.GamePlace.text = schedule.stadium
            
            switch schedule.away_team! {
            case "삼성":
                cell?.AwayTeamLogo.image = #imageLiteral(resourceName: "SAMSUNG")
            case "넥센":
                cell?.AwayTeamLogo.image = #imageLiteral(resourceName: "NEXEN")
            case "두산":
                cell?.AwayTeamLogo.image = #imageLiteral(resourceName: "DOOSAN")
            case "롯데":
                cell?.AwayTeamLogo.image = #imageLiteral(resourceName: "LOTTE")
            case "한화":
                cell?.AwayTeamLogo.image = #imageLiteral(resourceName: "HANHWA")
            default:
                cell?.AwayTeamLogo.image = UIImage(named: schedule.away_team!)
                
            }
            switch schedule.home_team! {
            case "삼성":
                cell?.HomeTeamLogo.image = #imageLiteral(resourceName: "SAMSUNG")
            case "넥센":
                cell?.HomeTeamLogo.image = #imageLiteral(resourceName: "NEXEN")
            case "두산":
                cell?.HomeTeamLogo.image = #imageLiteral(resourceName: "DOOSAN")
            case "롯데":
                cell?.HomeTeamLogo.image = #imageLiteral(resourceName: "LOTTE")
            case "한화":
                cell?.HomeTeamLogo.image = #imageLiteral(resourceName: "HANHWA")
            default:
                cell?.HomeTeamLogo.image = UIImage(named: schedule.home_team!)
                
            }
            
            return cell!
        
    }
    
    func tableView(tableView: UITableView!, didSelectRowAtIndexPath indexPath: NSIndexPath!) {
        print("You selected cell #\(indexPath.row)!")
        
        let indexPath = ScheduleTableView.indexPathForSelectedRow;
        
        let schedule = week[index][(indexPath?.row)!]

        print("game id = "+String(schedule.gameId!))
        
        valueToPass = schedule.gameId!

        performSegue(withIdentifier: "predictSegug", sender: self)
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? PredictViewController {
            let cell = sender as! UITableViewCell
            let selectedRow = ScheduleTableView.indexPath(for: cell)!.row

            let schedule = week[index][(selectedRow)]

            destination.gameNum = schedule.gameId!
        }
    }
    
    
    
}
 



