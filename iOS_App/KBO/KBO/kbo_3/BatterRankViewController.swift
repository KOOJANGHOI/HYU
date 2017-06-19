//
//  BatterRankViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class BatterRankViewController: UIViewController, UITableViewDelegate {
    
    func getFromJSON(){
        hitters.removeAll()
        let url = URL(string: urlStr_hitter)!
        let data = try! Data(contentsOf: url)
        //JSON PARSING
        if let result = try! JSONSerialization.jsonObject(with: data, options: []) as? [ [String:Any] ]
            
        {
            
            for one in result {
                var hitter = Hitter()
                guard let name = one["name"] as? String, let rate = one["rate"] as? Float,
                    let homerun = one["homerun"] as? Int, let team = one["team"] as? String,
                    let point = one["point"] as? Int else{
                        return
                }
                
                hitter.name = name
                hitter.rate = rate
                hitter.homerun = homerun
                hitter.team = team
                hitter.point = point
                
                hitters.append(hitter)
            }
        }
    }
   
    // 타자 순위 제어 변수
    var OtherKindButton:Int = 0
    
    //타자 순위 이름
    @IBOutlet weak var BatterRankKind: UILabel!
    
    //타자 사진
    @IBOutlet weak var BatterImage: UIImageView!
    
    //타자 순위 테이블 뷰
    @IBOutlet weak var BatterRankTableView: UITableView!
    
    override func viewWillAppear(_ animated: Bool) {
        var rank = 1

        super.viewWillAppear(animated)
        BatterRankTableView.reloadData()
        if OtherKindButton == 0 {
            hitters.sort(by: { $0.homerun! > $1.homerun!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        } else if OtherKindButton == 1 {
            hitters.sort(by: { $0.point! > $1.point!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        } else {
            hitters.sort(by: { $0.rate! > $1.rate!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        }
        
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.getFromJSON()
        
        var rank = 1

        BatterRankTableView.reloadData()
        if OtherKindButton == 0 {
            hitters.sort(by: { $0.homerun! > $1.homerun!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        } else if OtherKindButton == 1 {
            hitters.sort(by: { $0.point! > $1.point!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        } else {
            hitters.sort(by: { $0.rate! > $1.rate!})
            for i in 0..<hitters.count {
                hitters[i].rank=rank
                rank=rank+1
            }
        }

        OtherKindButton = 2

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //다른 순위 보기(이전)
    @IBAction func BatterRankKindButtonBefore(_ sender: Any) {
        OtherKindButton = (OtherKindButton-1)%3
        switch OtherKindButton {
        case 0:
            BatterRankKind.text = "홈런 순위"
        case 1:
            BatterRankKind.text = "타점 순위"
        case 2:
            BatterRankKind.text = "타율 순위"
        default:
            BatterRankKind.text = "오류"
        }
        self.viewWillAppear(true)

    }
    
    //다른 순위 보기(다음)
    @IBAction func BatterRankKindButtonAfter(_ sender: Any) {
        OtherKindButton = (OtherKindButton+1)%3
        switch OtherKindButton {
        case 0:
            BatterRankKind.text = "홈런 순위"
        case 1:
            BatterRankKind.text = "타점 순위"
        case 2:
            BatterRankKind.text = "타율 순위"
        default:
            BatterRankKind.text = "오류"
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

extension BatterRankViewController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return hitters.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->
        UITableViewCell {
            let cell = BatterRankTableView.dequeueReusableCell(withIdentifier: "batterRankCell", for: indexPath) as? BatterRankTableViewCell
            let hitter = hitters[indexPath.row]
            
            cell?.BatterName.text = hitter.name
            
            if OtherKindButton == 0 {
                cell?.BatterNum.text = String(hitter.homerun!)
            } else if OtherKindButton == 1 {
                cell?.BatterNum.text = String(hitter.point!)
                
            } else if OtherKindButton == 2{
                cell?.BatterNum.text = String(hitter.rate!)
            }
            
            cell?.BatterTeam.text = hitter.team
            cell?.BatterRank.text = String(hitter.rank!)
            return cell!
            
    }
}


