//
//  PitcherRankViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class PitcherRankViewController: UIViewController, UITableViewDelegate {
    var queue : OperationQueue!

    func getFromJSON(){
        pitchers.removeAll()
        let url = URL(string: urlStr_pitcher)!
        let data = try! Data(contentsOf: url)
        //JSON PARSING
        if let result = try! JSONSerialization.jsonObject(with: data, options: []) as? [ [String:Any] ]
        {
            
            for one in result {
                var pitcher = Pitcher()
                guard let name = one["name"] as? String, let team = one["team"] as? String,
                    let era = one["era"] as? Float, let win = one["win"] as? Int,
                    let so = one["so"] as? Int else{
                        return
                }
                
                pitcher.name = name
                pitcher.team = team
                pitcher.era = era
                pitcher.win = win
                pitcher.so = so
                
                pitchers.append(pitcher)
            }
        }
    }
    
    
    // 투수 순위 제어 변수
    var OtherKindButton:Int = 0
    
    //투수 순위 이름
    @IBOutlet weak var PitcherRankKind: UILabel!
    
    //투수 사진
    @IBOutlet weak var PitcherImage: UIImageView!
    
    //투수 순위 테이블 뷰
    @IBOutlet weak var PitcherRankTableView: UITableView!
    

    override func viewWillAppear(_ animated: Bool) {

        super.viewWillAppear(animated)
        
        var rank = 1
        
        if OtherKindButton == 0 {
            pitchers.sort(by: { $0.so! > $1.so!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        } else if OtherKindButton == 1 {
            pitchers.sort(by: { $0.era! < $1.era!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        } else {
            pitchers.sort(by: { $0.win! > $1.win!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        }
        PitcherRankTableView.reloadData()
        


    }
    
    override func viewDidLoad() {

        super.viewDidLoad()
        self.getFromJSON()
        
        var rank = 1
        
        if OtherKindButton == 0 {
            pitchers.sort(by: { $0.so! > $1.so!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        } else if OtherKindButton == 1 {
            pitchers.sort(by: { $0.era! < $1.era!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        } else {
            pitchers.sort(by: { $0.win! > $1.win!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
        }
        PitcherRankTableView.reloadData()
        
        PitcherRankTableView.dataSource = self
        PitcherRankTableView.delegate = self
        // 3으로 초기화
        OtherKindButton = 2
        // Do any additional setup after loading the view.
        
     

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //다른 순위 보기(이전)
    @IBAction func PitcherRankKindButtonBefore(_ sender: Any) {
        OtherKindButton = (OtherKindButton-1)%3
        switch OtherKindButton {
        case 0:
            PitcherRankKind.text = "탈삼진 순위"
        case 1:
            PitcherRankKind.text = "평균자책 순위"
        case 2:
            PitcherRankKind.text = "다승 순위"
        default:
            PitcherRankKind.text = "오류"
        }
        self.viewWillAppear(true)

    }
    
    //다른 순위 보기(다음)
    @IBAction func PitcherRankKindButtonAfter(_ sender: Any) {
        OtherKindButton = (OtherKindButton+1)%3
        switch OtherKindButton {
        case 0:
            PitcherRankKind.text = "탈삼진 순위"
        case 1:
            PitcherRankKind.text = "평균자책 순위"
        case 2:
            PitcherRankKind.text = "다승 순위"
        default:
            PitcherRankKind.text = "오류"
        }
        self.viewWillAppear(true)
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

extension PitcherRankViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return pitchers.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->
        UITableViewCell {
             let cell = PitcherRankTableView.dequeueReusableCell(withIdentifier: "pitcherRankCell", for: indexPath) as? PitcherRankTableViewCell
        let pitcher = pitchers[indexPath.row]
        
        cell?.PitcherName.text = pitcher.name
         
            if OtherKindButton == 0 {
                cell?.PitcherNum.text = String(pitcher.so!)
            } else if OtherKindButton == 1 {
                cell?.PitcherNum.text = String(pitcher.era!)

            } else if OtherKindButton == 2{
                cell?.PitcherNum.text = String(pitcher.win!)
            }
    
        cell?.PitcherTeam.text = pitcher.team
        cell?.PitcherRank.text = String(pitcher.rank!)
        
        return cell!
            
        
    }
}





