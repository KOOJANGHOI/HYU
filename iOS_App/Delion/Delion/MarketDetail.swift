//
//  MarketDetail.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 18..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class MarketDetail: UIViewController, UITableViewDataSource, UITableViewDelegate{
    
    // 배달 디테일 화면 (db연동)
    
    @IBOutlet weak var tableView: UITableView! // 메뉴 표
    @IBOutlet weak var marketTime: UILabel! // 마켓영업시간
    @IBOutlet weak var marketPhone: UILabel! // 마켓전화번호
    @IBOutlet weak var callButton: UIButton! // 전화버튼
    
    var selectedIndexPath: IndexPath? = nil
    var values:NSArray = []
    
    var titles:[String] = []
    var items:[[String]] = [[]]
    var prices:[[String]] = [[]]
    var titlenum = 0
    
    var markettitle:String = ""
    var marketid:String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = markettitle
        tableView.layer.borderWidth = 3
        tableView.layer.borderColor = UIColor(red: 245/255, green: 122/255, blue: 98/255, alpha: 1.0).cgColor
        self.marketTime.adjustsFontSizeToFitWidth = true
        
        self.getFromJSON()
    }
    
    // db연결
    func getFromJSON(){
        let url = NSURL(string: "http://222.239.250.218/delion/index.php/shop/shop_menu/\(marketid)")
        let request = NSMutableURLRequest(url: url! as URL)
        
        let task = URLSession.shared.dataTask(with: request as URLRequest){data,response,error in
            guard error == nil && data != nil else
            {
                print("Error:", error as Any)
                return
            }
            
            let httpStatus = response as? HTTPURLResponse
            
            if httpStatus?.statusCode == 200
            {
                if data?.count != 0
                {
                    self.values = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSArray
                    let setting = self.values[0] as! NSDictionary
                    
                    DispatchQueue.main.sync {
                        self.marketTime.text = "영업시간 : \(setting["opentime"] as! String)"
                        self.marketPhone.text = "tel : \(setting["phone"] as! String)"
                        self.callButton.setTitle("tel://\(setting["phone"] as! String)", for: .normal)
                        
                        self.titles.append(setting["extender_menu"] as! String)
                        self.items[self.titlenum].append(setting["menu"] as! String)
                        self.prices[self.titlenum].append("\(setting["price"] as! String)원")
                        
                        for i in 1 ..< self.values.count {
                            let dic = self.values[i] as! NSDictionary
                            if dic["extender_menu"] as! String == self.titles[self.titlenum] //같은 section일 경우
                            {
                                self.items[self.titlenum].append(dic["menu"] as! String)
                                self.prices[self.titlenum].append("\(dic["price"] as! String)원")
                            }
                            else // 다른 section일 경우
                            {
                                self.titlenum += 1
                                self.titles.append(dic["extender_menu"] as! String)
                                self.items.append([dic["menu"] as! String])
                                self.prices.append(["\(dic["price"] as! String)원"])
                            }
                        }
                        self.tableView.reloadData()
                    }
                }
            }
            else
            {
                print("error httpstatus code is :", httpStatus!.statusCode)
            }
        }
        task.resume()
    }
    
    // 그 메뉴 이름 셀마다 들어가야 하는 것 배정
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "menucell", for: indexPath) as! MenuCell
        
        cell.menuName.text = self.items[indexPath.section][indexPath.row]
        cell.menuPrice.text = self.prices[indexPath.section][indexPath.row]
        
        cell.clipsToBounds = true
        
        return cell
    }
    
    // 섹션(메뉴 종류) 이름 지정
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return self.titles[section]
    }
    
    // 섹션(메뉴 종류) 갯수만큼 출력
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.titles.count
    }
    
    // 셀(메뉴 이름) 갯수만큼 출력
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.items[section].count
    }
    
    // 섹션(메뉴 종류) 셀 사이즈 지정
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 30
    }

    // 셀(메뉴 이름) 셀 사이즈 지정
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 30
    }
    
    // 섹션(메뉴 종류) 셀 폰트 사이즈 지정
    func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        let header: UITableViewHeaderFooterView = view as! UITableViewHeaderFooterView
        header.textLabel?.font = UIFont.boldSystemFont(ofSize: 15)
    }
    
    // 전화버튼 클릭 시
    @IBAction func callMarket(sender: UIButton) {
        if sender.currentTitle == "tel://"{ // 전화번호가 없다면
            let alertController = UIAlertController(title: "알림", message: "위 매장은 전화번호가 없습니다.", preferredStyle: UIAlertControllerStyle.alert)
            let okAction = UIAlertAction(title: "확인", style: .default, handler: nil)
            alertController.addAction(okAction)
            self.present(alertController, animated: true, completion: nil)
        }
        else{ // 전화번호가 있다면 연결
            let phoneURL = NSURL(string: sender.currentTitle!)
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(phoneURL as! URL, options: [:], completionHandler: nil)
            } else {
                UIApplication.shared.openURL(phoneURL as! URL)
            }
        }
    }
}
