//
//  LifeList.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 18..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class LifeList : UIViewController, UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate{
    
    // 생활정보 리스트 화면 (db연동)
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var values:NSArray = []
    var defaultimg:UIImage = UIImage(named: "delion.png")!
    var shoptype = 0
    
    var lifetype:String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = lifetype// 버튼 이름 (마켓 종류)
        self.searchBar.delegate = self
        
        switch lifetype { // db링크 설정때문에
        case "세탁소":
            shoptype = 8
            defaultimg = UIImage(named: "laundry_thumbnails.png")!
            break
        case "편의점":
            shoptype = 9
            defaultimg = UIImage(named: "convenience_thumbnails.png")!
            break
        case "약국":
            shoptype = 10
            defaultimg = UIImage(named: "drug_thumbnails.png")!
            break
        case "병원":
            shoptype = 11
            defaultimg = UIImage(named: "hospital_thumbnails.png")!
            break
        case "인쇄소":
            shoptype = 12
            defaultimg = UIImage(named: "printer_thumbnails.png")!
            break
        case "문구점":
            shoptype = 13
            defaultimg = UIImage(named: "mungu_thumbnails.png")!
            break
        case "은행/ATM":
            shoptype = 14
            defaultimg = UIImage(named: "bank_thumbnails.png")!
            break
        case "의류수선":
            shoptype = 15
            defaultimg = UIImage(named: "susun_thumbnails.png")!
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
    
    // 테이블 셀 클릭 시 그 마켓정보를 ListDetail으로 전달
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "detailSegue"{
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
            
        // searchSegue 활성화 시, 검색 스트링 넘김
        else if segue.identifier == "searchSegue"{
            let searchresult = segue.destination as? SearchView
            searchresult?.searchstring = self.searchBar.text!
        }
    }
    
    
    // db연결
    func getFromJSON(){
        let url = NSURL(string: "http://222.239.250.218/delion/index.php/lifeinfo/life_list/\(shoptype)")
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
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "lifecell", for: indexPath) as! LifeCell
        let maindata = values[indexPath.row] as! NSDictionary
        
        cell.lifeName.text = maindata["name"] as? String
        if maindata["img"] as? String == "null"
        {
            cell.lifePhoto.image = defaultimg
            
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
