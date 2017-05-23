//
//  SearchView.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 29..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class SearchView: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate{
    
    // 검색 결과 화면
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var values:NSArray = []
    
    var searchstring:String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "검색결과"
        self.searchBar.delegate = self
        
        self.getFromJSON(search: self.searchstring)
    }
    
    // 검색바 클릭 시 결과 새로고침
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        getFromJSON(search: self.searchBar.text!)
        self.searchBar.text = ""
    }
    
    // 테이블 셀 클릭 시 그 마켓정보를 Detail화면 으로 전달
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "detailSegue1"{ // 배달 리스트 클릭 시
            let destination = segue.destination as? MarketDetail
            let dic = values[(tableView.indexPathForSelectedRow?.row)!] as! NSDictionary
            destination?.markettitle = dic["name"] as! String
            destination?.marketid = dic["id"] as! String
        }
        else if segue.identifier == "detailSegue2"{ // 생활정보 리스트 클릭 시
            let destination = segue.destination as? LifeDetail
            let dic = values[(tableView.indexPathForSelectedRow?.row)!] as! NSDictionary
            destination?.lifetitle = dic["name"] as! String
            destination?.lifeid = dic["id"] as! String
            
            // x, y 추출
            let address = dic["life_add_url"] as! String
            let xtmp = address.range(of: "&x=")
            let ytmp = address.range(of: "&y=")
            let tmp = address.range(of: "&enc=")
            
            destination?.xpoint = Double(address.substring(with: Range<String.Index>((xtmp?.upperBound)!..<(ytmp?.lowerBound)!)))!
            destination?.ypoint = Double(address.substring(with: Range<String.Index>((ytmp?.upperBound)!..<(tmp?.lowerBound)!)))!
        }
    }
    
    // db연결
    func getFromJSON(search: String){
        let url = NSURL(string: "http://222.239.250.218/delion/index.php/search/contents/")
        let request = NSMutableURLRequest(url: url! as URL)
        let param = ["name":search] as Dictionary<String, String>
        request.httpMethod = "POST"
        request.httpBody = try! JSONSerialization.data(withJSONObject: param, options: [])
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
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
                    DispatchQueue.main.sync {
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
    
    // 그 셀마다 들어가야 하는 것 배정
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let tmpcell = UITableViewCell()
        let maindata = values[indexPath.row] as! NSDictionary
        
        if maindata["state"] as? String == "0"{ // 배달 리스트일 경우
            let cell = self.tableView.dequeueReusableCell(withIdentifier: "searchmarketcell", for: indexPath) as! SearchMarketCell
            
            cell.marketName.text = maindata["name"] as? String
            cell.marketPlace.text = maindata["branch"] as? String
            if maindata["img"] as? String == "null"
            {
                switch maindata["category_id"] as! String { // 기본 썸네일 세팅
                case "1":
                    cell.marketPhoto.image = UIImage(named: "chicken_thumbnails.png")!
                    break
                case "2":
                    cell.marketPhoto.image = UIImage(named: "joongsik_thumbnails.png")!
                    break
                case "3":
                    cell.marketPhoto.image = UIImage(named: "pizza_thumbnails.png")!
                    break
                case "4":
                    cell.marketPhoto.image = UIImage(named: "hansik_thumbnails.png")!
                    break
                case "5":
                    cell.marketPhoto.image = UIImage(named: "bunsik_thumbnails.png")!
                    break
                case "6":
                    cell.marketPhoto.image = UIImage(named: "hamburger_thumbnails.png")!
                    break
                case "7":
                    cell.marketPhoto.image = UIImage(named: "jokbal_thumbnails.png")!
                    break
                default:
                    break
                }
            }
            else
            {
                let url = NSURL(string: "\(maindata["img"] as! String)")
                let imageData:NSData = NSData(contentsOf: url as! URL)!
                
                DispatchQueue.main.async {
                    let image = UIImage(data: imageData as Data)
                    cell.marketPhoto.image = image
                }
            }
            
            cell.callButton.setTitle("tel://\(maindata["phone"] as! String)", for: .normal)
            return cell
        }
        
        else if maindata["state"] as? String == "1"{ // 생활정보 리스트일 경우
            let cell = self.tableView.dequeueReusableCell(withIdentifier: "searchlifecell", for: indexPath) as! SearchLifeCell
            let maindata = values[indexPath.row] as! NSDictionary
            
            cell.lifeName.text = maindata["name"] as? String
            if maindata["img"] as? String == "null"
            {
                switch maindata["category_id"] as! String { // 기본 썸네일 세팅
                case "8":
                    cell.lifePhoto.image = UIImage(named: "laundry_thumbnails.png")!
                    break
                case "9":
                    cell.lifePhoto.image = UIImage(named: "convenience_thumbnails.png")!
                    break
                case "10":
                    cell.lifePhoto.image = UIImage(named: "drug_thumbnails.png")!
                    break
                case "11":
                    cell.lifePhoto.image = UIImage(named: "hospital_thumbnails.png")!
                    break
                case "12":
                    cell.lifePhoto.image = UIImage(named: "printer_thumbnails.png")!
                    break
                case "13":
                    cell.lifePhoto.image = UIImage(named: "mungu_thumbnails.png")!
                    break
                case "14":
                    cell.lifePhoto.image = UIImage(named: "bank_thumbnails.png")!
                    break
                case "15":
                    cell.lifePhoto.image = UIImage(named: "susun_thumbnails.png")!
                    break
                default:
                    break
                }
            }
            else
            {
                let url = NSURL(string: "\(maindata["img"] as! String)")
                let imageData:NSData = NSData(contentsOf: url as! URL)!
                
                DispatchQueue.main.async {
                    let image = UIImage(data: imageData as Data)
                    cell.lifePhoto.image = image
                }
            }
            
            cell.callButton.setTitle("tel://\(maindata["phone"] as! String)", for: .normal)
            
            return cell
        }
        
        return tmpcell
    }
    
    // 마켓 리스트 갯수만큼 출력
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.values.count
    }
    
    // 마켓 리스트 높이 지정
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
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
    
    @IBAction func callMarket2(sender: UIButton) {
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
                // Fallback on earlier versions
            }
        }
    }
    
}
