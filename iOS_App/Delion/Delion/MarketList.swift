//
//  MarketList.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 18..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit
import WebKit

class MarketList : UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate{
    
    // 배달 리스트 화면 (db연동)
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var values:NSArray = []
    var defaultimg:UIImage = UIImage(named: "delion.png")!
    var shoptype = 0
    
    var markettype:String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = markettype// 버튼 이름 (마켓 종류)
        self.searchBar.delegate = self
        
        switch markettype { // db링크 설정때문에
        case "치킨":
            shoptype = 1
            defaultimg = UIImage(named: "chicken_thumbnails.png")!
            break
        case "중식":
            shoptype = 2
            defaultimg = UIImage(named: "joongsik_thumbnails.png")!
            break
        case "피자":
            shoptype = 3
            defaultimg = UIImage(named: "pizza_thumbnails.png")!
            break
        case "한식":
            shoptype = 4
            defaultimg = UIImage(named: "hansik_thumbnails.png")!
            break
        case "분식":
            shoptype = 5
            defaultimg = UIImage(named: "bunsik_thumbnails.png")!
            break
        case "패스트푸드":
            shoptype = 6
            defaultimg = UIImage(named: "hamburger_thumbnails.png")!
            break
        case "족발":
            shoptype = 7
            defaultimg = UIImage(named: "jokbal_thumbnails.png")!
            break
        default: break
        }
        
        self.getFromJSON()
    }
    
    // 검색바 클릭 시 searchSegue 활성화
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        performSegue(withIdentifier: "searchSegue", sender: nil)
        self.searchBar.text = ""
    }
    
    // 테이블 셀 클릭 시 그 마켓정보를 MarketDetail으로 전달
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "detailSegue"{
            let destination = segue.destination as? MarketDetail
            let dic = values[(tableView.indexPathForSelectedRow?.row)!] as! NSDictionary
            destination?.markettitle = dic["name"] as! String
            destination?.marketid = dic["id"] as! String
        }
        
        // searchSegue 활성화 시, 검색 스트링 넘김
        else if segue.identifier == "searchSegue"{
            let searchresult = segue.destination as? SearchView
            searchresult?.searchstring = self.searchBar.text!
        }
    }
    
    // db연결
    func getFromJSON(){
        let url = NSURL(string: "http://222.239.250.218/delion/index.php/shop/shop_list/\(shoptype)")
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
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "marketcell", for: indexPath) as! MarketCell
        let maindata = values[indexPath.row] as! NSDictionary

        cell.marketName.text = maindata["name"] as? String
        cell.marketPlace.text = maindata["branch"] as? String
        if maindata["img"] as? String == "null"
        {
            cell.marketPhoto.image = defaultimg
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
}
