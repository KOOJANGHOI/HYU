//
//  Tab_2_ViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 5..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class Tab_2_ViewController: UIViewController, UITableViewDelegate {
  
    func getFromJSON(){
        teams.removeAll()
        let url = URL(string: urlStr_team)!
        let data = try! Data(contentsOf: url)
        //JSON PARSING
        if let result = try! JSONSerialization.jsonObject(with: data, options: []) as? [ [String:Any] ]
        {
            for one in result {
                var team = Team()
                guard let name = one["name"] as? String, let win = one["win"] as? Int,
                    let draw = one["draw"] as? Int, let lose = one["lose"] as? Int,
                    let diff = one["diff"] as? Float, let rate = one["rate"] as? Float else {
                        return
                }
                
                team.name = name
                team.win = win
                team.draw = draw
                team.lose = lose
                team.diff = diff
                team.rate = rate
                
                teams.append(team)
            }
            
        }
    }

    
    // 구단순위 테이블 뷰
    @IBOutlet weak var RankTableView: UITableView!
    
    
    
    override func viewWillAppear(_ animated: Bool) {
        //super.viewWillAppear(animated)
        //RankTableView.reloadData()
        let backgroundImage = #imageLiteral(resourceName: "background3")
        let imageView = UIImageView(image: backgroundImage)
        RankTableView.backgroundView = imageView
   
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.getFromJSON()
        var rank = 1
        
        teams.sort(by: { $0.diff! < $1.diff!})
        for i in 0..<teams.count {
            teams[i].rank = rank
            rank = rank+1
        }
        for i in 0..<teams.count-1 {
            
            if teams[i].diff == teams[i+1].diff {
                if teams[i].rate! < teams[i+1].rate! {
                    let tmp = teams[i]
                    teams[i]=teams[i+1]
                    teams[i+1]=tmp
                    teams[i].rank = teams[i+1].rank
                    teams[i+1].rank = teams[i+1].rank!+1
                }
            }
            
        }
        
        RankTableView.reloadData()
        
        RankTableView.dataSource = self
        RankTableView.delegate = self
        // Do any additional setup after loading the view.
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


extension Tab_2_ViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return teams.count+1
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->
        UITableViewCell {
            let cell = RankTableView.dequeueReusableCell(withIdentifier: "teamRankCell", for: indexPath) as? teamRankTableViewCell
            
             cell?.backgroundColor = UIColor(white: 0, alpha: 0.2)
            
            if indexPath.row == 0 {
                cell?.TeamLogo.image = nil
                cell?.TeamName.text = "팀명"
                cell?.WinNum.text = "승"
                cell?.LoseNum.text = "패"
                cell?.DrawNum.text = "무"
                cell?.GameNum.text = "경기"
                cell?.WinRate.text = "게임차"
                cell?.TeamRank.text = "순위"
            } else {
                let team = teams[indexPath.row-1]
                
                guard let win = team.win , let lose = team.lose, let draw = team.draw else {
                    return cell!
                }
                cell?.TeamName.text = team.name
                switch team.name! {
                case "삼성":
                    cell?.TeamLogo.image = #imageLiteral(resourceName: "SAMSUNG")
                case "넥센":
                    cell?.TeamLogo.image = #imageLiteral(resourceName: "NEXEN")
                case "두산":
                    cell?.TeamLogo.image = #imageLiteral(resourceName: "DOOSAN")
                case "롯데":
                    cell?.TeamLogo.image = #imageLiteral(resourceName: "LOTTE")
                case "한화":
                    cell?.TeamLogo.image = #imageLiteral(resourceName: "HANHWA")
                default:
                    cell?.TeamLogo.image = UIImage(named: team.name!)

                }
                //cell?.TeamLogo.image = UIImage(named: team.name!)
                
                cell?.WinNum.text = String(team.win!)
                cell?.LoseNum.text = String(team.lose!)
                cell?.DrawNum.text = String(team.draw!)
                let sum = win + lose + draw
                cell?.GameNum.text = String(sum)
                cell?.WinRate.text = String(team.diff!)
                
                cell?.TeamRank.text = String(team.rank!)
            }
            return cell!
            
    }
}
