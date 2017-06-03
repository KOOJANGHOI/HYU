//
//  LifeDetail.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 18..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit
import MapKit

class LifeDetail : UIViewController{
    
    // 생활정보 디테일 화면 (db연동)
    
    @IBOutlet weak var lifeMap: MKMapView! // 구글지도
    @IBOutlet weak var lifeTime: UILabel! // 마켓영업시간
    @IBOutlet weak var lifePhone: UILabel! // 마켓전화번호
    @IBOutlet weak var lifeAddress: UILabel! // 마켓주소
    @IBOutlet weak var callButton: UIButton! // 전화버튼
    
    var values:NSArray = []
    
    var lifetitle:String = ""
    var lifeid:String = ""
    var xpoint:Double = 0.0
    var ypoint:Double = 0.0
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = lifetitle
        lifeMap.layer.borderWidth = 3
        lifeMap.layer.borderColor = UIColor(red: 149/255, green: 205/255, blue: 219/255, alpha: 1.0).cgColor
        lifeAddress.layer.borderWidth = 3
        lifeAddress.layer.borderColor = UIColor(red: 149/255, green: 205/255, blue: 219/255, alpha: 1.0).cgColor
        self.lifeTime.adjustsFontSizeToFitWidth = true
        self.lifeAddress.adjustsFontSizeToFitWidth = true
        
        // 지도구현
        let span:MKCoordinateSpan = MKCoordinateSpanMake(0.01, 0.01)
        let location:CLLocationCoordinate2D = CLLocationCoordinate2DMake(ypoint as CLLocationDegrees, xpoint as CLLocationDegrees)
        let region:MKCoordinateRegion = MKCoordinateRegionMake(location, span)
        lifeMap.setRegion(region, animated: true)
        
        let annotation = MKPointAnnotation()
        
        annotation.coordinate = location
        annotation.title = lifetitle
        lifeMap.addAnnotation(annotation)
        
        
        self.getFromJSON()
    }
    
    // db연결
    func getFromJSON(){
        let url = NSURL(string: "http://222.239.250.218/delion/index.php/lifeinfo/life_detail/\(lifeid)")
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
                        self.lifeTime.text = "영업시간 : \(setting["opentime"] as! String)"
                        self.lifePhone.text = "tel : \(setting["phone"] as! String)"
                        self.lifeAddress.text = setting["life_add"] as? String
                        self.callButton.setTitle("tel://\(setting["phone"] as! String)", for: .normal)
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
