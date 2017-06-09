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
        case "최정":
            return "CJ"
        case "헥터":
            return "Hector"
        default:
            print("!!!KUSFHKHFKSHFKSAHDKHDISHAD!*&#*!(^#(!&*^#(")
            return "CJ"
        }
    }

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
  
    // 투수 1등 사진
    @IBOutlet weak var PitcherImage: UIImageView!
    
    //투수 순위 테이블 뷰
    @IBOutlet weak var PitcherRankTableView: UITableView!
    
    // 투수 1등 기록(종류)
    @IBOutlet weak var filterName: UILabel!
    
    // 투수 1등 이름
    @IBOutlet weak var RankerName: UILabel!
    
    // 투수 1등 소속팀
    @IBOutlet weak var RankerTeam: UILabel!
    
    // 투수 1등 기록(숫자)
    @IBOutlet weak var RankerNum: UILabel!
    
    override func viewWillAppear(_ animated: Bool) {

        super.viewWillAppear(animated)
        
        var rank = 1
        
        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString
        print("devcice id!!! "+deviceID)
        
        if OtherKindButton == 0 {
            pitchers.sort(by: { $0.so! > $1.so!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
            filterName.text = "탈삼진"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].so!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))

        } else if OtherKindButton == 1 {
            pitchers.sort(by: { $0.era! < $1.era!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
            filterName.text = "평균자책"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].era!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))

        } else {
            pitchers.sort(by: { $0.win! > $1.win!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
            filterName.text = "탈삼진"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].win!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))

        }
        PitcherRankTableView.reloadData()
        
        let backgroundImage = #imageLiteral(resourceName: "background3")
        let imageView = UIImageView(image: backgroundImage)
        PitcherRankTableView.backgroundView = imageView



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
            filterName.text = "탈삼진"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].so!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))
            
        } else if OtherKindButton == 1 {
            pitchers.sort(by: { $0.era! < $1.era!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
            filterName.text = "평균자책"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].era!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))

        } else {
            pitchers.sort(by: { $0.win! > $1.win!})
            for i in 0..<pitchers.count {
                pitchers[i].rank=rank
                rank=rank+1
            }
            filterName.text = "탈삼진"
            RankerName.text = pitchers[0].name
            RankerTeam.text = pitchers[0].team
            RankerNum.text = String(pitchers[0].win!)
            PitcherImage.image = UIImage(named: mappingName(name: pitchers[0].name!))

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
        
        if OtherKindButton == 0 {
            OtherKindButton = 2
        }else if OtherKindButton == 1 {
            OtherKindButton = 0
        }else if OtherKindButton == 2 {
            OtherKindButton = 1
        }else {
            
        }
    
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
        return pitchers.count-1
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) ->
        UITableViewCell {
             let cell = PitcherRankTableView.dequeueReusableCell(withIdentifier: "pitcherRankCell", for: indexPath) as? PitcherRankTableViewCell
        let pitcher = pitchers[indexPath.row+1]
        cell?.PitcherName.text = pitcher.name
         
            if OtherKindButton == 0 {
                cell?.PitcherNum.text = String(pitcher.so!)
            } else if OtherKindButton == 1 {
                cell?.PitcherNum.text = String(pitcher.era!)

            } else if OtherKindButton == 2{
                cell?.PitcherNum.text = String(pitcher.win!) + "승"
            }
    
        cell?.PitcherTeam.text = pitcher.team
        cell?.PitcherRank.text = String(pitcher.rank!)
        
            cell?.backgroundColor = UIColor(white: 0, alpha: 0.2)

      
        return cell!
            
        
    }
}





